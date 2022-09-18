import com.github.conf.GameStanderConfig;
import com.github.memory.WindowsUtils;
import com.github.util.RobotUtil;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import org.junit.Test;

/**
 * @author Arjen10
 * @date 2022/9/16 19:33
 */
public class RobotUtilTest {

    @Test
    public void testOnClickMessage() {
        RobotUtil.onClickMessage(386, 387);
    }

    @Test
    public void testSendMessage() {
        User32 user = WindowsUtils.getUSER();
        WinDef.HWND hwnd = WindowsUtils.getHWND(GameStanderConfig.WINDOW_NAME);
        WinDef.WPARAM wparam = WindowsUtils.getWPARAM();
        user.SetForegroundWindow(hwnd);

        user.SendMessage(hwnd,0x0201, wparam, new WinDef.LPARAM(0x0231028E));
        user.SendMessage(hwnd,0x0202, wparam, new WinDef.LPARAM(0x0231028E));
    }

}
