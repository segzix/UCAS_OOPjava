package l02.example5;

import java.util.Scanner;

public class Reconciliation1 {
	public static void main(String[] args) {

		double totalTransFromShanghai = 100.2;
		System.out.println("���������صĽ����ܶ�Ϊ����λ��Ԫ��: " + totalTransFromShanghai);

		// Create a Scanner
		Scanner input = new Scanner(System.in);

		System.out.print("��һ�ʽ��׶�Ϊ����λ��Ԫ��: ");
		double trans1 = input.nextDouble();

		System.out.print("�ڶ��ʽ��׶�Ϊ����λ��Ԫ��: ");
		double trans2 = input.nextDouble();

		double totalTrans = trans1 + trans2;

		if (totalTransFromShanghai == totalTrans) {
			System.out.println("������ȷ�����԰�ʱ�°��ˣ�");
		} else {
			System.out.println("Duang��");
			System.out.println("totalTrans = " + totalTrans);
		}

		input.close();
	}
}
