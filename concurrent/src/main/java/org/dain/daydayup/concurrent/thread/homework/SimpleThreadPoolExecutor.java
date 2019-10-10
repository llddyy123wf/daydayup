package org.dain.daydayup.concurrent.thread.homework;

import sun.awt.SunToolkit;

import java.security.InvalidParameterException;
import java.util.HashSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description 服务器端最佳线程数量=((线程等待时间+线程cpu时间)/线程cpu时间) * cpu数量
 * 学习ThreadPoolExecutor
 * @Author lideyin
 * @Date 2019/9/10 0:00
 * @Version 1.0
 */
public class SimpleThreadPoolExecutor {
    //ctl可以理解为controller的简写，此变量由两部分组成，一个变量可以包含两个信息：
    //线程池的运行状态(runState:后面用rs简称)和线程池内有效线程的数量(workCount后面用wc简称)
    //int型变量是由32位组成，所以用高3位来存储运行状态rs,剩下的低29位存储线程池内有效线程wc
    //学好位运算，走遍天下都不怕。。
    private final AtomicInteger ctl = new AtomicInteger();

    //用于存储线程池内有效线程的位数(29)
    private static final int COUNT_BITS = Integer.SIZE - 3;

    //用于存储线程池内有效线程的容量，即(2^29-1).高3位为0，低29位是1的数值(000111...1)
    //小知识点1：如果想得到2^n的n位1，则使用2^n-1
    private static final int CAPACITY = 1 << COUNT_BITS - 1;

    //小知识点2：最高位是1的都是负数，最高位是0的都是正数
    //小知识点3：如何求补码？a.正数的补码是自身（原码=反码=补码）b.负数的补码=原码取反+1
    //线程池的5种运行状态(通过高3位来判断状态)，以下5个常量从小到排列
    private static final int RUNNING = -1 << COUNT_BITS;  //11100000000000000000000000000000
    private static final int SHUTDOWN = 0 << COUNT_BITS;  //00000000000000000000000000000000
    private static final int STOP = 1 << COUNT_BITS;      //00100000000000000000000000000000
    private static final int TIDYING = 2 << COUNT_BITS;   //01000000000000000000000000000000
    private static final int TERMINATED = 3 << COUNT_BITS;//01100000000000000000000000000000

    private ReentrantLock mainLock = new ReentrantLock();
    /**
     * 跟踪线程池的大小.只能在mainLock的锁定情况下访问
     */
    private int largestPoolSize;

    /**
     * 线程池的任务完成数
     */
    private long completedTaskCount;

    /**
     * <b>根据线程池运行状态和工作线程数获取ctl的值</b>
     * 高3位的rs | 低29的wc即可得出ctl的值
     *
     * @param rs 线程池运行状态
     * @param wc 工作线程数
     * @return
     */
    private static int ctlOf(int rs, int wc) {
        return rs | wc;
    }

    /**
     * 根据线程控制器的值获取线程池的运行状态（取三位）
     *
     * @param c 线程控制器的值
     * @return
     */
    private static int runStateOf(int c) {
        return c & ~CAPACITY;//如001010...1 & 111000...0(29位不全部分用...代替，其它地方雷同不再赘述)
    }

    /**
     * 根据线程控制器的值获取工作线程数(取后29位)
     *
     * @param c 线程控制器的值
     * @return
     */
    private static int workerCountOf(int c) {
        return c & CAPACITY;//如001010...1 & 000111...1(29位不全部分用...代替，其它地方雷同不再赘述)
    }

    private volatile int corePoolSize;
    private volatile int maximumPoolSize;
    private volatile long keepAliveTime;
    private volatile TimeUnit timeUnit;
    private volatile boolean allowCoreThreadTimeOut = false;
    private final BlockingQueue<Runnable> workQueue;
    /**
     * 线程中的所有工作线程
     */
    private final HashSet<Worker> workers = new HashSet<>();
    private volatile SimpleRejectedExecutionHandler rejectHandler;

