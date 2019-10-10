package org.dain.daydayup.concurrent;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Description TODO
 * @Author lideyin
 * @Date 2019/9/12 9:55
 * @Version 1.0
 */
public class StaticHolder {
    private static final AtomicInteger errorCount = new AtomicInteger(0);
    private static int successCount=0;

    public static void increaseError() {
        errorCount.incrementAndGet();
    }

    public static void decreaseError() {
        errorCount.decrementAndGet();
    }

    public static int getError() {
        return errorCount.get();
    }

    public static void increaseSuccess() {
        successCount++;
    }

    public static void decreaseSuccess() {
        successCount--;
    }

    public static int getSuccess() {
        return successCount;
    }

}
