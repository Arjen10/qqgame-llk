package com.github.util;


import com.github.conf.GameStanderConfig;
import com.github.memory.WindowsUtils;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.event.InputEvent;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.github.conf.GameStanderConfig.*;
import static com.github.memory.WindowsUtils.getHWND;

/**
 * @author Arjen10
 * @date 2022/9/8 14:27
 */
@Slf4j
public class RobotUtil {

    private static Robot ROBOT;

    static {
        try {
            ROBOT = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    @Deprecated
    public synchronized static void onClick(Integer x, Integer y) {
        Objects.requireNonNull(x);
        Objects.requireNonNull(y);
        ROBOT.mouseMove(x, y);
        ROBOT.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        try {
            TimeUnit.MILLISECONDS.sleep(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ROBOT.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    public synchronized static void onClickMessage(Integer x, Integer y) {
        Objects.requireNonNull(x);
        Objects.requireNonNull(y);
        WindowsUtils.sendMessage(getHWND(WINDOW_NAME), MK_LBUTTON, (y << 16) + x);
        try {
            TimeUnit.MILLISECONDS.sleep(speed);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        WindowsUtils.sendMessage(getHWND(WINDOW_NAME), WM_LBUTTONUP, (y << 16) + x);
    }

}
