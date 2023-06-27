package online.flowerinsnow.cipherjava.util;

import java.util.function.BooleanSupplier;
import java.util.function.Function;

public abstract class ConditionUtils {
    private ConditionUtils() {
    }

    /**
     * 当给出的条件数量满足条件时，返回true
     *
     * @param acceptFunction 给出的条件数量可接受范围
     * @param conditions 条件
     * @return 当给出的条件数量满足条件时，返回true
     */
    public static boolean functionMatch(Function<Integer, Boolean> acceptFunction, BooleanSupplier... conditions) {
        int times = 0;
        for (BooleanSupplier condition : conditions) {
            if (condition.getAsBoolean()) {
                times++;
            }
        }
        return Boolean.TRUE == acceptFunction.apply(times);
    }

    /**
     * 当给出的条件数量为指定数量时，返回true
     *
     * @param acceptTimes 给出的条件数量可接受数量
     * @param conditions 条件
     * @return 当给出的条件数量满足条件时，返回true
     */
    public static boolean timesMatch(int acceptTimes, BooleanSupplier... conditions) {
        return functionMatch(times -> times == acceptTimes, conditions);
    }

    /**
     * 当给出的条件数量在指定范围内，返回true
     * 其中最小和最大可调换
     *
     * @param minAcceptTimes 最小次数
     * @param maxAcceptTimes 最大次数
     * @param conditions 条件
     * @return 当给出的条件数量满足条件时，返回true
     */
    public static boolean timesMatch(int minAcceptTimes, int maxAcceptTimes, BooleanSupplier... conditions) {
        return functionMatch(times -> times >= Math.min(minAcceptTimes, maxAcceptTimes) && times <= Math.max(minAcceptTimes, maxAcceptTimes), conditions);
    }

    /**
     * 当给出的条件数量有一个且只有一个时，返回true
     *
     * @param conditions 条件
     * @return 当给出的条件数量满足条件时，返回true
     */
    public static boolean oneMatch(BooleanSupplier... conditions) {
        return timesMatch(1, conditions);
    }
}
