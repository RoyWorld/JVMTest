package com.jvmtest.chapter;

import org.junit.Test;

import javax.security.auth.Subject;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by RoyChan on 2018/1/31.
 */
public class Chapter5 {

    /**
     * -XX:+TraceClassLoading
     */
    @Test
    public void test1(){
//        System.out.println(SubClass.value);

        SuperClass[] subClasses = new SuperClass[10];
    }

    @Test
    public void test2() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        ClassLoader myLoader = new ClassLoader() {
            @Override
            public Class<?> loadClass(String name) throws ClassNotFoundException {
                try {
                    String fileName = name.substring(name.lastIndexOf(".") + 1) + ".class";
                    InputStream is = getClass().getResourceAsStream(fileName);
                    if (is == null){
                        return super.loadClass(name);
                    }
                    byte[] b = new byte[is.available()];
                    is.read(b);
                    return defineClass(name, b, 0, b.length);
                } catch (IOException e) {
                    throw new ClassNotFoundException(name);
                }
            }
        };

        Object obj = myLoader.loadClass("com.jvmtest.chapter.Chapter4").newInstance();

        System.out.println(obj.getClass());
        System.out.println(obj instanceof com.jvmtest.chapter.Chapter4);
    }
}

class SuperClass{
    static {
        System.out.println("SuperClass init!");
    }

    public static int value = 123;
}

class SubClass extends SuperClass{
    static {
        System.out.println("SubClass init!");
    }
}

class ConstClass{
    static {
        System.out.println("ConstClass init!");
    }

    public static final String H = "hello world";
}