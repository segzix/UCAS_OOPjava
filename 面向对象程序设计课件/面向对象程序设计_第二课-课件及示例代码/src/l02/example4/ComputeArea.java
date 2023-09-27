package l02.example4;

import java.util.Scanner; // Scanner is in the java.util package

public class ComputeArea {
	public static void main(String[] args) {

		final double PI = 3.14159; // Declare PI
		
		// Create a Scanner object
		Scanner input = new Scanner(System.in);

		// Prompt the user to enter a radius
		System.out.print("����뾶ֵ: ");
		double radius = input.nextDouble();

		if (radius >= 0){
			// Compute area
			double area = radius * radius * PI;

			// Display result
			System.out.println("�뾶Ϊ " +
				      radius + " ��Բ����� " + area);
		} else {
			// Input error
			System.out.println("����ֵ���󣨲���Ϊ��ֵ�����������˳�");
	    	//System.exit(1);
		}
		
		input.close();
	}
}
