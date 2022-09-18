package com.github;


/**
 * @author Arjen10
 * @date 2022/9/16 14:55
 */
public interface FunctionService {

    /**
     * 锁定倒计时
     */
    void lockAndUnLockCountdown();

    /**
     * 调整消除速度
     * @param speed 速度，单位，毫秒
     */
    void changeSpeed(Integer speed);

    /**
     * 强制关闭线程池等等
     */
    void shutdown();
}
