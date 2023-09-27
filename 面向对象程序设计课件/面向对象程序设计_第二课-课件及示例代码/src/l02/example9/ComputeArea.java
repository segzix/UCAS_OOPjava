package l02.example9;

import java.util.InputMismatchException;
import java.util.Scanner; // Scanner is in the java.util package

public class ComputeArea {
	public static void main(String[] args) {

		final double PI = 3.14159; // Declare PI

		boolean nextRound = true;
		
		Scanner input = new Scanner(System.in);

		while (nextRound) {
			
			// Prompt the user to enter a radius
			System.out.print("输入半径值: ");
			
			double radius = 0;

			try {
				radius = Double.parseDouble(input.nextLine());
			} catch (Exception e) {
				System.out.println("输入值错误，请重新输入。");
				continue;
			}
			
			if (radius >= 0) {
				// Compute area
				double area = radius * radius * PI;

				// Display result
				System.out.println("半径为 " + radius + " 的圆面积是 " + area);
			} else {
				// Input error
				System.out.println("输入值错误（不能为负值），请重新输入。");
				continue;
			}
			
			boolean retype = true;
			while (retype) {
				System.out.print("是否继续？请输入Y或N：");
				try {
					String answer = input.nextLine();
					if (answer.toLowerCase().equals("y")){
						break;
					} else if (answer.toLowerCase().equals("n")){
						nextRound = false;
						break;
					}
				} catch (InputMismatchException e) {
					System.out.println("输入值错误，请重新输入。");
					continue;
				}
			}
		}
		
		System.out.println("谢谢使用！");

	}
}
