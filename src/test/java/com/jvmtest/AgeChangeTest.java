package com.jvmtest;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RoyChan on 2018/1/30.
 */
public class AgeChangeTest {
    static class OOMObject{
        public byte[] placeholder = new byte[64 * 1024];
    }

    public static void fillHeap(int num) throws InterruptedException {
        List<OOMObject> list = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            Thread.sleep(50);
            list.add(new OOMObject());
        }
//        1 这个gc不会将老年代的数据清空
        System.gc();//1
    }

    /**
     * -ea -verbose:gc -Xms300M -Xmx300M -XX:SurvivorRatio=8 -XX:+PrintGCDetails -XX:MaxTenuringThreshold=15 -XX:+PrintTenuringDistribution -XX:+UseSerialGC
     * @throws InterruptedException
     */
    @Test
    public void testHeap()throws InterruptedException {
        fillHeap(3000);
//        2 这个gc会同时将老年代的数据清空
//        System.gc();//2

//        System.gc后的长时间等待, 以观察gc的效果
        Thread.sleep(100000);
        System.out.println("have sleep");
    }
}
