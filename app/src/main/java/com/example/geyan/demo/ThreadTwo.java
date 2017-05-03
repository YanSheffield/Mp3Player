package com.example.geyan.demo;

/**
 * Created by geyan on 03/05/2017.
 */

public class ThreadTwo implements Runnable{

    @Override
    public void run() {
        for (int i = 0;i<100000;i++){
            System.out.println("chicken");
        }
    }
}
