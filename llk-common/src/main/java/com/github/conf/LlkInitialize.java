package com.github.conf;

import com.github.memory.WindowsUtils;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ConfigurableApplicationContext;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static com.github.conf.GameStanderConfig.WINDOW_NAME;
import static com.github.memory.WindowsUtils.getRect;

/**
 * @author Arjen10
 * @date 2022/9/8 14:41
 */
@Slf4j
public abstract class LlkInitialize {

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static WinNT.HANDLE HANDLE;

    private static WinDef.RECT RECT;

    public static WinNT.HANDLE getHANDLE() {
        return Objects.requireNonNull(HANDLE, "窗口句柄为空！可能是没有初始化程序！");
    }

    public static WinDef.RECT getRECT() {
        return RECT;
    }

    public static void initHandleAndRect() throws NullPointerException {
        HANDLE = WindowsUtils.getHandleByWindowName(WINDOW_NAME);
        RECT = getRect(WINDOW_NAME);
        Objects.requireNonNull(HANDLE, "获取连连看句柄失败！");
        Objects.requireNonNull(RECT, "获取连连看窗口句柄失败！");
        log.info("时间：{}，初始化成功！", LocalDateTime.now().format(FORMATTER));
    }


}
