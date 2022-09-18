package com.github;


import com.github.impl.ClearServiceImpl;
import org.junit.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * @author Arjen10
 * @date 2022/9/15 21:48
 */
public class TestClear {

    @Test
    public void testClearAll() {
        Instant begin = Instant.now();
        ClearServiceImpl clearService = new ClearServiceImpl();
        clearService.clearAll();
        long between = ChronoUnit.SECONDS.between(begin, Instant.now());
        System.out.println("耗时：" + between + "秒");
    }

}
