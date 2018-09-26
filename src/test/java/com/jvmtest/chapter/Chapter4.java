package com.jvmtest.chapter;

import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by RoyChan on 2018/1/15.
 */
public class Chapter4 {

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
        System.gc();
    }

    /**
     * -ea -verbose:gc -Xms300M -Xmx300M -XX:SurvivorRatio=8 -XX:+PrintGCDetails -XX:MaxTenuringThreshold=15 -XX:+PrintTenuringDistribution -XX:+UseSerialGC
     * @throws InterruptedException
     */
    @Test
    public void testHeap()throws InterruptedException {
        fillHeap(3000);
//        2 这个gc会同时将老年代的数据清空
//        System.gc();
//        System.gc后的长时间等待, 以观察gc的效果
        Thread.sleep(100000);
        System.out.println("have sleep");
    }

    public static void createBusyThread(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true)
                    ;
            }
        }, "testBusyThread");

        thread.start();
    }

    public static void createLockThread(final Object lock){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (lock){
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, "testLockThread");

        thread.start();
    }

    /**
     * 模拟console输入
     */
    private void robotIn(){
        String data = "Hello, World!\r\n";
        System.setIn(new ByteArrayInputStream(data.getBytes()));
    }

    /**
     * 由于在JUnit中测试是无法对console进行输入, 所以console的输入要靠代码模拟
     * @throws InterruptedException
     * @throws IOException
     */
    @Test
    public void testStack() throws InterruptedException, IOException {
//        等待5s后进行模拟输入
        Thread.sleep(5000);
        robotIn();

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println(br.readLine());

//        等待5s后进行模拟输入
        Thread.sleep(5000);
        robotIn();

        createBusyThread();
        System.out.println(br.readLine());
        Object obj = new Object();
        createLockThread(obj);
        String str;
        System.out.println("Enter lines of text.");
        System.out.println("Enter 'stop' to quit.");

        do {
//            等待3s后进行模拟输入
            Thread.sleep(3000);
            robotIn();
            Scanner scanner = new Scanner(System.in);
            str = scanner.nextLine();
            System.out.println(str);
        } while(!str.equals("stop"));
    }

    @Test
    public void testDeadLock() throws IOException {
        for (int i = 0; i < 100; i++) {
            new Thread(new SynAddRunable(1, 2)).start();
            new Thread(new SynAddRunable(2, 1)).start();
        }
//        死循环等待, 用来观察线程死锁
        for(;;){
            ;
        }
    }

    @Test
    public void testBTraceTest() throws IOException, InterruptedException {
        BTraceTest test = new BTraceTest();

        for (;;) {
            Thread.sleep(5000);
            int a = (int) Math.round(Math.random() * 1000);
            int b = (int) Math.round(Math.random() * 1000);
            System.out.println(test.add(a, b));
        }
    }

}

class SynAddRunable implements Runnable {
    int a, b;

    public SynAddRunable(int a, int b){
        this.a = a;
        this.b = b;
    }

    @Override
    public void run() {
        synchronized (Integer.valueOf(a)){
            synchronized (Integer.valueOf(b)){
                System.out.println(a + b);
            }
        }
    }
}
