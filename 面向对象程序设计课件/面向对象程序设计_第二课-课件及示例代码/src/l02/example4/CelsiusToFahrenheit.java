package l02.example4;

import java.util.Scanner;

public class CelsiusToFahrenheit {
  public static void main(String[] args) {
    Scanner input = new Scanner(System.in);

    System.out.print("���������¶�: ");
    double celsius = input.nextDouble(); 

    if (celsius < -273.15){
    	// Input error
		System.out.println("����ֵ���󣨲��ܵ��ھ������-237.15�����������˳�");
    	System.exit(1);
	} else if (celsius > 1.416833e32) {
		// Input error
		System.out.println("����ֵ���󣨲��ܸ������ʿ��¶�1.416833(85)x10^32K�����������˳�");
    	System.exit(1);
	} else {
		// Convert Celsius to Fahrenheit
	    double fahrenheit = (9.0 / 5) * celsius + 32;
	    System.out.println("���� " + celsius + " �ȵ��ڻ��� " + 
	    		fahrenheit + " ��"); 
	}   
    
    input.close();
  }
}
