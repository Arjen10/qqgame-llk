package com.github.memory;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;
import lombok.Getter;

import java.util.Optional;

import static com.github.memory.MemoryFactory.*;

public class WindowsUtils {

    private static final Kernel32 KERNEL = Kernel32.INSTANCE;

    @Getter
    private static final User32 USER = User32.INSTANCE;

    @Getter
    private static final WinDef.WPARAM WPARAM = new WinDef.WPARAM(0);

    public static HWND getHWND(String windowName) {
        return USER.FindWindow(null, windowName);
    }

    public static void sendMessage(HWND hwnd, int i, long lparam) {
        USER.SetForegroundWindow(hwnd);
        USER.PostMessage(hwnd, i, WPARAM, new WinDef.LPARAM(lparam));
    }

    /**
     * 获取窗口
     *
     * @param windowName 窗口名称
     * @return 窗口
     */
    public static WinDef.RECT getRect(String windowName) {
        WinDef.RECT rect = new WinDef.RECT();
        boolean b = USER.GetWindowRect(getHWND(windowName), rect);
        if (b) {
            return rect;
        }
        throw new RuntimeException("获取窗口位置失败！");
    }

    /**
     * 通过窗口名称获取句柄
     *
     * @param windowName 窗口名称
     * @return 句柄
     */
    public static HANDLE getHandleByWindowName(String windowName) throws NullPointerException {
        HWND hwnd = getHWND(windowName);
        IntByReference pidReference = new IntByReference();
        USER.GetWindowThreadProcessId(hwnd, pidReference);
        int pid = pidReference.getValue();
        HANDLE handle = KERNEL.OpenProcess(WinNT.PROCESS_ALL_ACCESS, false, pid);
        return Optional.ofNullable(handle)
                .orElseThrow(() -> new NullPointerException("句柄为空！"));
    }


    /**
     * 通过pid获取句柄
     *
     * @param pid pid
     * @return 句柄
     */
    public static HANDLE getHandleByPid(int pid) {
        return KERNEL.OpenProcess(WinNT.PROCESS_ALL_ACCESS, false, pid);
    }


    /**
     * 将int值写入到指定地址
     *
     * @param handle  程序句柄
     * @param address 地址
     * @param value   值
     * @return 写入多少 -1为失败
     */
    public static Integer writeIntToAddress(HANDLE handle, int address, int value) {
        try (var intMemory = get4BitMemory()) {
            intMemory.setInt(0, value);
            IntByReference readByteNumber = new IntByReference();
            KERNEL.WriteProcessMemory(handle, Pointer.createConstant(address), intMemory, 4, readByteNumber);
            return readByteNumber.getValue();
        }
    }

    /**
     * 读取指定地址的int值
     *
     * @param handle  程序句柄
     * @param address 地址
     * @return 值
     */
    public static Integer readIntOfAddress(HANDLE handle, int address) {
        try (var intMemory = get4BitMemory()) {
            var readByteNumber = new IntByReference();
            KERNEL.ReadProcessMemory(handle, Pointer.createConstant(address), intMemory, 4, readByteNumber);
            return intMemory.getInt(0);
        }
    }


    public static Byte readByteOfAddress(HANDLE handle, int address) {
        try (var byteMemory = get1BitMemory()) {
            var readByteNumber = new IntByReference();
            KERNEL.ReadProcessMemory(handle, Pointer.createConstant(address), byteMemory, 1, readByteNumber);
            return byteMemory.getByte(0);
        }
    }

    /**
     * 读取指定地址的int值
     *
     * @param handle  程序句柄
     * @param pointer 地址
     * @return 值
     */
    public static Integer readIntOfPointer(HANDLE handle, Pointer pointer) {
        try (var intMemory = get4BitMemory()) {
            IntByReference readByteNumber = new IntByReference();
            KERNEL.ReadProcessMemory(handle, pointer, intMemory, 4, readByteNumber);
            return intMemory.getInt(0);
        }
    }

    /**
     * 读取指定地址的int值
     *
     * @param pointer 地址
     * @return 值
     */
    public static Integer readIntOfExPointer(ExPointer pointer) {
        try (var intMemory = get4BitMemory()) {
            IntByReference readByteNumber = new IntByReference();
            KERNEL.ReadProcessMemory(pointer.getHandle(), pointer.getPointer(), intMemory, 4, readByteNumber);
            return intMemory.getInt(0);
        }
    }


    /**
     * 读取指定地址的double值
     *
     * @param exPointer 地址
     * @return 值
     */
    public static Double readDoubleOfExPointer(ExPointer exPointer) {
        try (var doubleMemory = get8BitMemory()) {
            IntByReference readByteNumber = new IntByReference();
            KERNEL.ReadProcessMemory(exPointer.getHandle(), exPointer.getPointer(), doubleMemory, 8, readByteNumber);
            return doubleMemory.getDouble(0);
        }
    }


    /**
     * 读取指定地址的float值
     *
     * @param exPointer 地址
     * @return 值
     */
    public static Float readFloatOfExPointer(ExPointer exPointer) {
        try (var doubleAndFloatMemory = get8BitMemory()) {
            IntByReference readByteNumber = new IntByReference();
            KERNEL.ReadProcessMemory(exPointer.getHandle(), exPointer.getPointer(), doubleAndFloatMemory, 8, readByteNumber);
            return doubleAndFloatMemory.getFloat(0);
        }
    }

    /**
     * 读取指定地址的float值
     *
     * @param exPointer 地址
     * @return 值
     */
    public static Long readLongOfExPointer(ExPointer exPointer) {
        try (var bitMemory = get8BitMemory()) {
            IntByReference readByteNumber = new IntByReference();
            KERNEL.ReadProcessMemory(exPointer.getHandle(), exPointer.getPointer(), bitMemory, 8, readByteNumber);
            return bitMemory.getLong(0);
        }
    }

    /**
     * 读取指定地址的float值
     *
     * @param handle 地址
     * @return 值
     */
    public static Byte readByteOfExPointer(HANDLE handle) {
        try (var bitMemory = get1BitMemory()) {
            IntByReference readByteNumber = new IntByReference();
            KERNEL.ReadProcessMemory(handle, handle.getPointer(), bitMemory, 1, readByteNumber);
            return bitMemory.getByte(0);
        }
    }

    /**
     * 读取指定地址的float值
     *
     * @param handle 句柄
     * @return 值
     */
    public static byte[] readByteArray(HANDLE handle, int size) {
        Memory memory = new Memory(size);
        IntByReference readByteNumber = new IntByReference();
        KERNEL.ReadProcessMemory(handle, handle.getPointer(), memory, size, readByteNumber);
        return memory.getByteArray(0, size);
    }

    /**
     * 写入到地址
     *
     * @param exPointer 指针
     * @param value     值
     */
    public static boolean writeValueToExPointer(ExPointer exPointer, Number value) {
        try (var memory = getMemoryByValue(value)) {
            IntByReference readByteNumber = new IntByReference();
            return KERNEL.WriteProcessMemory(exPointer.getHandle(), exPointer.getPointer(), memory, 4, readByteNumber);
        }
    }

    public static boolean writeValueToExPointer(ExPointer exPointer, byte[] value) {
        try (Memory memory = getByteArrayMemory(value)) {
            IntByReference readByteNumber = new IntByReference();
            return KERNEL.WriteProcessMemory(exPointer.getHandle(), exPointer.getPointer(), memory, 4, readByteNumber);
        }
    }

}
