# partitionStateMachine

partitionStateMachine是controller模块中的分区状态机，主要负责分区在online,offline,new,non_existent几种状态间进行转化。分区状态机主要负责对于分区进行了副本领导的选举，将无副本的分区下线，更新controller的上下文，发送数据给各个代理节点更新等一系列事物。下面将根据partitionStateMachine中的一系列函数进行说明

#### startUp()

```java
 def startup() {
    info("Initializing partition state")
    initializePartitionState()
    info("Triggering online partition state changes")
    triggerOnlinePartitionStateChange()
    info(s"Started partition state machine with initial state -> $partitionState")
  }
```

startUp函数的主要功能

* init()判断分区的主副本是否存在，若存在则为online，不存在为offline
* triggerOnlinePartitionStateChange()将所有的分区状态变为新建或离线(除非分区要被删除)
* 以上的操作进行判断的前提，均从controller的上下文中获得

#### handleStateChanges()

```java
def handleStateChanges(partitions: Seq[TopicPartition], targetState: PartitionState,
                         partitionLeaderElectionStrategyOpt: Option[PartitionLeaderElectionStrategy] = None): Unit = {
    if (partitions.nonEmpty) {
      try {
        controllerBrokerRequestBatch.newBatch()
        doHandleStateChanges(partitions, targetState, partitionLeaderElectionStrategyOpt)
        controllerBrokerRequestBatch.sendRequestsToBrokers(controllerContext.epoch)
      } catch {
        case e: Throwable => error(s"Error while moving some partitions to $targetState state", e)
      }
    }
  }
```

handleStateChanges()首先调用BrokerRequestBatch(ControllerChannelManager中的类)来建立与各个代理节点之间的通道，在中间调用dohandleStateChanges()函数

#### doHandleStateChanges()

```java
private def doHandleStateChanges(partitions: Seq[TopicPartition], targetState: PartitionState,
                           partitionLeaderElectionStrategyOpt: Option[PartitionLeaderElectionStrategy]): Unit = {
    val stateChangeLog = stateChangeLogger.withControllerEpoch(controllerContext.epoch)
    partitions.foreach(partition => partitionState.getOrElseUpdate(partition, NonExistentPartition))
    val (validPartitions, invalidPartitions) = partitions.partition(partition => isValidTransition(partition, targetState))
    invalidPartitions.foreach(partition => logInvalidTransition(partition, targetState))
    targetState match {
      case NewPartition =>
        validPartitions.foreach { partition =>
          stateChangeLog.trace(s"Changed partition $partition state from ${partitionState(partition)} to $targetState with " +
            s"assigned replicas ${controllerContext.partitionReplicaAssignment(partition).mkString(",")}")
          partitionState.put(partition, NewPartition)
        }
      case OnlinePartition =>
        val uninitializedPartitions = validPartitions.filter(partition => partitionState(partition) == NewPartition)
        val partitionsToElectLeader = validPartitions.filter(partition => partitionState(partition) == OfflinePartition || partitionState(partition) == OnlinePartition)
        if (uninitializedPartitions.nonEmpty) {
          val successfulInitializations = initializeLeaderAndIsrForPartitions(uninitializedPartitions)
          successfulInitializations.foreach { partition =>
            stateChangeLog.trace(s"Changed partition $partition from ${partitionState(partition)} to $targetState with state " +
              s"${controllerContext.partitionLeadershipInfo(partition).leaderAndIsr}")
            partitionState.put(partition, OnlinePartition)
          }
        }
        if (partitionsToElectLeader.nonEmpty) {
          val successfulElections = electLeaderForPartitions(partitionsToElectLeader, partitionLeaderElectionStrategyOpt.get)
          successfulElections.foreach { partition =>
            stateChangeLog.trace(s"Changed partition $partition from ${partitionState(partition)} to $targetState with state " +
              s"${controllerContext.partitionLeadershipInfo(partition).leaderAndIsr}")
            partitionState.put(partition, OnlinePartition)
          }
        }
      case OfflinePartition =>
        validPartitions.foreach { partition =>
          stateChangeLog.trace(s"Changed partition $partition state from ${partitionState(partition)} to $targetState")
          partitionState.put(partition, OfflinePartition)
        }
      case NonExistentPartition =>
        validPartitions.foreach { partition =>
          stateChangeLog.trace(s"Changed partition $partition state from ${partitionState(partition)} to $targetState")
          partitionState.put(partition, NonExistentPartition)
        }
    }
  }
```

doHandleStateChanges()步骤

