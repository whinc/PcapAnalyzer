package com.whinc.algorithm;

/**
 * Created by Administrator on 2016/3/23.
 */
public abstract class AbstractVector<T extends Number> implements Vector<T>{
    private T[] vector;

    @Override
    public T get(int index) {
        return vector[index];
    }

    @Override
    public void set(int index, T value) {
        vector[index] = value;
    }

    @Override
    public int size() {
        return vector.length;
    }

    protected void createVector(int size) {
        vector = (T[])new Number[size];
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof AbstractVector)) return false;

        if (this == obj) return true;

        AbstractVector other = (AbstractVector) obj;
        if (size() != other.size()) return false;

        for (int i = 0; i < size(); ++i) {
            if (!get(i).equals(other.get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("[");
        for (int i = 0; i < vector.length; ++i) {
            builder.append(vector[i]);
            if (i != vector.length - 1) {
                builder.append(",");
            }
        }
        builder.append("]");
        return builder.toString();
    }
}
