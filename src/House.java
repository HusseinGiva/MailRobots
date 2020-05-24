package src;

import java.awt.*;

public class House extends Block {

    public Point point;

    public House(Shape shape, Point point, Color color) {
        super(shape, color);
        this.point = point;
    }

}
