package l02.example6;

import java.util.InputMismatchException;
import java.util.Scanner; 

public class LeapYear {
  public static void main(String args[]) {
    // Create a Scanner
    Scanner input = new Scanner(System.in);
    System.out.print("请输入年份: ");
    
    try{
    	int year = input.nextInt();
    	
    	if (year < 0) {
        	System.out.println("输入值错误（不能为负值），程序已退出");
        	System.exit(1);
        }

        // Check if the year is a leap year 
        boolean isLeapYear = 
          (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);

        // Display the result in a message dialog box
        System.out.println(year + "年是否为闰年? " + isLeapYear);   
        
    } catch (InputMismatchException e){
    	System.out.println("输入值错误 （非整数），程序已退出");
    	System.exit(1);
    }
  } 
}
