package com.example;

public class MyClass {

    public static void main(String[] args) {

        String a = "https://i1.wp.com/wp.cgameclub.com/wp-content/uploads/2016/12/giphy-4-1.gif?resize=175%2C131";
        //获取路径上的gif图片名称
        System.out.println(a.substring(a.lastIndexOf("/")+1, a.length()));

        String b = "https://www.youtube.com/embed/5WS11UesMj8";
        System.out.println("id 是"+b.substring(b.lastIndexOf("/")+1));

        System.out.println("获取到的字符串是："+getStr());

    }

    private static String getStr(){

        try {
            String a = null;
            a.lastIndexOf("/");
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
