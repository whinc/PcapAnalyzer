package com.whinc.algorithm;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Administrator on 2016/3/23.
 */
public class KmeansTest {

    @Test
    public void testRun() throws Exception {
        Kmeans kmeans = new Kmeans(6);
        Eigenvector[] dataset = new Eigenvector[10];
        dataset[0] = new Eigenvector(1, 2);
        dataset[1] = new Eigenvector(3, 3);
        dataset[2] = new Eigenvector(3, 4);
        dataset[3] = new Eigenvector(5, 6);
        dataset[4] = new Eigenvector(8, 9);
        dataset[5] = new Eigenvector(4, 5);
        dataset[6] = new Eigenvector(6, 4);
        dataset[7] = new Eigenvector(3, 9);
        dataset[8] = new Eigenvector(5, 9);
        dataset[9] = new Eigenvector(4, 2);
        dataset[9] = new Eigenvector(1, 9);
        dataset[9] = new Eigenvector(7, 8);
        kmeans.setSampleSet(dataset);
        kmeans.run();
        kmeans.printCenter();
    }
}