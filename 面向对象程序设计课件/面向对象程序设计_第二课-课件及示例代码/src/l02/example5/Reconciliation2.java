package l02.example5;

import java.math.BigDecimal;
import java.util.Scanner;

public class Reconciliation2 {
	public static void main(String[] args) {

		BigDecimal totalTransFromShanghai = new BigDecimal("100.2");
		System.out.println("���������صĽ����ܶ�Ϊ����λ��Ԫ��: " + totalTransFromShanghai);

		// Create a Scanner
		Scanner input = new Scanner(System.in);

		System.out.print("��һ�ʽ��׶�Ϊ����λ��Ԫ��: ");
		BigDecimal trans1 = input.nextBigDecimal();

		System.out.print("�ڶ��ʽ��׶�Ϊ����λ��Ԫ��: ");
		BigDecimal trans2 = input.nextBigDecimal();

		BigDecimal totalTrans = trans1.add(trans2);

		if (totalTransFromShanghai.compareTo(totalTrans) == 0) {
			System.out.println("������ȷ�����԰�ʱ�°��ˣ�");
		} else {
			System.out.println("Duang��");
			// System.out.println("totalTrans = " + totalTrans);
		}

		input.close();
	}
}
