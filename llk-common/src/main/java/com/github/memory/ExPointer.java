package com.github.memory;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author Arjen
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ExPointer {

    /**
     * 指针
     */
    private Pointer pointer;

    private HANDLE handle;

    public ExPointer(HANDLE handle, int address) {
        this.handle = handle;
        pointer = Pointer.createConstant(address);
    }

    public ExPointer offset(int offset) {
        int value = WindowsUtils.readIntOfExPointer(this);
        int newAddress = value + offset;
        return new ExPointer(handle, newAddress);
    }


    /**
     * 获取指针的地址
     * @return  地址
     */
    public int getAddress() {
        return (int) Pointer.nativeValue(pointer);
    }

    /**
     * 读取指针指向的值
     * @return  int
     */
    public int readInt() {
        return WindowsUtils.readIntOfExPointer(this);
    }

    public boolean writeInt(Integer value) {
        return WindowsUtils.writeValueToExPointer(this, value);
    }


    public double readDouble() {
        return WindowsUtils.readDoubleOfExPointer(this);
    }

}