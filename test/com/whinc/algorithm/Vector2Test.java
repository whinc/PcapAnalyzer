package com.whinc.algorithm;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Administrator on 2016/3/23.
 */
public class Vector2Test {

    @Test
    public void testMultiply() throws Exception {
        Vector2 v1 = new Vector2(1.0, 2.0);
        assertEquals(new Vector2(2.0, 4.0), v1.multiply(2.0));

        Vector2 v2 = new Vector2(v1);
        assertEquals(v1, v2);
    }
}