1. 首先根据传进的partitions信息分出validPartitions和invalidPartitions；对于valid的partition如果目标是转成new，offline，nonexist都可以直接进行；若要转成online，则应根据该分区是否是新建而进行领导的选举，选举成功之后才可以转成online状态
2. 在要转为online的分区中，分为uninitializedPartitions和partitionsForLeader，分别对应新建未选举过的分区和已经经历过选举的分区；对于uninitializedPartitions调用initializedLeaderAndIsrForPartitions()，对于partitionsForLeader调用electLeaderForPartitions()函数

#### initializeLeaderAndIsrForPartitions(partitions: Seq[TopicPartition])

```java
private def initializeLeaderAndIsrForPartitions(partitions: Seq[TopicPartition]): Seq[TopicPartition] = {
    val successfulInitializations = mutable.Buffer.empty[TopicPartition]
    val replicasPerPartition = partitions.map(partition => partition -> controllerContext.partitionReplicaAssignment(partition))
    val liveReplicasPerPartition = replicasPerPartition.map { case (partition, replicas) =>
        val liveReplicasForPartition = replicas.filter(replica => controllerContext.isReplicaOnline(replica, partition))
        partition -> liveReplicasForPartition
    }
    val (partitionsWithoutLiveReplicas, partitionsWithLiveReplicas) = liveReplicasPerPartition.partition { case (_, liveReplicas) => liveReplicas.isEmpty }

    partitionsWithoutLiveReplicas.foreach { case (partition, replicas) =>
      val failMsg = s"Controller $controllerId epoch ${controllerContext.epoch} encountered error during state change of " +
        s"partition $partition from New to Online, assigned replicas are " +
        s"[${replicas.mkString(",")}], live brokers are [${controllerContext.liveBrokerIds}]. No assigned " +
        "replica is alive."
      logFailedStateChange(partition, NewPartition, OnlinePartition, new StateChangeFailedException(failMsg))
    }
    val leaderIsrAndControllerEpochs = partitionsWithLiveReplicas.map { case (partition, liveReplicas) =>
      val leaderAndIsr = LeaderAndIsr(liveReplicas.head, liveReplicas.toList)
      val leaderIsrAndControllerEpoch = LeaderIsrAndControllerEpoch(leaderAndIsr, controllerContext.epoch)
      partition -> leaderIsrAndControllerEpoch
    }.toMap
    val createResponses = try {
      zkClient.createTopicPartitionStatesRaw(leaderIsrAndControllerEpochs)
    } catch {
      case e: Exception =>
        partitionsWithLiveReplicas.foreach { case (partition,_) => logFailedStateChange(partition, partitionState(partition), NewPartition, e) }
        Seq.empty
    }
    createResponses.foreach { createResponse =>
      val code = createResponse.resultCode
      val partition = createResponse.ctx.get.asInstanceOf[TopicPartition]
      val leaderIsrAndControllerEpoch = leaderIsrAndControllerEpochs(partition)
      if (code == Code.OK) {
        controllerContext.partitionLeadershipInfo.put(partition, leaderIsrAndControllerEpoch)
        controllerBrokerRequestBatch.addLeaderAndIsrRequestForBrokers(leaderIsrAndControllerEpoch.leaderAndIsr.isr,
          partition, leaderIsrAndControllerEpoch, controllerContext.partitionReplicaAssignment(partition), isNew = true)
        successfulInitializations += partition
      } else {
        logFailedStateChange(partition, NewPartition, OnlinePartition, code)
      }
    }
    successfulInitializations
  }
```

* initializeLeaderAndIsrForPartitions(partitions: Seq[TopicPartition])函数首先得到partitionWithoutReplicas和partitionsWithReplicas，前者会报错并记录在文件中
* 由partitionsWithReplicas出发，调用LeaderAndISR.api得到第一个副本与存活副本，并向zk节点创建createResponses。函数将Epoch更新，并更新上下文，发送给个代理节点完成initialization(successfulinitialization进行计数)，并根据createResponses进行交互

与onBrokerStartup(newBrokers: Seq[Int])相对应的，对代理节点进行操作的函数有onBrokerFailure(deadBrokers: Seq[Int])与onBrokerUpdate(updatedBrokerId: Int)。Failure函数负责将下线节点上的所有副本转为offline状态，并注销registerBrokerModificationsHandler监听器；onBrokerUpdate()函数负责给代理节点发送元数据并进行更新

以上说的onReplicasBecomeOffline()负责调用分区状态机和副本状态机的handlestatechanges()函数将分区和副本均改为下线状态

#### onPartitionReassignment(topicPartition: TopicPartition, reassignedPartitionContext: ReassignedPartitionsContext)

