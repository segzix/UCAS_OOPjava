package l02.example6;

import java.util.InputMismatchException;
import java.util.Scanner; 

public class LeapYear {
  public static void main(String args[]) {
    // Create a Scanner
    Scanner input = new Scanner(System.in);
    System.out.print("���������: ");
    
    try{
    	int year = input.nextInt();
    	
    	if (year < 0) {
        	System.out.println("����ֵ���󣨲���Ϊ��ֵ�����������˳�");
        	System.exit(1);
        }

        // Check if the year is a leap year 
        boolean isLeapYear = 
          (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);

        // Display the result in a message dialog box
        System.out.println(year + "���Ƿ�Ϊ����? " + isLeapYear);   
        
    } catch (InputMismatchException e){
    	System.out.println("����ֵ���� �������������������˳�");
    	System.exit(1);
    }
  } 
}
