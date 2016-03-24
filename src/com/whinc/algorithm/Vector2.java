package com.whinc.algorithm;

/**
 * Created by Administrator on 2016/3/23.
 */
public class Vector2 extends BaseVector{

    public Vector2() {
        this(0.0, 0.0);
    }

    public Vector2(Double v0, Double v1) {
        createVector(2);
        set(0, v0);
        set(1, v1);
    }

    public Vector2(Vector2 other) {
        createVector(other.size());
        for (int i = 0; i < size(); ++i) {
            set(i, other.get(i));
        }
    }
}
