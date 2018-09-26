package com.jvmtest.chapter;


import org.junit.Test;

/**
 *
 * Created by RoyChan on 2018/1/12.
 */
public class Chapter3 {

    public Object instance = null;

    public static final int _1MB = 1024 * 1024;

    private byte[] bigSize = new byte[2 * _1MB];

    @Test
    public void testGC(){
        Chapter3 chapter3A = new Chapter3();
        Chapter3 chapter3B = new Chapter3();

        chapter3A.instance = chapter3B;
        chapter3A.instance = chapter3B;

        chapter3A = null;
        chapter3B = null;

        System.gc();
    }

    @Test
    public void testFinalizeEscapeGC() throws Throwable{
        FinalizeEscapeGC.SAVE_HOOK = new FinalizeEscapeGC();

        FinalizeEscapeGC.SAVE_HOOK = null;
        System.gc();

        Thread.sleep(500);

        if (FinalizeEscapeGC.SAVE_HOOK != null){
            FinalizeEscapeGC.SAVE_HOOK.isAlive();
        } else {
            System.out.println("dead");
        }

        FinalizeEscapeGC.SAVE_HOOK = null;

        System.gc();

        Thread.sleep(500);

        if (FinalizeEscapeGC.SAVE_HOOK != null){
            FinalizeEscapeGC.SAVE_HOOK.isAlive();
        } else {
            System.out.println("dead");
        }
    }

    /**
     * -ea -verbose:gc -Xms20M -Xmx20M -Xmn10M -XX:SurvivorRatio=8 -XX:+PrintGCDetails
     *
     * [GC [PSYoungGen: 6211K->1016K(9216K)] 6211K->3229K(19456K), 0.0052204 secs] [Times: user=0.00 sys=0.00, real=0.01 secs]
     Heap
   -  PSYoungGen      total 9216K, used 7375K [0x00000000ff600000, 0x0000000100000000, 0x0000000100000000)
      eden space 8192K, 77% used [0x00000000ff600000,0x00000000ffc35f58,0x00000000ffe00000)
      from space 1024K, 99% used [0x00000000ffe00000,0x00000000ffefe030,0x00000000fff00000)
      to   space 1024K, 0% used [0x00000000fff00000,0x00000000fff00000,0x0000000100000000)
   -  ParOldGen       total 10240K, used 6309K [0x00000000fec00000, 0x00000000ff600000, 0x00000000ff600000)
      object space 10240K, 61% used [0x00000000fec00000,0x00000000ff229660,0x00000000ff600000)
   -  PSPermGen       total 21504K, used 4460K [0x00000000f9a00000, 0x00000000faf00000, 0x00000000fec00000)
      object space 21504K, 20% used [0x00000000f9a00000,0x00000000f9e5b2b8,0x00000000faf00000)

     */
    @Test
    public void testAllocation(){
        byte[] allocation1, allocation2, allocation3, allocation4;

        allocation1 = new byte[2 * _1MB];
        allocation2 = new byte[2 * _1MB];
        allocation3 = new byte[2 * _1MB];
        allocation4 = new byte[4 * _1MB];
    }

    /**
     * -verbose:gc -Xms20M -Xmx20M -Xmn10M -XX:SurvivorRatio=8 -XX:+PrintGCDetails
     * -XX:PretenureSizeThreshold=3M
     */
    @Test
    public void testPretenureSizeThreshold(){
        byte[] allocation;

        allocation = new byte[4 * _1MB];
    }

    /**
     * -verbose:gc -Xms20M -Xmx20M -Xmn10M -XX:SurvivorRatio=8 -XX:+PrintGCDetails -XX:MaxTenuringThreshold=1
     * -XX:+PrintTenuringDistribution
     * -XX:+UseSerialGC
     * 测试的有误差应该是来源与动态对象年龄判断
     */
    @Test
    public void testTenuringThreshold(){
        byte[] allocation1, allocation2, allocation3;

        allocation1 = new byte[_1MB / 4];

        allocation2 = new byte[4 * _1MB];
//        只进行一次GC来观察效果
        allocation3 = new byte[4 * _1MB];

//        allocation3 = null;
//        allocation3 = new byte[4 * _1MB];
    }

    @Test
    public void testTenuringThreshold2(){
        byte[] allocation1, allocation2, allocation3, allocation4;

        allocation1 = new byte[_1MB / 4];
        allocation4 = new byte[_1MB / 4];
        allocation2 = new byte[4 * _1MB];
        allocation3 = new byte[4 * _1MB];//第一次GC
        allocation3 = null;
        allocation3 = new byte[4 * _1MB];
    }

    /**
     * -verbose:gc -Xms20M -Xmx20M -Xmn10M -XX:SurvivorRatio=8 -XX:+PrintGCDetails -XX:-HandlePromotionFailure
     * -XX:+UseSerialGC
     */
    @Test
    public void testHandlePromotion(){
        byte[] allocation1, allocation2, allocation3, allocation4, allocation5, allocation6, allocation7;

        allocation1 = new byte[2 * _1MB];
        allocation2 = new byte[2 * _1MB];
        allocation3 = new byte[2 * _1MB];
        allocation1 = null;
        allocation4 = new byte[2 * _1MB];
        allocation5 = new byte[2 * _1MB];
        allocation6 = new byte[2 * _1MB];
        allocation4 = null;
        allocation5 = null;
        allocation6 = null;
        allocation7 = new byte[2 * _1MB];

    }
}


class FinalizeEscapeGC {
    public static FinalizeEscapeGC SAVE_HOOK = null;

    public void isAlive() {
        System.out.println("still alive");
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        System.out.println("finalize method executed");
        FinalizeEscapeGC.SAVE_HOOK = this;
    }
}