    /**
     * 构造函数
     *
     * @param corePoolSize    核心线程数,即使线程空闲了也不会销毁,非负整数
     * @param maximumPoolSize 线程池的最大线程数,不得小于核心线程数,正整数,
     * @param keepAliveTime   非核心线程的存活时长,非负整数
     * @param timeUnit        非核心线程存活时长的单位
     * @param workQueue       工作队列
     * @param rejectHandler   线程池已满(即线程数为maximumPoolSize)，且无空闲线程，且队列已经达到最大线程数时的拒绝策略
     */
    public SimpleThreadPoolExecutor(int corePoolSize,
                                    int maximumPoolSize,
                                    long keepAliveTime,
                                    TimeUnit timeUnit,
                                    BlockingQueue<Runnable> workQueue,
                                    SimpleRejectedExecutionHandler rejectHandler) {
        if (corePoolSize < 0) {
            throw new InvalidParameterException("Parameter corePoolSize is invalid.");
        }
        if (maximumPoolSize <= 0) {
            throw new InvalidParameterException("Parameter maximumPoolSize is invalid.");
        }
        if (keepAliveTime < 0) {
            throw new InvalidParameterException("Parameter keepAliveTime is invalid.");
        }
        if (corePoolSize > maximumPoolSize) {
            throw new InvalidParameterException("corePoolSize must less than maximumPoolSize.");
        }
        if (workQueue == null) {
            throw new InvalidParameterException("Parameter workQueue is not to be empty.");
        }
        if (rejectHandler == null) {
            throw new InvalidParameterException("Parameter rejectHandler is not to be empty.");
        }
        this.corePoolSize = corePoolSize;
        this.maximumPoolSize = maximumPoolSize;
        this.keepAliveTime = timeUnit.toNanos(keepAliveTime);
        this.timeUnit = timeUnit;
        this.workQueue = workQueue;
        this.rejectHandler = rejectHandler;
    }

    /**
     * 执行指定的线程,有可能创建新线程，也有可能直接使用线程池中现有线程
     * <ol>
     * <li>如果当前线程数小于核心线程数，则尝试添加新线程。</li>
     * <li>如果当前线程数大于核心线程，则添加线程到工作队列中</li>
     * <li>如果队列已满，但是未达到最大线程数，则添加新线程</li>
     * </ol>
     *
     * @param command 需要执行的线程
     */
    public void execute(Runnable command) {
        if (command == null) {
            throw new NullPointerException();
        }

        //如果线程池内的线程数少于核心线程数，则添加新的核心线程
        if (workerCountOf(ctl.get()) < corePoolSize) {
            addWorker(command, true);
            return;
        }

        //如果线程池内的线程数大于核心线程数，且队列未满，则向队列中插数据
        if (isRunning(ctl.get()) && workQueue.offer(command)) {
            addWorker(null, false);
            return;
        }

        //如果线程队列已满，且未达到最大线程数据，则创建新线程;如果达到最大线程，则执行拒绝策略
        if (!addWorker(command, false)) {
            rejectHandler.execute(command);
        }
    }

    //线程池是否正在运行中
    private static boolean isRunning(int c) {
        //因为线程池的运行运行状态是按序排列的，所以比SHUTDOWN小，则认为是在运行中
        return c < SHUTDOWN;
    }

