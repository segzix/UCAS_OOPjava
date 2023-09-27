package l02.example4;

import java.util.Scanner;

public class CelsiusToFahrenheit {
  public static void main(String[] args) {
    Scanner input = new Scanner(System.in);

    System.out.print("输入摄氏温度: ");
    double celsius = input.nextDouble(); 

    if (celsius < -273.15){
    	// Input error
		System.out.println("输入值错误（不能低于绝对零度-237.15），程序已退出");
    	System.exit(1);
	} else if (celsius > 1.416833e32) {
		// Input error
		System.out.println("输入值错误（不能高于普朗克温度1.416833(85)x10^32K），程序已退出");
    	System.exit(1);
	} else {
		// Convert Celsius to Fahrenheit
	    double fahrenheit = (9.0 / 5) * celsius + 32;
	    System.out.println("摄氏 " + celsius + " 度等于华氏 " + 
	    		fahrenheit + " 度"); 
	}   
    
    input.close();
  }
}
