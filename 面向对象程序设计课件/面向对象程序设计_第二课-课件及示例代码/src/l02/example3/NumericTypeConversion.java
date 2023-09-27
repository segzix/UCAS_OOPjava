package l02.example3;

import java.util.Scanner;

public class NumericTypeConversion {
  public static void main(String[] args) {
	  
	int a1 = 9 / 5; 
    System.out.println("int a1 = 9 / 5;");
    System.out.println("a1 = " + a1);
    
	int a2 = (int)(9.0 / 5); 
    System.out.println("int a2 = 9.0 / 5;");
    System.out.println("a2 = " + a2);
    
	int a3 = (int)(9.0 / 5.0); 
    System.out.println("int a3 = 9.0 / 5.0;");
    System.out.println("a3 = " + a3);
    
	double a4 = 9 / 5; 
    System.out.println("double a4 = 9 / 5;");
    System.out.println("a4 = " + a4);
    
	double a5 = 9.0 / 5; 
    System.out.println("double a5 = 9.0 / 5;");
    System.out.println("a5 = " + a5);
    
	double a6 = 9.0 / 5.0; 
    System.out.println("double a6 = 9.0 / 5.0;");
    System.out.println("a6 = " + a6);
  }
}
