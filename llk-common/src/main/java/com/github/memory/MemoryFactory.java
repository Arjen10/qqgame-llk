package com.github.memory;


import com.sun.jna.Memory;

import java.util.Objects;

/**
 * @author Arjen10
 * @date 2022/9/7 20:32
 */
public class MemoryFactory {


    public static Memory get4BitMemory() {
        return new Memory(4);
    }

    public static Memory get1BitMemory() {
        return new Memory(1);
    }

    public static Memory get8BitMemory() {
        return new Memory(8);
    }

    public static Memory getByteArrayMemory(byte[] bytes) {
        Objects.requireNonNull(bytes);
        return new Memory(bytes.length);
    }

    public static <T extends Number> Memory getMemoryByValue(T value) throws IllegalArgumentException {
        Objects.requireNonNull(value);
        if (value instanceof Integer) {
            var memory = get4BitMemory();
            memory.setInt(0, (Integer) value);
            return memory;
        } else if (value instanceof Float) {
            var memory = get4BitMemory();
            memory.setFloat(0, (Float) value);
            return memory;
        } else if (value instanceof Double) {
            var memory = get8BitMemory();
            memory.setDouble(0, (Double) value);
            return memory;
        } else if (value instanceof Long) {
            var memory = get8BitMemory();
            memory.setLong(0, (Long) value);
            return memory;
        }
        throw new IllegalArgumentException("类型错误！");
    }

}
