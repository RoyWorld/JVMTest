package com.jvmtest;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by RoyChan on 2018/1/29.
 */
public class OOMTest {


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
     * vm args: -ea -Xss2M
     * danger
     */
    @Test
    public void vmStackOOM() throws InterruptedException {
        OOMTest jvmTest = new OOMTest();
        jvmTest.stackLeakByThread();
    }

    /**
     * vm args: -ea -Xss128k
     */
    @Test
    public void vmStackSOE() {
        OOMTest jvmTest = new OOMTest();
        try{
            jvmTest.stackLeak();
        } catch (Throwable e){
            System.out.println("stack length:" + jvmTest.stackLength);
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
     * vm args: -ea -XX:PermSize=10M -XX:MaxPermSize=10M
     */
    @Test
    public void vmMethodAreaOOM() throws InterruptedException {
        while (true){
            Thread.sleep(100);
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(OOMTest.OOMObject.class);
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
}
