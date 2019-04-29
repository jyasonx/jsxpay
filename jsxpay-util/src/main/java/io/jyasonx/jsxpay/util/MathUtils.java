package io.jyasonx.jsxpay.util;

import java.util.Collection;

/**
 * {@link MathUtils} is responsible for some mathematics utility function.
 */
public class MathUtils {
    /**
     * 计算标准差的函数.
     */
    public static Double std(Collection<Double> data) {
        if (data.isEmpty()) {
            return 0.0d;
        } else {
            Double average = avg(data);
            Double tempSum = 0.0d;
            for (Double d : data) {
                tempSum += Math.pow(d - average, 2);
            }
            return Math.sqrt(tempSum / data.size());
        }
    }

    /**
     * 计算平均值的函数.
     */
    private static Double avg(Collection<Double> data) {
        if (data.isEmpty()) {
            return 0.0d;
        } else {
            Double sumValue = data.stream().reduce((sum, index) -> sum += index).orElse(0.0d);
            return sumValue / data.size();
        }
    }

}