    /**
     * 添加核心线程并执行
     * <b>注意：firstTask是开启线程执行的首个任务，之后常驻在线程池中的线程执行的任务都是从阻塞队列中取出的，需要注意</b>
     *
     * @param firstTask    工作线程
     * @param isCoreThread 是否核心线程
     * @return boolean
     */
    private boolean addWorker(Runnable firstTask, boolean isCoreThread) {
        //当线程池处理running状态时，只有线程池的线程数量+1后才会退出循环，否则会一直执行retry
        retry:
        for (; ; ) {//外层循环用于获取最新状态
            int c = ctl.get();
            //线程池运行状态
            int rs = runStateOf(c);
            //当线程池不在运行中(即SHUTDOWN,STOP,TIDYING,TERMINATED)时，
            // 满足如下条件之一则直接结束，不创建任何新线程，并返回false.
            // 1.状态为STOP,TIDYING,TERMINATED
            // 2.状态为SHUTDOWN,并且task!=null
            // 3.状态为SHUTDOWN,并且队列workQueue为空
            if (rs >= SHUTDOWN &&
                    !(rs == SHUTDOWN && firstTask == null && !workQueue.isEmpty())) {
                return false;
            }

            for (; ; ) {//内层循环是为了保证运行状态rs在running的情况下，线程数量+1的CAS操作的成功
                int wc = workerCountOf(c);
                //如果有效线程池数量>=有效线程的最大容量，或者有效线程池数量>=指定的线程数量（核心线程最大为corePoolSize,
                // 非核心线程最大为maximumPoolSize）时，直接返回false
                if (wc >= CAPACITY || wc >= (isCoreThread ? corePoolSize : maximumPoolSize)) {
                    return false;
                }

                //必须保证cas操作成功才跳出全部循环，执行后续操作
                if (compareAndIncrementWorkerCount(c)) {
                    break retry;//直接跳出外层循环执行外层循环后的代码
                }

                //每次轮循实时取最新ctl值，在轮循过程中，如果状态发生改变，则需要重新从外层循环开始执行
                c = ctl.get();
                if (runStateOf(c) != rs) {
                    continue retry;//跳到外层循环开始处，重新开始执行循环
                }
            }
        }

        //worker和task的区别:Worker是线程池中的线程，而Task虽然是runnable，但是并没有真正执行，只是被Worker调用了run方法
        boolean workerAdded = false;
        boolean workerStarted = false;
        Worker w = null;

        try {
            w = new Worker(firstTask);
            final Thread t = w.thread;
            if (t != null) {
                final ReentrantLock mainLock = this.mainLock;
                mainLock.lock();
                try {
                    //重新校验运行状态
                    int rs = runStateOf(ctl.get());

                    //如果线程池状态为RUNNING,或者是已经关闭且第一个任务还没添加进来，则允许添加工作任务
                    if (rs < SHUTDOWN ||
                            (rs == SHUTDOWN && firstTask == null)) {
                        if (t.isAlive()) {//再次校验线程启动状态，如果已启动则抛出异常
                            throw new SunToolkit.IllegalThreadException();
                        }
                        workers.add(w);
                        int s = workers.size();
                        if (s > largestPoolSize) {
                            largestPoolSize = s;
                        }
                        workerAdded = true;
                    }
                } finally {
                    mainLock.unlock();
                }
                if (workerAdded) {
                    workerStarted = true;
                }
            }
        } finally {
            if (!workerAdded) {
                //如果工作任务添加失败，则需要执行相关的失败处理
                addWorkerFailed(w);
            }
        }


        return workerStarted;
    }

