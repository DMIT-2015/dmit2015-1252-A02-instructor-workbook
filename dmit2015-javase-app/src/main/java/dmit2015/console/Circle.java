package dmit2015.console;

/**
 * Models a Circle shape.
 *
 * @author Sam Wu
 * @version 2026.01.13
 */
public class Circle {
    // Define a field to track the radius of this circle
    private double radius;

    // Define accessor (getter) and mutator (setting) for the data fields
    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        if (radius < 0) {
            throw new IllegalArgumentException("Radius cannot be negative");
        }
        this.radius = radius;
    }

    // Create a Circle with a radius of 1.0
    public Circle() {
        setRadius(1.0);
    }
    // Create a Circle with a specific radius
    public Circle(double radius) {
//        this.radius = radius;
        setRadius(radius);
    }

    // Define instance-level methods to determine area, diameter, perimeter
    public double getArea() {
        return Math.PI * radius * radius;
    }
    public double getDiameter() {
        return 2 * radius;
    }
    public double getPerimeter() {
        return 2 * Math.PI * radius;
    }

}
