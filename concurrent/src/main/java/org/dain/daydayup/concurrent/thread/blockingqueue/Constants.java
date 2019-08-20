package org.dain.daydayup.concurrent.thread.blockingqueue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class Constants {
	public static final BlockingQueue<Integer> blockingQueue = new LinkedBlockingQueue<Integer>(10);
	public static List<Integer> dbList = new CopyOnWriteArrayList<Integer>(Arrays.asList(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21));
	public static volatile boolean isProducted=false;//是否已生产
}
