package org.slowcoders.io.serialize;

import org.slowcoders.util.Debug;

public abstract class TextWriter extends DataWriter.AggregatedStream {

	TextWriter(IOAdapterLoader loader) {
		super(loader);
	}

	public boolean shouldEncodeEnumToBitSet() {
		return false;
	}
	
	public boolean isPersistentStream() {
		return true;
	}
	
	
	public void writeBooleanArray(boolean[] values) throws Exception {
		if (!writeArrayStart(values)) return;
		for (boolean value : values) {
			this.writeBoolean(value);
		}
		writeArrayEnd();
	}
	

	public void writeByteArray(byte[] values) throws Exception {
		if (!writeArrayStart(values)) return;
		for (byte value : values) {
			this.writeByte(value);
		}
		writeArrayEnd();
	}
	
	public void writeCharArray(char[] values) throws Exception {
		if (!writeArrayStart(values)) return;
		for (char value : values) {
			this.writeChar(value);
		}
		writeArrayEnd();
	}
	
	public void writeShortArray(short[] values) throws Exception {
		if (!writeArrayStart(values)) return;
		for (short value : values) {
			this.writeShort(value);
		}
		writeArrayEnd();
	}
	
	public void writeIntArray(int[] values) throws Exception {
		if (!writeArrayStart(values)) return;
		for (int value : values) {
			this.writeInt(value);
		}
		writeArrayEnd();
	}
	
	public void writeLongArray(long[] values) throws Exception {
		if (!writeArrayStart(values)) return;
		for (long value : values) {
			this.writeLong(value);
		}
		writeArrayEnd();
	}
	
	public void writeFloatArray(float[] values) throws Exception {
		if (!writeArrayStart(values)) return;
		for (float value : values) {
			this.writeFloat(value);
		}
		writeArrayEnd();
	}
	
	public void writeDoubleArray(double[] values) throws Exception {
		if (!writeArrayStart(values)) return;
		for (double value : values) {
			this.writeDouble(value);
		}
		writeArrayEnd();
	}
	
	public void writeStringArray(String[] values) throws Exception {
		if (!writeArrayStart(values)) return;
		for (String value : values) {
			this.writeString(value);
		}
		writeArrayEnd();
	}

//	public void write(Object[] values) {
//		if (!writeArrayStart(values)) return;
//		for (Object value : values) {
//			this.write(value);
//		}
//		writeArrayEnd();
//	}
//	

	public boolean writeArrayStart(Object values) throws Exception {
		throw Debug.notImplemented();
	}

	public void writeArrayEnd() throws Exception {
		throw Debug.notImplemented();
	}

	protected abstract AggregatedStream beginAggregate(String compositeType, boolean isMap) throws Exception;



}
