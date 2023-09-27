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
			System.out.print("����뾶ֵ: ");
			
			double radius = 0;

			try {
				radius = Double.parseDouble(input.nextLine());
			} catch (Exception e) {
				System.out.println("����ֵ�������������롣");
				continue;
			}
			
			if (radius >= 0) {
				// Compute area
				double area = radius * radius * PI;

				// Display result
				System.out.println("�뾶Ϊ " + radius + " ��Բ����� " + area);
			} else {
				// Input error
				System.out.println("����ֵ���󣨲���Ϊ��ֵ�������������롣");
				continue;
			}
			
			boolean retype = true;
			while (retype) {
				System.out.print("�Ƿ������������Y��N��");
				try {
					String answer = input.nextLine();
					if (answer.toLowerCase().equals("y")){
						break;
					} else if (answer.toLowerCase().equals("n")){
						nextRound = false;
						break;
					}
				} catch (InputMismatchException e) {
					System.out.println("����ֵ�������������롣");
					continue;
				}
			}
		}
		
		System.out.println("ллʹ�ã�");

	}
}
