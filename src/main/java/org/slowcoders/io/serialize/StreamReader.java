package org.slowcoders.io.serialize;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.slowcoders.util.Debug;

public class StreamReader extends DataReader.AutoCloseStream implements StreamData {
	
	private final ByteBuffer in;

	private boolean wasNull;

	private int itemCount;
	
	private boolean isMap;

	private String containerType;
	
	public StreamReader(IOAdapterLoader loader, byte[] data) throws Exception {
		super(loader);
		in = ByteBuffer.wrap(data);
		byte type = in.get();
		this.itemCount = in.getShort();
		Debug.Assert((type & ARRAY) != 0);
		
		int item_t = (type & 0xF);
		if (item_t > TYPED_OBJECT) {
			this.containerType = this.readString(); 
		}
		this.isMap = item_t == KEY_VALUE || item_t == TYPED_MAP;
	}

	@Override
	public boolean wasNull() throws Exception {
		return this.wasNull;
	}
	
	public boolean isMap() {
		return this.isMap;
	}
	
	@Override 
	public String getEntityType() {
		return this.containerType;
	}

	@Override
	public boolean readBoolean() throws Exception {
		return readInt64(0, 1) != 0;
	}

	@Override
	public byte readByte() throws Exception {
		return (byte)readInt64(Byte.MIN_VALUE, Byte.MAX_VALUE);
	}

	@Override
	public short readShort() throws Exception {
		return (short)readInt64(Short.MIN_VALUE, Short.MAX_VALUE);
	}

	@Override
	public char readChar() throws Exception {
		return (char)readInt64(Character.MIN_VALUE, Character.MAX_VALUE);
	}

	@Override
	public int readInt() throws Exception {
		return (int)readInt64(Integer.MIN_VALUE, Integer.MAX_VALUE);
	}

	@Override
	public long readLong() throws Exception {
		return readInt64(Long.MIN_VALUE, Long.MAX_VALUE);
	}

	@Override
	public float readFloat() throws Exception {
		return (float)readDouble(Float.MIN_VALUE, Float.MAX_VALUE);
	}

	@Override
	public double readDouble() throws Exception {
		return readDouble(Float.MIN_VALUE, Float.MAX_VALUE);
	}
	
	@Override
	public Object readPrimitiveOrStrings() throws Exception {
		in.mark();
		byte type = in.get();
		this.wasNull = false;

		Object v;
		switch (type) {
			case NULL:
				return null;
			case BOOLEAN:
			case BYTE:
				v = in.get();
				break;
			case CHAR:
				v = (char)in.getShort();
				break;
			case SHORT:
				v = in.getShort();
				break;
			case INT:
				v = in.getInt();
				break;
			case FLOAT:
				v = in.getFloat();
				break;
			case LONG:
				v = in.getLong();
				break;
			case DOUBLE:
				v = in.getDouble();
				break;
			case STRING:
				in.reset();
				v = this.readString();
				break;
			case STRING | ARRAY:
				in.reset();
				v = this.readStringArray();
				break;
			default:
				throw Debug.wtf("unknown type: 0x" + Integer.toHexString(type));
		}
		return v;
	}

	private long readInt64(long min, long max) throws Exception {
		byte type = in.get();
		this.wasNull = false;

		long v;
		switch (type) {
		case NULL:
			this.wasNull = true;
			return 0;
		case BOOLEAN:
		case BYTE:
			v = in.get();
			break;
		case CHAR:
			v = (char)in.getShort();
			break;
		case SHORT:
			v = in.getShort();
			break;
		case INT:
			v = in.getInt();
			break;
		case FLOAT:
			float fv = in.getFloat();
			v = (long)fv;
			if (fv != v) {
				throw Debug.wtf("Can not convert float(" + fv + ") to long(" + v + ")");
			}
			break;
		case LONG:
			v = in.getLong();
			break;
		case DOUBLE:
			double dv = in.getDouble();
			v = (long)dv;
			if (dv != v) {
				throw Debug.wtf("Can not convert double(" + dv + ") to long(" + v + ")");
			}
			break;
		default:
			throw Debug.wtf("unkown type: 0x" + Integer.toHexString(type));
		}
		if (v < min || v > max) {
			throw Debug.wtf("Number convert error: " + v + " (min: " + min + ", max: " + max + ")");
		}
		this.wasNull = false;
		return v;
	}

