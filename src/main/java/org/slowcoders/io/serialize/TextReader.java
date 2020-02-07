package org.slowcoders.io.serialize;

import org.slowcoders.util.Debug;

import java.io.IOException;

public abstract class TextReader extends DataReader.AutoCloseStream {

	private boolean isMap;

	public TextReader(IOAdapterLoader loader, boolean isMap) {
		super(loader);
		this.isMap = isMap;
	}

	@Override
	public boolean isMap() {
		return isMap;
	}

	public boolean isIntEnum() {
		return false;
	}
	
	public boolean isPersistentStream() {
		return true;
	}


	public boolean[] readBooleanArray() throws Exception {
		int len = this.readPrimitiveArrayLength();
		if (len < 0) {
			return null;
		}
		boolean[] values = new boolean[len];
		for (int i = 0; i < len; i++) {
			values[i] = this.readBoolean();
			this.skipArrayItemSeparator();
		}
		this.readEndOfArray();
		return values;
	}
	
	public byte[] readByteArray() throws Exception {
		int len = this.readPrimitiveArrayLength();
		if (len < 0) {
			return null;
		}
		byte[] values = new byte[len];
		for (int i = 0; i < len; i++) {
			values[i] = this.readByte();
			this.skipArrayItemSeparator();
		}
		this.readEndOfArray();
		return values;
	}
	
	public short[] readShortArray() throws Exception {
		int len = this.readPrimitiveArrayLength();
		if (len < 0) {
			return null;
		}
		short[] values = new short[len];
		for (int i = 0; i < len; i++) {
			values[i] = this.readShort();
			this.skipArrayItemSeparator();
		}
		this.readEndOfArray();
		return values;
	}
	
	public char[] readCharArray() throws Exception {
		int len = this.readPrimitiveArrayLength();
		if (len < 0) {
			return null;
		}
		char[] values = new char[len];
		for (int i = 0; i < len; i++) {
			values[i] = this.readChar();
			this.skipArrayItemSeparator();
		}
		this.readEndOfArray();
		return values;
	}
	
	public int[] readIntArray() throws Exception {
		int len = this.readPrimitiveArrayLength();
		if (len < 0) {
			return null;
		}
		int[] values = new int[len];
		for (int i = 0; i < len; i++) {
			values[i] = this.readInt();
			this.skipArrayItemSeparator();
		}
		this.readEndOfArray();
		return values;
	}
	
	public long[] readLongArray() throws Exception {
		int len = this.readPrimitiveArrayLength();
		if (len < 0) {
			return null;
		}
		long[] values = new long[len];
		for (int i = 0; i < len; i++) {
			values[i] = this.readLong();
			this.skipArrayItemSeparator();
		}
		this.readEndOfArray();
		return values;
	}
	
	public float[] readFloatArray() throws Exception {
		int len = this.readPrimitiveArrayLength();
		if (len < 0) {
			return null;
		}
		float[] values = new float[len];
		for (int i = 0; i < len; i++) {
			values[i] = this.readFloat();
			this.skipArrayItemSeparator();
		}
		this.readEndOfArray();
		return values;
	}
	
	public double[] readDoubleArray() throws Exception {
		int len = this.readPrimitiveArrayLength();
		if (len < 0) {
			return null;
		}
		double[] values = new double[len];
		for (int i = 0; i < len; i++) {
			values[i] = this.readDouble();
			this.skipArrayItemSeparator();
		}
		this.readEndOfArray();
		return values;
	}

	protected abstract void skipArrayItemSeparator() throws IOException;

	public String[] readStringArray() throws Exception {
		int len = this.readObjectArrayLength();
		if (len < 0) {
			return null;
		}
		String[] values = new String[len];
		for (int i = 0; i < len; i++) {
			values[i] = this.readString();
			this.skipArrayItemSeparator();
		}
		this.readEndOfArray();
		return values;
	}
	

	
	/**
	 * @return the length of array<br> 
	 * or {@code -1} when the array object is null 
	 */
	protected int readPrimitiveArrayLength() throws Exception {
		throw Debug.notImplemented();
	}

	/**
	 * @return the length of array<br> 
	 * or {@code DataDecoder.UNKNOWN} when the length is unknown<br>
	 * or {@code -1} when the array object is null 
	 */
	protected int readObjectArrayLength() throws Exception {
		throw Debug.notImplemented();
	}

	
	protected void readEndOfArray() throws Exception {
		throw Debug.notImplemented();
	}


}
