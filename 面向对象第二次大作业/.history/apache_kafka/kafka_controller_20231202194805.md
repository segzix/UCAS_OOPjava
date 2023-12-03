# Kafka_Controller

Kafka_Controller作为controller模块中的核心组件，对整个Kafka控制器起着总管作用。kafka_controller由外界进行启动和关闭，主要功能有进行状态机和控制器上下文的初始化，并负责想不通的代理节点发送信息。kafka_controller通过调用其他类的方法来完成上述功能，并且注册监听器，将controllerEvent写进阻塞队列中供eventManager实例进行处理。下面通过对kafka_controller的重要函数来说明kafka_controller的主要功能

#### startUp()

```apache
def startup() = {
    zkClient.registerStateChangeHandler(new StateChangeHandler {
      override val name: String = StateChangeHandlers.ControllerHandler
      override def afterInitializingSession(): Unit = {
        eventManager.put(RegisterBrokerAndReelect)
      }
      override def beforeInitializingSession(): Unit = {
        val expireEvent = new Expire
        eventManager.clearAndPut(expireEvent)

        // Block initialization of the new session until the expiration event is being handled,
        // which ensures that all pending events have been processed before creating the new session
        expireEvent.waitUntilProcessingStarted()
      }
    })
    eventManager.put(Startup)
    eventManager.start()
  }
```

startUp函数的主要功能时注册registerStateChangeHandler监听器，并且对eventManager类中的阻塞队列进行清空，方便之后controllerEventmanager事件发生时成功写进阻塞队列并且由eventManager实例进行处理

#### Shutdown()

```apache
def shutdown() = {
    eventManager.close()
    onControllerResignation()
  }
```

Shutdown函数的主要功能是关闭eventManager实例
