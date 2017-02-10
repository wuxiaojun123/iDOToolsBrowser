package com.example;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuxiaojun on 17-2-10.
 */

public class TestList {


    public static void main(String[] args){

        List<String> list = new ArrayList<>();
        list.add("aa");
        list.add("bb");
        list.add("cc");
        list.add("dd");
        list.add("ee");
        list.add("ff");

        for (int i=0;i < list.size();i++){
            if("aa".equals(list.get(i))){
                list.remove(i);
                break;
            }
        }

        for (int i=0;i < list.size();i++){
            System.out.println("东东是："+list.get(i));
        }

    }


}
