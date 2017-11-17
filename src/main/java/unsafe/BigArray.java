package unsafe;

import util.UnsafeUtils;

public class BigArray {

    private static final int BYTE = 1;

    private long size;
    private long address;

    public BigArray(long size) {
        this.size = size;
        address = UnsafeUtils.get().allocateMemory(size * BYTE); //off-heap memory
    }

    public void set(long i, byte value) {
        UnsafeUtils.get().putByte(address + i * BYTE, value);
    }

    public int get(long idx) {
        return UnsafeUtils.get().getByte(address + idx * BYTE);
    }

    public long size() {
        return size;
    }


}
