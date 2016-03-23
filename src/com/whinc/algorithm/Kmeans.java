package com.whinc.algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2016/3/11.
 */
public class Kmeans implements Runnable {

    /** 类别数 */
    private int k = 0;

    /** 样本集合 */
    Eigenvector[] S = null;

    /** 聚类中心集合 */
    Eigenvector[] C;

    /** 距离矩阵，行表示聚类中心，列表示样本点，D[i][j] == 1表示第j个样本点属于第i个聚类中心（即距离最近） */
    double[][] D;

    /**
     * 创建k-means的具体实现对象
     * @param k 分类数
     */
    public Kmeans(int k) {
        if (k <= 0 && k > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("invalid argument k:" + k);
        }
        this.k = k;
        this.C = new Eigenvector[k];
        this.D = new double[k][];
    }

    /**
     * 设置样本集合
     * @param S
     */
    public void setSampleSet(Eigenvector[] S) {
        if (S == null) {
            throw new IllegalArgumentException("Sample set is null");
        }
        this.S = S;

        for (int i = 0; i < D.length; ++i) {        // 创建数组
            D[i] = new double[S.length];
        }
    }

    /** 从样本集合中随机选取k个样本作为聚类中心 */
    private void randomCenter(Eigenvector[] dataset, Eigenvector[] center) {
        Random random = new Random(System.currentTimeMillis());
        for (int k = 0; k < center.length; ++k) {
            do {
                center[k] = dataset[random.nextInt(dataset.length)];
            } while (exist(center, k, center[k]));    // 如果随机数已经存在，则继续直到找到一个未出现过的随机数
        }
    }

    /**
     * 检查数组中是否已存在指定值
     */
    private boolean exist(Eigenvector[] center, int length, Eigenvector e) {
        boolean result = false;

        for (int i = 0; i < length; ++i) {
            if (center[i] == e) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * 计算与样本点距离最近的聚类中心点，并返回该聚类中心在聚类中心数组中的索引
     * @param center 聚类中心数组
     * @param e 样本点
     * @return 最近的聚类中心的索引
     */
    private int getMinDistance(Eigenvector[] center, Eigenvector e) {
        int index = 0;
        double minDistance = Eigenvector.distance(center[index], e);

        for (int i = 1; i < center.length; ++i) {
            double distance = Eigenvector.distance(center[i], e);
            if (distance < minDistance) {
                minDistance = distance;
                index = i;
            }
        }

        return index;
    }

    /** 重置距离矩阵 D */
    private void reset() {
        for (int i = 0; i < D.length; ++i) {
            for (int j = 0; j < D[i].length; ++j) {
                D[i][j] = 0;
            }
        }
    }


    @Override
    public void run() {
        // 设置阈值
        // 当两次误差平方和准则函数计算的结果之差的绝对值小于等于该值时，k-means算法收敛
        double threshold = 3.0;
        double error = 0.0;

        // 初始化聚类中心
        // 随机选择 k 个点作为初始的聚类中心（作为简化，这里在已有的数据集合中选择）
        randomCenter(S, C);

        do {
            // 修正
            // 重新计算数据集中每个元素到各个聚类中心点最小距离 d，
            // 例如 d[i] 表示数据集中所有元素到第i个聚类中心的最小距离
            reset();
            for (int i = 0; i < S.length; ++i) {
                int index = getMinDistance(C, S[i]);    // 获取与第i个样本点距离最近的聚类中心的索引
                D[index][i] = 1;
            }

            // 修正聚类中心
            Eigenvector[] C2 = new Eigenvector[C.length];
            for (int i = 0; i < C.length; ++i) {
                double v = 0;
                Eigenvector e = new Eigenvector(0, 0);
                for (int j = 0; j < S.length; ++j) {
                    v += D[i][j];
                    Eigenvector vector = new Eigenvector(S[j]);
                    e.add(vector.multiply(D[i][j]));
                }
                C2[i] = e.divide(v);
            }

            // 计算误差
            error = 0;  // 先清零
            for (int i = 0; i < C.length; ++i) {
                error += Eigenvector.distance(C2[i], C[i]);
                C[i] = C2[i];       // 存储新的聚类中心
            }

        } while (error >= threshold);     // // 误差若不小于设定的阈值则继续

    }

    public void printCenter() {
        for (int i = 0; i < C.length; ++i) {
            System.out.println(C[i]);
        }
    }

    /** 获取聚类中心点集合 */
    public List<Eigenvector> getCenter() {
        return Arrays.asList(C);
    }

    /** 获取每个分类所包含的样本点集合 */
    public List<List<Eigenvector>> getCenterList(){
        List<List<Eigenvector>> d = new ArrayList<>(C.length);
        // 距离矩阵中，行表示分类，列表示样本点，D[i][j]如果为1，表示第i个分类包含第j个样本，否则表示不包含
        for (int i = 0; i < C.length; ++i) {
            List<Eigenvector> list = new ArrayList<>();
            d.add(list);
            for (int j = 0; j < S.length; ++j) {
                if (D[i][j] == 1) {
                    list.add(S[j]);
                }
            }
        }
        return d;
    }
}
