package org.dain.daydayup.concurrent.thread.blockingqueue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class PriorityBlockingQueueTest {

	private final static PriorityBlockingQueue<PriorityNode> queue = new PriorityBlockingQueue<>();

	public static void main(String[] args) throws InterruptedException {
		PriorityNode n1 = new PriorityNode(1, "11");
		PriorityNode n2 = new PriorityNode(2, "22");
		PriorityNode n3 = new PriorityNode(3, "33");
		PriorityNode n4 = new PriorityNode(4, "44");
		PriorityNode n5 = new PriorityNode(5, "55");
		PriorityNode n6 = new PriorityNode(6, "66");
		PriorityNode n7 = new PriorityNode(6, "666");
		
		new Thread(()->{
			queue.add(n5);
		}).start();
		new Thread(()->{
			queue.add(n6);
		}).start();
		Thread.sleep(1000);
		queue.add(n2);
		queue.add(n3);
		queue.add(n4);
		queue.add(n1);
		if	(!queue.contains(n7)) {
			queue.add(n6);
			queue.add(n6);	
		}
		List<PriorityNode> resList = new ArrayList<>();
		queue.drainTo(resList,10);
		for	(PriorityNode res:resList) {
			System.out.println("大小："+resList.size());
			System.out.println(res.getName());
		}
//
//		while (!queue.isEmpty()) {
//			System.out.println(queue.take().getName());
//			System.out.println(queue.toString());
//		}

	}
}

class PriorityNode implements Comparable<PriorityNode> {
	private int order;
	private String name;

	public PriorityNode(int order, String name) {
		super();
		this.order = order;
		this.name = name;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int compareTo(PriorityNode o) {
		return this.order > o.getOrder() ? -1 : this.order == o.getOrder() ? 0 : 1;
	}

	public String toString() {
		return this.order+": "+this.name;
	}
	
	public boolean equals(Object obj) {
        return (this.order == ((PriorityNode)obj).getOrder());
    }
}
