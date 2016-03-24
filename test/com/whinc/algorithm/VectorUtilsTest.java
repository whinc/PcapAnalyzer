package com.whinc.algorithm;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Administrator on 2016/3/24.
 */
public class VectorUtilsTest {

    @Test
    public void testDistance() throws Exception {
        Vector2 v1 = new Vector2(1.0, 2.0);
        Vector2 v2 = new Vector2(v1);
        assertEquals(0.0, VectorUtils.distance(v1, v2), 1e-3);
        Vector2 v3 = new Vector2(2.0, 3.0);
        assertEquals(Math.sqrt(2), VectorUtils.distance(v1, v3), 1e-3);
    }
}