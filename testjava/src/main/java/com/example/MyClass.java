package com.example;

public class MyClass {

    public static void main(String[] args) {

        String a = "https://i1.wp.com/wp.cgameclub.com/wp-content/uploads/2016/12/giphy-4-1.gif?resize=175%2C131";
        //获取路径上的gif图片名称
        System.out.println(a.substring(a.lastIndexOf("/")+1, a.length()));

    }

}
