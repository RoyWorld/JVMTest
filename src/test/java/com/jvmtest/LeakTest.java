package com.jvmtest;

import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by RoyChan on 2018/1/30.
 */
public class LeakTest {
    /**
     * vm args: -ea -Xms20m -Xmx20m -XX:-UseGCOverheadLimit -XX:+HeapDumpOnOutOfMemoryError
     * -XX:-UseGCOverheadLimit这个参数可去掉再观察
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
}
