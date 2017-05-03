package com.example.geyan.demo;

/**
 * Created by geyan on 03/05/2017.
 */

public class main {
    public static void main(String[] args){
        ThreadOne threadOne = new ThreadOne();
        Thread thread = new Thread(threadOne);
        thread.start();
    }
}
