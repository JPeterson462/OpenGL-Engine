package utils;

public class BitFlags8 implements BitFlags {
	
	private byte data = 0;
	
	private static final byte MAX_VALUE = (byte) 0xFF;
	
	private static final byte[] BITS = {
		0x01,
		0x02,
		0x04,
		0x08,
		0x10,
		0x20,
		0x40,
		(byte) 0x80
	};

	@Override
	public boolean bitSet(int i) {
		return (data & BITS[i]) != 0;
	}

	@Override
	public void setBit(int i, boolean value) {
		data = (byte) (value ? data | BITS[i] : data & ~BITS[i]);
	}

	@Override
	public void setBits(boolean value) {
		data = value ? MAX_VALUE : 0;
	}
	
}
