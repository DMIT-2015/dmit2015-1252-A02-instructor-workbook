package dmit2015.console;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CircleTest {

    @Test
    void getArea() {
        // Create a circle with a radius of 5
        Circle circle = new Circle(5);
        // Verify the radius is 5
//        assertEquals(5, circle.getRadius());
        // Verify area of circle is not zero using JUnit assertion
        // assertEquals(0, circle.getArea());
        // Verify area of circle is 78.54 using JUnit assertion
        final double DIFFERENCE_ALLOWED = 0.005;
        assertEquals(78.54, circle.getArea(), DIFFERENCE_ALLOWED);

    }

    @Test
    void getDiameter() {
    }

    @Test
    void getPerimeter() {
    }

    @Test
    void testToString() {
    }
}