package org.dain.daydayup.concurrent.thread.gotolable;

/**
 * @Description TODO
 * @Author lideyin
 * @Date 2019/9/11 22:39
 * @Version 1.0
 */
public class GoToLableTest {
    public static void main(String[] args) {
        continueRetry();
        continueWithoutRetry();
        breakRetry();
        breakWithoutRetry();
    }

    public static void continueRetry() {

        int count = 0;

        System.err.println("------------------------------------" +
                Thread.currentThread().getStackTrace()[1].getMethodName() + "的结果-----------------------");
        retry:
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 3; j++) {
                count++;
                if (count == 2) {
                    continue retry;
                }
                System.out.print(count + "(i=" + i + ",j=" + j + ") ");
            }
            System.out.println("outer loop:i=" + i);
        }
    }

    public static void continueWithoutRetry() {

        int count = 0;

        System.err.println("------------------------------------" +
                Thread.currentThread().getStackTrace()[1].getMethodName() + "的结果-----------------------");
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 3; j++) {
                count++;
                if (count == 2) {
                    continue;
                }
                System.out.print(count + " ");
            }
            System.out.println("outer loop:i=" + i);
        }
    }

    public static void breakRetry() {
        int count = 0;
        System.err.println("------------------------------------" +
                Thread.currentThread().getStackTrace()[1].getMethodName() + "的结果-----------------------");
        retry:
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 3; j++) {
                count++;
                if (count == 2) {
                    break retry;
                }
                System.out.println(count + "(i=" + i + ",j=" + j + ") ");
            }
            System.out.println("outer loop:i=" + i);
        }
    }

    public static void breakWithoutRetry() {
        int count = 0;
        System.err.println("------------------------------------" +
                Thread.currentThread().getStackTrace()[1].getMethodName() + "的结果-----------------------");
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 3; j++) {
                count++;
                if (count == 2) {
                    break;
                }
                System.out.print(count + " ");
            }
            System.out.println("outer loop:i=" + i);
        }
    }
}
