package l02.example5;

import java.util.Scanner;

public class Reconciliation1 {
	public static void main(String[] args) {

		double totalTransFromShanghai = 100.2;
		System.out.println("交易所发回的交易总额为（单位：元）: " + totalTransFromShanghai);

		// Create a Scanner
		Scanner input = new Scanner(System.in);

		System.out.print("第一笔交易额为（单位：元）: ");
		double trans1 = input.nextDouble();

		System.out.print("第二笔交易额为（单位：元）: ");
		double trans2 = input.nextDouble();

		double totalTrans = trans1 + trans2;

		if (totalTransFromShanghai == totalTrans) {
			System.out.println("对账正确，可以按时下班了！");
		} else {
			System.out.println("Duang！");
			System.out.println("totalTrans = " + totalTrans);
		}

		input.close();
	}
}
