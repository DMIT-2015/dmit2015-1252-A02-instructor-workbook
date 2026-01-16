package dmit2015.console;

/**
 * Models a Rectangle shape
 *
 * @author Sam Wu
 * @version 2026.01.16
 */
public class Rectangle implements Shape {

    // Define read-only fields
    private final double length;
    private final double width;

    // Define a greedy constructor to set Rectangle to specific length and width

    public Rectangle(double length, double width) {
        if (length <= 0 || width <= 0) {
            throw new IllegalArgumentException("Length and Width must be positive numbers.");
        }
        this.length = length;
        this.width = width;
    }

    public double getLength() {
        return length;
    }

    public double getWidth() {
        return width;
    }

    @Override
    public double getArea() {
        return length * width;
    }

    @Override
    public double getPerimeter() {
        return 2 * (length + width);
    }
}
