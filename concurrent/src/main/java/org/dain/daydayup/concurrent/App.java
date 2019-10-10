package org.dain.daydayup.concurrent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class App {

    public static void main(String[] args) {
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        //1.没问题
//        for (int i = 0; i < list.size(); i++) {
//            if (i == 1) {
//                list.remove(i);
//            }
//        }

        //2.此种方式没问题
//        for (int i:list) {
//            if (i == 1) {
//                list.remove(i);
//            }
//        }

        //3.报错
//        Iterator<Integer> iterator = list.iterator();
//        while (iterator.hasNext()){
//            Integer t = iterator.next();
//            if  (t.intValue()==2){
//                list.remove(t);
//            }
//        }

        //4.成功
        Integer[] array = list.toArray(new Integer[list.size()]);
        for (int i=0;i<array.length;i++){
            Integer t =array[i];
            if (t==1){
                list.remove(t);
            }
        }

        System.out.println(list.size());
    }
}
