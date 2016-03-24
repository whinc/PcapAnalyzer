package com.whinc.algorithm;

/**
 * Created by Administrator on 2016/3/24.
 */
public class BaseVector extends AbstractVector<Double>{

    @Override
    public <S extends Vector<Double>> S multiply(Double v) {
        for (int i = 0; i < size(); ++i) {
            set(i, get(i) * v);
        }
        return (S) this;
    }

    @Override
    public <S extends Vector<Double>> S divide(Double v) {
        for (int i = 0; i < size(); ++i) {
            set(i, get(i) / v);
        }
        return (S) this;
    }

    @Override
    public <S extends Vector<Double>> S add(S vector) {
        for (int i = 0; i < size(); ++i) {
            set(i, get(i) + vector.get(i));
        }
        return (S) this;
    }

    @Override
    public <S extends Vector<Double>> S sub(S vector) {
        for (int i = 0; i < size(); ++i) {
            set(i, get(i) - vector.get(i));
        }
        return (S)this;
    }
}
