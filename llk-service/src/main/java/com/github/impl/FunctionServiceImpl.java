package com.github.impl;

import com.github.FunctionService;
import com.github.conf.GameStanderConfig;
import com.github.memory.WindowsUtils;
import com.sun.jna.platform.win32.WinNT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import static com.github.conf.GameStanderConfig.*;
import static com.github.conf.LlkInitialize.getHANDLE;

/**
 * @author Arjen10
 * @date 2022/9/16 14:55
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FunctionServiceImpl implements FunctionService {

    private static final Map<String, ScheduledFuture<?>> CONCURRENT_MAP = new ConcurrentHashMap<>(16);

    private final ThreadPoolTaskScheduler scheduler;

    @Override
    public void lockAndUnLockCountdown() throws NullPointerException{
        WinNT.HANDLE handle = getHANDLE();
        Optional.ofNullable(CONCURRENT_MAP.getOrDefault(LOCK_COUNTDOWN_THREAD_NAME, null))
                .ifPresentOrElse(sf -> {
                    sf.cancel(true);
                    CONCURRENT_MAP.remove(LOCK_COUNTDOWN_THREAD_NAME);
                }, () -> {
                    ScheduledFuture<?> schedule = scheduler.schedule(
                            () -> WindowsUtils.writeIntToAddress(handle, TIME_ADDRESS, 750)
                            , TRIGGER);
                    CONCURRENT_MAP.put(LOCK_COUNTDOWN_THREAD_NAME, schedule);
                });
    }

    @Override
    public void changeSpeed(Integer speed) {
        GameStanderConfig.speed = speed < 0 ? 0 : speed;
        log.info("当前速度：每隔 {} 毫秒清除一次", GameStanderConfig.speed);
    }

    @Override
    public void shutdown() {
        scheduler.shutdown();
    }

}
