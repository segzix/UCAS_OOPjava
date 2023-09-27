package l02.example1;

public class ComputeArea {
	
  public static void main(String[] args) {
	  
    double radius; // Declare radius
    double area; // Declare area
    final double PI = 3.14159; // Declare PI
    
    radius = 20; // Assign a radius

    // Compute area
    area = radius * radius * PI;

    // Display results
    System.out.println("半径为 " +
      radius + " 的圆面积是 " + area);
    }
}
