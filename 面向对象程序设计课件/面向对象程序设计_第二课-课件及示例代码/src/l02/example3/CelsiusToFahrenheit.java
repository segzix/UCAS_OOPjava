package l02.example3;

import java.util.Scanner;

public class CelsiusToFahrenheit {
  public static void main(String[] args) {
	  
    Scanner input = new Scanner(System.in);

    System.out.print("���������¶�: ");
    double celsius = input.nextDouble(); 

    // Convert Celsius to Fahrenheit
    double fahrenheit = (9.0 / 5.0) * celsius + 32;
    
    System.out.println("���� " + celsius + " �ȵ��ڻ��� " + 
    		fahrenheit + " ��");  
    
    input.close();
  }
}
