package com.ssm.test;

import java.util.ArrayList;
import java.util.List;

public class threadTest extends Thread{
	/*public static void main(String[] args) {
		List list = new ArrayList();
          list.add("qqyumidi");
          list.add("corn");
          list.add(100);
		  
		List<String> list = new ArrayList<>();
		list.add("qqyumidi");
        list.add("corn");
        //list.add(100);
          for (int i = 0; i < list.size(); i++) {
             String name = (String) list.get(i); // 1
             System.out.println("name:" + name);
         }
		persion<String> name = new persion<>("zyy");
		persion<Integer> age = new persion<>(18);
		System.out.println("name class:"+name.getClass());
		System.out.println("age class:"+age.getClass());
		System.out.println(age.getClass() == name.getClass());
		
		persion<Integer> age2 = new persion<Integer>(18);
		persion<Number> name2 = new persion<Number>(18);
		getData(name2);
		getData(age2);
	}
	public static void getData(persion<?> persion){
		System.out.println(persion.getData());
	}*/
	public threadTest(String name){
		super(name);
	}
	public void run() {
		for (int i = 0; i < 5; i++) {
			synchronized (this) {
				System.out.println(this.getName()+":"+i);
			}
		}
	}
	public static void main(String[] args) {
		System.out.println("********");
		Thread t1 = new threadTest("张三");
		Thread t2 = new threadTest("李四");
		t1.start();
		t2.start();
		System.out.println("------");
	}
}