	private double readDouble(double min, double max) throws Exception {
		byte type = in.get();
		double v;
		switch (type) {
		case NULL:
			this.wasNull = true;
			return 0;
		case BOOLEAN:
		case BYTE:
			v = in.get();
			break;
		case CHAR:
			v = (char)in.getShort();
			break;
		case SHORT:
			v = in.getShort();
			break;
		case INT:
			v = in.getInt();
			break;
		case FLOAT:
			v = in.getFloat();
			break;
		case LONG:
			v = in.getLong();
			break;
		case DOUBLE:
			v = in.getDouble();
			break;
		default:
			throw Debug.wtf("unkown type: 0x" + Integer.toHexString(type));
		}
		this.wasNull = false;
		return v;
	}

	@Override
	public Number readNumber() throws Exception {
		Object v = this.readPrimitiveOrStrings();
		return (Number) v;
	}

	private int readArrayLength(int expectedType) throws IOException {
		byte type = in.get();
		if ((this.wasNull = type == NULL)) {
			return -1;
		}
		if (type != expectedType) {
			throw new IOException("array type mismatch ");
		}
		return in.getShort();
	}


	@Override
	public String readString() throws Exception {
		char[] buf = this.readCharArray();
		if (buf == null) {
			return null;
		}
		return new String(buf);
	}

	@Override
	public boolean[] readBooleanArray() throws Exception {
		int len = this.readArrayLength(BOOLEAN | ARRAY);
		if (len < 0) {
			return null;
		}
		boolean[] array = new boolean[len];
		for (int i = 0; i < len; i++) {
			array[i] = in.get() != 0;
		}
		return array;
	}

	@Override
	public byte[] readByteArray() throws Exception {
		int len = this.readArrayLength(BYTE | ARRAY);
		if (len < 0) {
			return null;
		}
		byte[] array = new byte[len];
		in.get(array);
		return array;
	}

	@Override
	public short[] readShortArray() throws Exception {
		int len = this.readArrayLength(SHORT | ARRAY);
		if (len < 0) {
			return null;
		}
		short[] array = new short[len];
		for (int i = 0; i < array.length; i ++) {
			array[i] = in.getShort();
		}
		return array;
	}

	@Override
	public char[] readCharArray() throws Exception {
		int len = this.readArrayLength(CHAR | ARRAY);
		if (len < 0) {
			return null;
		}
		char[] array = new char[len];
		for (int i = 0; i < array.length; i ++) {
			array[i] = in.getChar();
		}
		return array;
	}

	@Override
	public int[] readIntArray() throws Exception {
		int len = this.readArrayLength(INT | ARRAY);
		if (len < 0) {
			return null;
		}
		int[] array = new int[len];
		for (int i = 0; i < array.length; i ++) {
			array[i] = in.getInt();
		}
		return array;
	}

	@Override
	public long[] readLongArray() throws Exception {
		int len = this.readArrayLength(LONG | ARRAY);
		if (len < 0) {
			return null;
		}
		long[] array = new long[len];
		for (int i = 0; i < array.length; i ++) {
			array[i] = in.getLong();
		}
		return array;
	}

	@Override
	public float[] readFloatArray() throws Exception {
		int len = this.readArrayLength(FLOAT | ARRAY);
		if (len < 0) {
			return null;
		}
		float[] array = new float[len];
		for (int i = 0; i < array.length; i ++) {
			array[i] = in.getFloat();
		}
		return array;
	}

	@Override
	public double[] readDoubleArray() throws Exception {
		int len = this.readArrayLength(DOUBLE | ARRAY);
		if (len < 0) {
			return null;
		}
		double[] array = new double[len];
		for (int i = 0; i < array.length; i ++) {
			array[i] = in.getDouble();
		}
		return array;
	}

	@Override
	public String[] readStringArray() throws Exception {
		int len = this.readArrayLength(STRING | ARRAY);
		if (len < 0) {
			return null;
		}
		String[] array = new String[len];
		for (int i = 0; i < array.length; i++) {
			array[i] = this.readString();
		}
		return array;
	}

	
	
	@Override
	public boolean isClosed() {
		int r = in.remaining();
		return r == 0;
	}

	@Override
	public String readKey() throws IOException, Exception {
		return this.readString();
	}

	@Override
	protected int getItemCount() throws IllegalStateException {
		return this.itemCount;
	}

	public void skipKeySeparator() throws Exception {
		return;
	}

	@Override
	public AutoCloseStream openChunkedStream() throws Exception {
		byte[] bytes = this.readByteArray();
		if (bytes == null) {
			return null;
		}
		
		StreamReader in = new StreamReader(this.getLoader(), bytes);
		return in;
	}


}
