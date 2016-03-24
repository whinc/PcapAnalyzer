package com.whinc.algorithm;

/**
 * Created by Administrator on 2016/3/24.
 */
public class VectorUtils {

    /** 计算两个向量之间的距离 ||v1 - v2|| */
    public static double distance(BaseVector v1, BaseVector v2) {
        if (v1.equals(v2)) {
            return 0.0;
        }
        if (v1.getClass() != v2.getClass()) {
            throw new IllegalArgumentException("Vector must be same type, but v1 is " +
                    v1.getClass().getName() + ", v2 is " + v2.getClass().getName()
            );
        }

        float distance = 0.0f;
        for (int k = 0; k < v1.size(); ++k) {
            distance += Math.pow(v1.get(k) - v2.get(k), 2);
        }
        return Math.sqrt(distance);
    }
}
