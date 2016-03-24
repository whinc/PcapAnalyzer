package com.whinc.algorithm;

/**
 * 特征向量
 */
public class Eigenvector {
    private double[] vector;

    public int getSize() {
        return vector.length;
    }

    public double get(int index) {
        return vector[index];
    }

    public Eigenvector(double v0, double v1) {
        vector = new double[2];
        vector[0] = v0;
        vector[1] = v1;
    }

    /** 根据已有的向量创建一个新的向量 */
    public Eigenvector(Eigenvector vector) {
        if (vector == null) {
            throw new NullPointerException();
        }
        this.vector = new double[vector.getSize()];
        for (int i = 0; i < this.vector.length; ++i) {
            this.vector[i] = vector.get(i);
        }
    }

    public Eigenvector multiply(double v) {
        for (int i = 0; i < vector.length; ++i) {
            vector[i] *= v;
        }
        return this;
    }

    public Eigenvector divide(double v) {
        for (int i = 0; i < vector.length; ++i) {
            vector[i] /= v;
        }
        return this;
    }

    public Eigenvector add(Eigenvector other) {
        for (int i = 0; i < vector.length; ++i) {
            vector[i] += other.get(i);
        }
        return this;
    }

    public Eigenvector sub(Eigenvector other) {
        for (int i = 0; i < vector.length; ++i) {
            vector[i] -= other.get(i);
        }
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Eigenvector)) return false;

        if (this == obj) return true;

        Eigenvector other = (Eigenvector) obj;
        if (getSize() != other.getSize()) return false;
        for (int i = 0; i < getSize(); ++i) {
            if (get(i) != other.get(i)) {
                return false;
            }
        }
        return true;
    }

    /** 计算两个向量之间的距离 ||v1 - v2|| */
    public static double distance(Eigenvector e1, Eigenvector e2) {
        if (e1 == e2) {

        }
        if (e1.getSize() != e2.getSize()) {
            throw new IllegalArgumentException("Vector length must be equal!");
        }

        float distance = 0.0f;
        for (int k = 0; k < e1.getSize(); ++k) {
            distance += Math.pow(e1.get(k) - e2.get(k), 2);
        }
        return Math.sqrt(distance);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("(");
        for (int i = 0; i < vector.length; ++i) {
            builder.append(vector[i]);
            if (i != vector.length - 1) {
                builder.append(",");
            }
        }
        builder.append(")");
        return builder.toString();
    }
}
