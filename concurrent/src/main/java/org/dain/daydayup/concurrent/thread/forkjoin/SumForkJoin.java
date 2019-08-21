package org.dain.daydayup.concurrent.thread.forkjoin;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

/**
 * @Description 模拟一个求和算法, 继承一个ForkJoin的递归抽象类
 * @Author lideyin
 * @Date 2019/8/21 23:57
 * @Version 1.0
 */
public class SumForkJoin extends RecursiveTask<Integer> {
    //进行拆分运算的阈值
    private static final int THRESHOLD = 2;
    private Integer start;
    private Integer end;

    public SumForkJoin(Integer start, Integer end) {
        this.start = start;
        this.end = end;
    }

    @Override
    protected Integer compute() {
        int sum = 0;
        //如果两数之差小于阈值，直接进行运算
        if (end - start <= THRESHOLD) {
            for (int i = start; i <= end; i++) {
                sum += i;
            }
        } else {
            //采用二分法思想进行拆分
            int middle = (start + end) / 2;
            SumForkJoin leftForkJoin = new SumForkJoin(start, middle);
            SumForkJoin rightForkJoin = new SumForkJoin(middle + 1, end);

            //执行左右两边的任务，其实就是递归调用
            leftForkJoin.fork();
            rightForkJoin.fork();

            //等待任务执行完后获取相应的结果
            Integer left = leftForkJoin.join();
            Integer right = rightForkJoin.join();

            sum = left + right;
        }
        return sum;
    }
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ForkJoinPool pool = new ForkJoinPool();
        SumForkJoin task = new SumForkJoin(1,100);
        //返回实现Future的一个对象
        ForkJoinTask<Integer> future = pool.submit(task);
        System.out.println("the result is " +future.get());

    }
}
