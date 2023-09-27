package l02.example5;

import java.math.BigDecimal;
import java.util.Scanner;

public class Reconciliation2 {
	public static void main(String[] args) {

		BigDecimal totalTransFromShanghai = new BigDecimal("100.2");
		System.out.println("交易所发回的交易总额为（单位：元）: " + totalTransFromShanghai);

		// Create a Scanner
		Scanner input = new Scanner(System.in);

		System.out.print("第一笔交易额为（单位：元）: ");
		BigDecimal trans1 = input.nextBigDecimal();

		System.out.print("第二笔交易额为（单位：元）: ");
		BigDecimal trans2 = input.nextBigDecimal();

		BigDecimal totalTrans = trans1.add(trans2);

		if (totalTransFromShanghai.compareTo(totalTrans) == 0) {
			System.out.println("对账正确，可以按时下班了！");
		} else {
			System.out.println("Duang！");
			// System.out.println("totalTrans = " + totalTrans);
		}

		input.close();
	}
}
