package dmit2015.console;

import java.util.List;

public class ShapeDemo {

    static void main() {
        List<Shape> shapes = List.of(
                new Circle(5.0),
                new Rectangle(5.0,10.0)
        );

        for (Shape currentShape : shapes) {
            System.out.printf("Area: %.2f, Perimeter: %.2f",
                    currentShape.getArea(),
                    currentShape.getPerimeter());
        }
    }
}