    /**
     * 回滚工作线程的创建
     *
     * @param w
     */
    private void addWorkerFailed(Worker w) {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            //如果worker已添加到工作线程，则删除工作线程
            if (w != null) {
                workers.remove(w);
            }
            //减少工作线程数量
            decrementWorkerCount();
            tryTerminate();
        } finally {
            mainLock.unlock();
        }
    }

    private static final boolean ONLY_ONE = true;

    /**
     * 尝试中断线程
     */
    final void tryTerminate() {
        for (; ; ) {
            int c = ctl.get();
            //如果线程池正在运行，或者线程池状态为TIDING或TERMINATED，
            // 或者线程池状态为已关闭，但是工作队列不为空，则直接返回
            if (isRunning(c) ||
                    atLeastRunState(c, TIDYING) ||
                    (runStateOf(c) == SHUTDOWN && !workQueue.isEmpty())) {
                return;
            }

            //如果做线程数不为0
            if (workerCountOf(c) != 0) {
                //尝试终止最多一个空闲线程，并返回
                interruptIdleWorkers(ONLY_ONE);
                return;
            }

            //清理状态
            final ReentrantLock mainLock = this.mainLock;
            mainLock.lock();
            try {
                // CAS操作，将运行状态置为TIDING.线程数量置为0
                if (ctl.compareAndSet(c, ctlOf(TIDYING, 0))) {
                    try {
                        terminated();
                    } finally {
                        //最后将运行状态置为最终状态TERMINATED，线程数量置为0
                        ctl.set(ctlOf(TERMINATED, 0));
//                        termination.signalAll();//释放所有锁
                    }
                    return;
                }
            } finally {
                mainLock.unlock();
            }
        }
    }

    //默认不做处理，具体实现交由子类去处理
    protected void terminated() {
    }

    ;

    private void interruptIdleWorkers(boolean onlyOne) {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            for (Worker w : workers) {
                Thread t = w.thread;
                //如果线程没有打中断标记，且能worker能拿到锁
                if (!t.isInterrupted() && w.tryLock()) {
                    try {
                        t.interrupt();//中断线程
                    } catch (SecurityException e) {
                        //do nothing.
                    } finally {
                        w.unlock();
                    }
                }
                //这么写有可能一个也没有终止
                if (onlyOne) {
                    break;
                }
            }
        } finally {
            mainLock.unlock();
        }
    }

    /**
     * 对工作线程进行cas操作,增加工作线程数量
     *
     * @param expect 期望值
     * @return
     */
    private boolean compareAndIncrementWorkerCount(int expect) {
        return ctl.compareAndSet(expect, expect + 1);
    }

    /**
     * 对工作线程进行cas操作，减少工作线程数量
     *
     * @param expect 期望值
     * @return
     */
    private boolean compareAndDecrementWorkerCount(int expect) {
        return ctl.compareAndSet(expect, expect - 1);
    }

    final void runWorker(Worker worker) {
        Thread currentThread = Thread.currentThread();
        Runnable task = worker.firstTask;
        worker.firstTask = null;
        worker.unlock();//先解锁，允许线程中止
        //是否突然完成。正常情况下是没有worker,将此变更赋值为非突然完成false，
        // 只有在出现异常的情况下，直接执行了finally才可能会出现此值为true的情况
        boolean completeAbruptly = true;
        try {
            //只要有任务，则一直执行（任务可能是第一个任务，也可能是从线程池中拿的任务）
            while (task != null || (task = getTask()) != null) {
                worker.lock();
                //判断运行状态为stop,tidying,terminated时，给当前线程打上中断标识,否则清除中断标识
                //1.如果线程池状态>=STOP,且当前线程没有设置中断标识，则调用中断方法interrupt
                //2.如果一开始判断线程池状态<STOP，但线程已经被中断，且清除了状态（即Thread.interrupted())==true），
                // 再次判断线程池状态是否>=STOP，且当前线程没有设置中断标识，则调用中断方法interrupt
                if ((atLeastRunState(ctl.get(), STOP) ||
                        Thread.interrupted() && atLeastRunState(ctl.get(), STOP)) && !currentThread.isInterrupted()) {
                    currentThread.interrupt();
                }

                try {
                    Throwable throwable = null;
                    beforeExecute(worker, task);
                    try {
                        task.run();
                    } catch (Throwable e) {
                        throwable = e;
                        throw e;
                    } finally {
                        afterExecute(task, throwable);
                    }

                } finally {
                    task = null;
                    worker.completedTasks++;
                    worker.unlock();
                }
            }
            completeAbruptly = false;//循环执行完毕将是否突然完成字段置为false.
        } finally {
            //任务执行完了，则退出任务
            processWorkerExit(worker, completeAbruptly);
        }
    }

    protected void afterExecute(Runnable task, Throwable throwable) {
        //TODO 可以打点日志啥的
    }

    protected void beforeExecute(Worker worker, Runnable task) {
        //TODO 可以打点日志啥的
    }

    private boolean atLeastRunState(int c, int s) {
        return c >= s;
    }

    private static boolean runStateLessThan(int c, int s) {
        return c < s;
    }

    private Runnable getTask() {
        //从线程池队列中拿数据是否超时
        boolean timedOut = false;
        //循环从队列中取数据
        for (; ; ) {
            int c = ctl.get();
            int rs = runStateOf(c);

            //如果运行状态>=STOP或者运行状态>=SHUTDOWN并且工作队列为空,则减少工作线程数量并返回null
            if (rs >= SHUTDOWN && (rs >= STOP || workQueue.isEmpty())) {
                decrementWorkerCount();
                return null;
            }

            int wc = workerCountOf(c);

            //是否允许超时回收
            //如果手动开启允许核心线程超时，或者当前工作线程大于核心线程，都是可以超时回收
            boolean timed = allowCoreThreadTimeOut || wc > corePoolSize;

            //1.如果线程池中的线程数量大于最大线程数（理论上讲线程池数量不可能大于最大线程数的，
            // 但是如果手动设置了setMaximumPoolSize方法，则可能导致如下情况）
            // 2.或者设置了允许超时，并且已经超时，意味着空闲线程可以进行回收
            if ((wc > maximumPoolSize || (timed && timedOut))
                    && (wc > 1 || workQueue.isEmpty())) {
                //如果成功的对线程数量进行cas操作-1,则返回null,执行清空线程操作
                if (compareAndDecrementWorkerCount(wc)) {
                    return null;
                }
                //否则一直循环重试
                continue;
            }

            try {
                //如果允许超时(核心线程配置可以超时或者存在非核心线程数)，则在超时时间内获取工作队列
                //否则阻塞线程
                Runnable r = timed ?
                        workQueue.poll(keepAliveTime, TimeUnit.NANOSECONDS) :
                        workQueue.take();
                //如果在超时时间内获取到任务，则直接返回
                if (r != null) {
                    return r;
                }

                //如果超时仍未获取到任务，则将timeOut置为true.继续循环
                timedOut = true;
            }
            //take阻塞会调ReentrantLock的lockInterruptibly()方法，即调用AQS的sync.acquireInterruptibly()方法
            //所以当线程发出中断信号时，会抛出异常，然后继续执行循环
            catch (InterruptedException e) {
                timedOut = false;
            }

        }
    }

    //减少工程线程的数量，如果没有成功则一直循环直到成功
    private void decrementWorkerCount() {
        do {
        } while (!compareAndDecrementWorkerCount(ctl.get()));
    }

    //worker执行完毕的清理动作，只有worker执行完毕后调用
    private void processWorkerExit(Worker worker, boolean completeAbruptly) {
        //1.如果是突然终止，说明task执行有异常，那么正在进行的worker线程数量需要手动-1
        //如果是正常终止，说明task正常执行完，而worker线程的数量在getTask时已经-1了，所不需要做-1操作
        if (completeAbruptly) {
            decrementWorkerCount();
        }

        //2.移除线程池中的工作线程
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            completedTaskCount += worker.completedTasks;//将worker中的任务完成数追加到线程池任务完成数
            workers.remove(worker);//删除HashSet中的worker
        } finally {
            mainLock.unlock();
        }

        //3.尝试终止线程,判断线程池是否满足终止的状态，
        // 如果状态满足，但是还有线程正在运行，则发出中断的信号，使其能退出线程，没有线程执行了，则更新状态tidying->terminated
        tryTerminate();

        //4.判断是否需要新加worker.
        int c = ctl.get();
        //如果状态<STOP,即状态为RUNNING或SHUTDOWN，即上一步tryTerminate()没有成功终止线程，尝试再添加一个worker.
        if (runStateLessThan(c, STOP)) {
            //如果线程不是突然完成的，即没有task任务来完成了，计算最小的线程池数量，
            // 并根据当前的worker数量来判断是否需要addWorker.
            if (!completeAbruptly) {
                int min = allowCoreThreadTimeOut ? 0 : corePoolSize;//最小线程数=如果是允许核心线程超时，则为0，否则为核心线程数
                //如果min为0,则不需要维持核心线程数量，但是同时工作队列workQueue不为空，则需要至少维持一个线程
                if (min == 0 && !workQueue.isEmpty()) {
                    min = 1;
                }

                //如果当前的线程池数量大于等最小线程数量，则直接返回，不需要新加addWorker.
                if (workerCountOf(c) >= min) {
                    return;
                }
            }
            //添加一个没有firstTask的worker
            //如果线程是突然中止的，则需要添加一个新的worker.即便当前线程池的状态为SHUTDOWN
            addWorker(null, false);
        }
    }


    private class Worker extends AbstractQueuedSynchronizer implements Runnable {

        final Thread thread;
        Runnable firstTask;
        volatile long completedTasks;

        public Worker(Runnable firstTask) {
            setState(-1);
            this.firstTask = firstTask;
            //巧妙的用法，调用thread的run方法即是调用worker对象的run方法
            this.thread = Executors.defaultThreadFactory().newThread(this);
        }

        @Override
        public void run() {
            //调用外部的runWorker方法
            runWorker(this);
        }

        /**
         * 是否被独占
         * 0代表被未锁定状态
         * 1代表上锁状态
         *
         * @return
         */
        protected boolean isHeldExclusively() {
            return getState() != 0;
        }

        //获取锁
        protected boolean tryAcquire() {
            //采用cas操作，确保状态是从0到1
            if (compareAndSetState(0, 1)) {
                //将当前线程设置为独占线程
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }

        protected boolean tryRelease() {
            //清空独占线程
            setExclusiveOwnerThread(null);
            setState(0);
            return true;
        }

        public void lock() {
            acquire(1);
        }

        public boolean tryLock() {
            return tryAcquire();
        }

        public void unlock() {
            release(1);
        }

        public boolean isLocked() {
            return isHeldExclusively();
        }

        void interruptIfStarted() {
            Thread t;
            if (getState() >= 0 && (t = thread) != null && t.isInterrupted()) {
                t.interrupt();
            }
        }

    }
}
