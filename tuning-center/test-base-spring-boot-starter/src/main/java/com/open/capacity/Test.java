package com.open.capacity;

import java.io.File;
import java.text.DecimalFormat;

import cn.hutool.system.oshi.CpuInfo;
import cn.hutool.system.oshi.OshiUtil;

public class Test {
    public static void main(String[] args) {
    	//获取cpu利用率
        getOsInfo();
        //获取内存数据
        getMemoryInfo();
        //获取硬盘使用量
        getDiskUsed();
    }

    /**
     * 获取cpu利用率
     */
    public static void getOsInfo(){
        CpuInfo cpuInfo = OshiUtil.getCpuInfo();
        double free = cpuInfo.getFree();
        DecimalFormat format = new DecimalFormat("#.00");
        System.out.println("cpu利用率：" + Double.parseDouble(format.format(100.0D - free)));
    }


    /**
     * 获取硬盘使用量
     */
    public static void getDiskUsed(){
        File win = new File("/");
        if (win.exists()) {
            long total = win.getTotalSpace();
            long freeSpace = win.getFreeSpace();
            System.out.println("磁盘总量：" + total/1024/1024/1024);
            System.out.println("磁盘剩余总量：" + freeSpace/1024/1024/1024);
            System.out.println("磁盘已用总量：" + (total - freeSpace)/1024/1024/1024);
        }
    }
}
 