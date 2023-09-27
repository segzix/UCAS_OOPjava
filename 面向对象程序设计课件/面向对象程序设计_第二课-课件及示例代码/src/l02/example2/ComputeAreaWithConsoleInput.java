package l02.example2;

import java.util.Scanner; // Scanner is in the java.util package

public class ComputeAreaWithConsoleInput {
	public static void main(String[] args) {

		final double PI = 3.14159; // Declare PI
		
		// Create a Scanner object
		Scanner input = new Scanner(System.in);

		// Prompt the user to enter a radius
		System.out.print("输入半径值: ");
		double radius = input.nextDouble();

		// Compute area
		double area = radius * radius * PI;

		// Display result
		System.out.println("半径为 " +
			      radius + " 的圆面积是 " + area);
		
		input.close();
	}
}