这是kafka_controller类中最为重要的函数之一，它负责在有新的副本上线时，进行分区的重新分配。并且它会被多次调用。每次被调用时，由于要考虑到当前的ISR与AR是否都包含了OAR与RAR，因此只有当上述条件满足时，才会进行第二步操作，否则条件判断将一直为false

```java
private def onPartitionReassignment(topicPartition: TopicPartition, reassignedPartitionContext: ReassignedPartitionsContext) {
    val reassignedReplicas = reassignedPartitionContext.newReplicas
    if (!areReplicasInIsr(topicPartition, reassignedReplicas)) {
      info(s"New replicas ${reassignedReplicas.mkString(",")} for partition $topicPartition being reassigned not yet " +
        "caught up with the leader")
      val newReplicasNotInOldReplicaList = reassignedReplicas.toSet -- controllerContext.partitionReplicaAssignment(topicPartition).toSet
      val newAndOldReplicas = (reassignedPartitionContext.newReplicas ++ controllerContext.partitionReplicaAssignment(topicPartition)).toSet
      //1. Update AR in ZK with OAR + RAR.
      updateAssignedReplicasForPartition(topicPartition, newAndOldReplicas.toSeq)
      //2. Send LeaderAndIsr request to every replica in OAR + RAR (with AR as OAR + RAR).
      updateLeaderEpochAndSendRequest(topicPartition, controllerContext.partitionReplicaAssignment(topicPartition),
        newAndOldReplicas.toSeq)
      //3. replicas in RAR - OAR -> NewReplica
      startNewReplicasForReassignedPartition(topicPartition, reassignedPartitionContext, newReplicasNotInOldReplicaList)
      info(s"Waiting for new replicas ${reassignedReplicas.mkString(",")} for partition ${topicPartition} being " +
        "reassigned to catch up with the leader")
    } else {
      //4. Wait until all replicas in RAR are in sync with the leader.
      val oldReplicas = controllerContext.partitionReplicaAssignment(topicPartition).toSet -- reassignedReplicas.toSet
      //5. replicas in RAR -> OnlineReplica
      reassignedReplicas.foreach { replica =>
        replicaStateMachine.handleStateChanges(Seq(new PartitionAndReplica(topicPartition, replica)), OnlineReplica)
      }
      //6. Set AR to RAR in memory.
      //7. Send LeaderAndIsr request with a potential new leader (if current leader not in RAR) and
      //   a new AR (using RAR) and same isr to every broker in RAR
      moveReassignedPartitionLeaderIfRequired(topicPartition, reassignedPartitionContext)
      //8. replicas in OAR - RAR -> Offline (force those replicas out of isr)
      //9. replicas in OAR - RAR -> NonExistentReplica (force those replicas to be deleted)
      stopOldReplicasOfReassignedPartition(topicPartition, reassignedPartitionContext, oldReplicas)
      //10. Update AR in ZK with RAR.
      updateAssignedReplicasForPartition(topicPartition, reassignedReplicas)
      //11. Update the /admin/reassign_partitions path in ZK to remove this partition.
      removePartitionFromReassignedPartitions(topicPartition)
      //12. After electing leader, the replicas and isr information changes, so resend the update metadata request to every broker
      sendUpdateMetadataRequest(controllerContext.liveOrShuttingDownBrokerIds.toSeq, Set(topicPartition))
      // signal delete topic thread if reassignment for some partitions belonging to topics being deleted just completed
      topicDeletionManager.resumeDeletionForTopics(Set(topicPartition.topic))
    }
```

在上述函数关于分区的重新分配中，可以大致分为以下五个步骤：

1. 将RAR加入AR，此时AR=OAR+RAR
2. 将RAR加入ISR，此时ISR=OAR+RAR
3. ISR不变，但分区的主副本从RAR中选举
4. 将OAR从ISR中移除，此时ISR=RAR
5. 将OAR从AR中移除，此时AR=RAR

关于onPartitionReassignment()为什么可能要执行两次的原因，因为onPartitionReassignment()每次执行时关于allNewReplicas的判断，如果为假，则说明还并没有将新建的所有副本加入进ISR中，因此需要在执行一次；当该条件判断为真时，则说明第一步操作已经完成，此时再将之前的旧的副本分别从ISR和AR中移除就可以了

关于该函数的调用时序图如下。

kafka_controller中其他的函数，比如resetControllerContext()更新上下文，fetchPendingPreferredReplicaElections()进行副本领导机制选举，再次不过多赘述。kafka_contoller中的方法，最终被contoller_event类的子类所用，即prossess()函数调用kafka_contoller中的函数(由startUp类进行调用)
