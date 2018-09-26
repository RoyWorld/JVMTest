package com.jvmtest.chapter;

import com.jvmtest.Pilot;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by RoyChan on 2018/2/1.
 */
public class ThreadTest {

    @Test
    public void test() throws InterruptedException {
        final List list = new ArrayList();

        final CountDownLatch countDownLatch = new CountDownLatch(1000);
        for (int i = 0; i < 1000; i++) {
            final int a = i;
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    list.add(new Pilot("thread-"+a, a));
                    countDownLatch.countDown();
                }
            });
            thread.start();
        }

        countDownLatch.await();
        System.out.println(list.size());
    }
}
