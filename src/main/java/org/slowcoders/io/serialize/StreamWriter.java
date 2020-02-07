package org.slowcoders.io.serialize;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;

import org.slowcoders.util.Debug;

public class StreamWriter extends DataWriter.AggregatedStream implements StreamData {

	private DataOutputStream out;

	private DataWriter parent;

	private ByteArrayOutputStream baos;
	
	private int cntItem;

	private boolean isMap;

	private StreamWriter(IOAdapterLoader loader, OutputStream out, boolean isMap) throws Exception {
		super(loader);
		init(out, null, isMap);
	}
	
	public StreamWriter(DataWriter parent, String type, boolean isMap) throws Exception {
		super(parent.getLoader());
		Debug.Assert(parent != null);
		this.parent = parent;
		this.baos = new ByteArrayOutputStream();
		this.init(baos, type, isMap);
	}

	private void init(OutputStream out2, String type, boolean isMap) throws Exception {
		this.out = new DataOutputStream(out2);
		this.cntItem = -1;
		int content_type;
		this.isMap = isMap;
		if (isMap) {
			content_type = type == null ? KEY_VALUE : TYPED_MAP;
		}
		else {
			content_type = type == null ? OBJECT : TYPED_COLLECTION;
		}
		this.writeArrayLength(content_type, 0);
		if (type != null) {
			this.writeString(type);
		}
	}
	
	public static void writeEntity(Object entity, OutputStream out0) throws Exception {
		StreamWriter out = new StreamWriter(IOAdapter.getLoader(false), out0, true);
		out.writeEntity(entity);
		return;
	}

	public static byte[] toByteArray(Object entity) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		StreamWriter out = new StreamWriter(IOAdapter.getLoader(false), baos, true);
		out.writeEntity(entity);
		byte[] data = out.toByteArray(baos);
		return data;
	}

	private void writeType(int type) throws Exception {
		out.write((byte)type);
		cntItem ++;
	}

	@Override
	public void writeBoolean(boolean value) throws Exception {
		writeType(BOOLEAN);
		out.write(value ? (byte)1 : (byte)0);
	}

	@Override
	public void writeByte(byte value) throws Exception {
		writeType(BYTE);
		out.write(value);
	}

	@Override
	public void writeChar(char value) throws Exception {
		writeType(CHAR);
		out.writeChar(value);
	}

	@Override
	public void writeShort(short value) throws Exception {
		writeType(SHORT);
		out.writeShort(value);
	}

	@Override
	public void writeInt(int value) throws Exception {
		writeType(INT);
		out.writeInt(value);
	}

	@Override
	public void writeLong(long value) throws Exception {
		writeType(LONG);
		out.writeLong(value);
	}

	@Override
	public void writeFloat(float value) throws Exception {
		writeType(FLOAT);
		out.writeFloat(value);
	}

	@Override
	public void writeDouble(double value) throws Exception {
		writeType(DOUBLE);
		out.writeDouble(value);
	}

	@Override
	public void writeString(String value) throws Exception {
		if (value == null) {
			writeNull();
			return;
		}
		if (!writeArrayLength(CHAR, value.length())) return;
		for (int i = 0; i < value.length(); i++) {
			out.writeChar(value.charAt(i));
		}
	}

	@Override
	public void writeNumber(Number value) throws Exception {
		throw Debug.notImplemented();
	}

	@Override
	public void writeNull() throws Exception {
		cntItem ++;
		out.write((byte)NULL);
	}

	@Override
	public void writeBooleanArray(boolean[] values) throws Exception {
		if (!writeArrayHeader(BOOLEAN, values)) return;

		for (boolean v : values) {
			out.write(v ? (byte)1 : (byte)0);
		}
	}

	@Override
	public void writeByteArray(byte[] values) throws Exception {
		if (!writeArrayHeader(BYTE, values)) return;

		for (byte v : values) {
			out.write(v);
		}
	}

	@Override
	public void writeCharArray(char[] values) throws Exception {
		if (!writeArrayHeader(CHAR, values)) return;

		for (char v : values) {
			out.writeChar(v);
		}
	}

	@Override
	public void writeShortArray(short[] values) throws Exception {
		if (!writeArrayHeader(SHORT, values)) return;

		for (short v : values) {
			out.writeShort(v);
		}
	}

	@Override
	public void writeIntArray(int[] values) throws Exception {
		if (!writeArrayHeader(INT, values)) return;

		for (int v : values) {
			out.writeInt(v);
		}
	}

	@Override
	public void writeLongArray(long[] values) throws Exception {
		if (!writeArrayHeader(LONG, values)) return;

		for (long v : values) {
			out.writeLong(v);
		}
	}

	@Override
	public void writeFloatArray(float[] values) throws Exception {
		if (!writeArrayHeader(FLOAT, values)) return;

		for (float v : values) {
			out.writeFloat(v);
		}
	}

	@Override
	public void writeDoubleArray(double[] values) throws Exception {
		if (!writeArrayHeader(DOUBLE, values)) return;

		for (double v : values) {
			out.writeDouble(v);
		}
	}

	@Override
	public void writeStringArray(String[] values) throws Exception {
		if (!writeArrayHeader(STRING, values)) return;

		for (String str : values) {
			this.writeString(str);
		}
	}

	private boolean writeArrayHeader(int type, Object values) throws Exception {
		if (values == null) {
			writeNull();
			return false;
		}
		int length = Array.getLength(values);
		return writeArrayLength(type, length);
	}

	private boolean writeArrayLength(int type, int length) throws Exception {
		out.write(type | ARRAY);
		out.writeChar((char)length);
		return length > 0;
	}

	@Override
	protected AggregatedStream beginAggregate(String compositeType, boolean isMap) throws Exception {
		return new StreamWriter(this, compositeType, isMap);
	}

	private byte[] toByteArray(ByteArrayOutputStream baos) {
		byte[] bytes = baos.toByteArray();
		bytes[1] = (byte)((this.cntItem >> 16) & 0xFF);
		bytes[2] = (byte)(this.cntItem & 0xFF);
		return bytes;
	}
	
	@Override
	public void close() throws Exception {
		if (this.baos != null) {
			byte[] bytes = this.toByteArray(baos); 
			parent.writeByteArray(bytes);
		}
	}

	@Override
	protected boolean isMap() {
		return this.isMap;
	}

}
