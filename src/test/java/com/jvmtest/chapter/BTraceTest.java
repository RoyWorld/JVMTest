package com.jvmtest.chapter;

/**
 * 与Chapter4中的testBTraceTest()搭配使用
 * Created by RoyChan on 2018/1/16.
 */
public class BTraceTest {
    public int add(int a, int b){
        return a + b;
    }
}

/**
 * 以下code是经过test, 能在BTrace中运行并看到打印信息
 */
/* BTrace Script Template */
//import com.sun.btrace.annotations.*;
//        import static com.sun.btrace.BTraceUtils.*;
//
//@BTrace
//public class TracingScript {
//
//    @OnMethod(clazz="com.jvmtest.chapter.BTraceTest",method="add",location=@Location(Kind.RETURN))
//    public static void func(@Self com.jvmtest.chapter.BTraceTest instance, int a, int b, @Return int result) {
//        println("heap and stack:");
//        jstack();
//        println(strcat("method A:", str(a)));
//        println(strcat("method B:", str(b)));
//        println(strcat("method result:", str(result)));
//    }
//
//}