package com.whinc.algorithm;

/**
 * Created by Administrator on 2016/3/23.
 */
public interface Vector<T> {
    T get(int index);
    void set(int index, T value);
    int size();
    <S extends Vector<T>> S multiply(T v);
    <S extends Vector<T>> S divide(T v);
    <S extends Vector<T>> S add(S vector);
    <S extends Vector<T>> S sub(S vector);
}
