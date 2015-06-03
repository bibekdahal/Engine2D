package com.bibek.engine2d;

public class TransformationComponent implements Component {
    public float x, y, angle;

    public TransformationComponent(float x, float y, float angle) {
        this.x = x;
        this.y = y;
        this.angle = angle;
    }
}
