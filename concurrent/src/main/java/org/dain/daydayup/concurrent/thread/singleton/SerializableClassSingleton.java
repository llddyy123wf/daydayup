package org.dain.daydayup.concurrent.thread.singleton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;

public class SerializableClassSingleton {
	public static void main(String[] args) {

		try {
			MyObject obj = MyObject.getInstance();
			FileOutputStream fos = new FileOutputStream(new File("myObjectFile.txt"));
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(obj);
			oos.close();
			fos.close();
			System.out.println(obj.hashCode());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			FileInputStream fis = new FileInputStream(new File("myObjectFile.txt"));
			ObjectInputStream ois=new ObjectInputStream(fis);
			MyObject obj2 = (MyObject)ois.readObject();
			ois.close();
			fis.close();
			System.out.println(obj2.hashCode());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

class MyObject implements Serializable {
	private static class MyObjectHandler {
		private static final MyObject obj = new MyObject();
	}

	private MyObject() {
	}

	public static MyObject getInstance() {
		return MyObjectHandler.obj;
	}

/*	在jdk中ObjectInputStream的类中有readUnshared（）方法，上面详细解释了原因。
	我简单描述一下，那就是如果被反序列化的对象的类存在readResolve这个方法，他会调用这个方法*/
	protected Object readResolve() throws ObjectStreamException {
		System.out.println("invoked readSolve....");
		return MyObjectHandler.obj;
	}
}
