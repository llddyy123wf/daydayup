package org.dain.daydayup.concurrent.thread.masterworker;

public class Task {
private int id;
private int count;


public Task(int id, int count) {
	super();
	this.id = id;
	this.count = count;
}
public int getId() {
	return id;
}
public void setId(int id) {
	this.id = id;
}
public int getCount() {
	return count;
}
public void setCount(int count) {
	this.count = count;
}

}
