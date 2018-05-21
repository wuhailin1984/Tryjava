package com.example.lib;


import java.util.*;

public class myClass {
    public static void main(String[] args){
        System.out.println(new Date());
        Properties p=System.getProperties();
        p.list(System.out);
        System.out.println("--Memory usage:");
        Runtime rt=Runtime.getRuntime();
        System.out.println("Total memory= "+rt.totalMemory()+"   Free memory= "+rt.freeMemory());

        System.out.println(args);

        try {
            Thread.currentThread().sleep(5*1000);
        } catch(InterruptedException e) {}

    }
}
