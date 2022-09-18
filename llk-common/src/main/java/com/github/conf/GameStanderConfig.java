package com.github.conf;

import org.springframework.scheduling.support.PeriodicTrigger;

import java.util.concurrent.TimeUnit;

/**
 *
 *
 * @author Arjen10
 * @date 2022/8/19 18:01
 */
public abstract class GameStanderConfig {

    /**
     * 窗口名称
     */
    public static final String WINDOW_NAME = "QQ游戏 - 连连看角色版";

    /**
     * 练习模式倒计时地址
     */
    public static final Integer TIME_ADDRESS = 0x186AA8;

    /**
     * 剩余方框数量
     */
    public static final Integer REMAINDER_ADDRESS = 0x184DC0;

    /**
     * 棋盘基质
     */
    public static final Integer CHESSBOARD_ADDRESS = 0x199F5C;

    /**
     * CONCURRENT_MAP定时任务中的key
     */
    public static final String LOCK_COUNTDOWN_THREAD_NAME = "lockCountdown";

    /**
     * 锁倒计时的定时任务
     */
    public static final PeriodicTrigger TRIGGER = new PeriodicTrigger(100, TimeUnit.MILLISECONDS);

    /**
     * 鼠标左键按下
     */
    public static final Integer MK_LBUTTON = 0x0201;

    /**
     * 鼠标左键抬起
     */
    public static final Integer WM_LBUTTONUP = 0x0202;

    public static Integer speed = 400;

}
