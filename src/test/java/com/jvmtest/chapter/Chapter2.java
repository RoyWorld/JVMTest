package com.jvmtest.chapter;

import com.jvmtest.Pilot;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by RoyChan on 2018/1/12.
 */
public class Chapter2 {

    private int stackLength = 1;

    static class OOMObject {
    }

    public void stackLeak() {
        stackLength++;
        stackLeak();
    }

    /**
     * vm args: -ea -Xms20m -Xmx20m -XX:+HeapDumpOnOutOfMemoryError
     */
    @Test
    public void testOOM() throws InterruptedException {
        List<OOMObject> list = new ArrayList<>();

        int i = 0;
        while (true) {
            if (i % 100 == 0){
                Thread.sleep(1);
            }
            i++;
            list.add(new OOMObject());
        }
    }

    /**
     * vm args: -ea -Xms20m -Xmx20m -XX:-UseGCOverheadLimit -XX:+HeapDumpOnOutOfMemoryError
     */
    @Test
    public void testOOMMemoryLeak() throws InterruptedException {
        Map<String, Pilot> map = new HashMap<>();
        Object[] array = new Object[1000000];
        for(int i= 0; i < 1000000; i++){
            if (i % 100 == 0){
                Thread.sleep(100);
            }
            String d = new Date().toString();
            Pilot p = new Pilot(d, i);
            map.put(i+"rosen jiang", p);
            array[i]=p;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Map<String, Pilot> map = new HashMap<>();
        Object[] array = new Object[1000000];
        for(int i= 0; i < 1000000; i++){
            if (i % 100 == 0){
                Thread.sleep(100);
            }
            String d = new Date().toString();
            Pilot p = new Pilot(d, i);
            map.put(i+"rosen jiang", p);
            array[i]=p;
        }

    }

    /**
     * vm args: -ea -Xss128k
     */
    @Test
    public void vmStackSOE() {
        Chapter2 chapter2 = new Chapter2();
        try{
            chapter2.stackLeak();
        } catch (Throwable e){
            System.out.println("stack length:" + chapter2.stackLength);
            throw e;
        }
    }

    private void dontStop(){
        while(true){

        }
    }

    public void stackLeakByThread() throws InterruptedException {
        int i = 1;
        while (i < 1000){
            i++;
            Thread.sleep(10);
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    dontStop();
                }
            });
            thread.start();
        }
    }

    /**
     * vm args: -ea -Xss2M
     * danger
     */
    @Test
    public void vmStackOOM() throws InterruptedException {
        Chapter2 chapter2 = new Chapter2();
        chapter2.stackLeakByThread();
    }

    /**
     * vm args: -ea -XX:PermSize=10M -XX:MaxPermSize=10M
     */
    @Test
    public void vmMethodAreaOOM() throws InterruptedException {
        while (true){
            Thread.sleep(100);
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(OOMObject.class);
            enhancer.setUseCache(false);
            enhancer.setCallback(new MethodInterceptor() {
                @Override
                public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                    return methodProxy.invokeSuper(o, objects);
                }
            });
            enhancer.create();
        }
    }

    @Test
    public void test(){
        String[] alpha = new String[]{
                "q","w","e","r","t","y","u","i","o","p","a","s","d","f","g","h","j","k","l","z","x","c","v","b","n","m"
        };

        Random random = new Random(System.currentTimeMillis());
        for (int i = 10; i < 10000; i++){
            int num = 5;
            String s = "";
            for (int j = 0; j < num; j++){
                int index = (random.nextInt(Integer.MAX_VALUE - 26) + 26) % 26;
                s += alpha[index];
            }

            System.out.println(s);
        }
    }

}

