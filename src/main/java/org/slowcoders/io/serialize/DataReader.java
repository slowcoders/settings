package org.slowcoders.io.serialize;

import com.google.common.collect.ImmutableSet;
import org.slowcoders.util.Debug;
import org.slowcoders.util.EnumUtils;

import java.util.EnumSet;
import java.util.Set;

public abstract class DataReader {

	private Object context;
	private IOAdapterLoader loader;
	private boolean isImmutable;

	public DataReader(IOAdapterLoader loader) {
		this.loader = loader;
		this.isImmutable = true;
	}

	public final Object getContext() {
		return context;
	}

	public final boolean isImmutable() { return isImmutable; }

	public final IOAdapterLoader getLoader() { return this.loader; }
	
	protected final void setContext_unsafe(Object context) {
		this.context = context;
	}
	
	public abstract boolean wasNull() throws Exception;
	
	public abstract boolean readBoolean() throws Exception;
	
	public abstract byte readByte() throws Exception;
	
	public abstract short readShort() throws Exception;
	
	public abstract char readChar() throws Exception;
	
	public abstract int readInt() throws Exception;
	
	public abstract long readLong() throws Exception;
	
	public abstract float readFloat() throws Exception;
	
	public abstract double readDouble() throws Exception;
	
	
	public abstract Object readPrimitiveOrStrings() throws Exception;
	
	public abstract Number readNumber() throws Exception;

	public abstract String readString() throws Exception;

	public <E extends Enum<E>> E readEnum(Class<E> enumType) throws Exception {
		Object v = this.readPrimitiveOrStrings();
		if (v == null) {
			return null;
		}

		E e = null;
		if (v instanceof Number) {
			int idx = ((Number)v).intValue();
			if (idx >= 0) {
				e = EnumUtils.toEnum(enumType, idx);
			}
		}
		else if (v.getClass() == String.class) {
			String name = v.toString();
			e = EnumUtils.toEnum(enumType, name);
		}
		else {
			throw Debug.wtf("Can not convert " + v.getClass().getName() + " to Enum.");
		}
		return e;
	}

	//@NotNull
	public <E extends Enum<E>> Set<E> readEnumSet(Class<E> enumType, boolean isImmutable) throws Exception {
		Object v = this.readPrimitiveOrStrings();
		if (v == null) {
			if (isImmutable) {
				return ImmutableSet.of();
			}
			return EnumSet.noneOf(enumType);
		}

		Set<E> set;

		if (v instanceof Number) {
			long bits = ((Number)v).longValue();
			set = EnumUtils.bitsToEnumSet(enumType, bits, isImmutable);
		}
		else if (v instanceof String) {
			String[] names = v.toString().split(IOAdapter.TEXT_SEPARATOR);
			set = EnumUtils.toEnumSet(enumType, names, isImmutable);
		}
		else if (v instanceof String[]) {
			String[] names = (String[])v;
			set = EnumUtils.toEnumSet(enumType, names, isImmutable);
		}
		else {
			throw Debug.wtf("Can not convert " + v.getClass().getName() + " to EnumSet.");
		}
		return set;
	}



	public abstract boolean[] readBooleanArray() throws Exception;
	
	public abstract byte[] readByteArray() throws Exception;
	
	public abstract short[] readShortArray() throws Exception;
	
	public abstract char[] readCharArray() throws Exception;
	
	public abstract int[] readIntArray() throws Exception;
	
	public abstract long[] readLongArray() throws Exception;
	
	public abstract float[] readFloatArray() throws Exception;
	
	public abstract double[] readDoubleArray() throws Exception;
	
	public abstract String[] readStringArray() throws Exception;

	public Object readAny() throws Exception { throw Debug.notImplemented(); }
	
	
	protected abstract AutoCloseStream openChunkedStream() throws Exception;
	
	public final <T> DataIterator<T> readCollection(IOAdapter<T, ?> itemAdapter) throws Exception {
		AutoCloseStream stream = this.openChunkedStream();
		if (stream == null) {
			return null;
		}
		Debug.Assert(stream == null || !stream.isMap());
		return new DataIterator<>(stream, itemAdapter);
	}

	public final <K,V> KeyValueCursor<K,V> readDictionary(IOAdapter<K, ?> keyAdapter, IOAdapter<V, ?> valueAdapter) throws Exception {
		AutoCloseStream stream = this.openChunkedStream();
		if (stream == null) {
			return null;
		}
		Debug.Assert(stream == null || stream.isMap());
		return new KeyValueCursor<>(stream, keyAdapter, valueAdapter);
	}

    public static class DataIterator<T> implements java.util.Iterator<T> {

		private AutoCloseStream stream;
		private IOAdapter<T, ?> adapter;
		
		public DataIterator(AutoCloseStream stream, IOAdapter<T, ?> itemAdapter) {
			this.stream = stream;
			this.adapter = itemAdapter;
		}

		public int getItemCount() {
			return stream.getItemCount();
		}
		
		public String getCollectionType() {
			return stream.getEntityType();
		}
		
		@Override
		public boolean hasNext() {
			return !stream.isClosed();
		}

		@Override
		public T next() {
			try {
				return adapter.read(stream);
			} catch (Exception e) {
				throw Debug.wtf(e);
			}
		}
		
	}

	public static class KeyValueCursor<K,V> {

		private AutoCloseStream stream;
		private IOAdapter<K, ?> keyAdapter;
		private IOAdapter<V, ?> valueAdapter;
		private K key;
		private V value;
		
		public KeyValueCursor(AutoCloseStream stream, IOAdapter<K, ?> keyAdapter, IOAdapter<V, ?> valueAdapter) {
			this.stream = stream;
			this.keyAdapter = keyAdapter;
			this.valueAdapter = valueAdapter;
		}
		
		protected String getMapType() {
			return stream.getEntityType();
		}
		
		public K getKey() {
			return key;
		}

		public V getValue() {
			return value;
		}

		public boolean next() {
			if (stream.isClosed()) {
				return false;
			}
			try {
				key = keyAdapter.read(stream);
				stream.skipKeySeparator();
				value = valueAdapter.read(stream);
				return true;
			}
			catch (Exception e) {
				throw Debug.wtf(e);
			}
		}
	}
	
	public static abstract class AutoCloseStream extends DataReader {

		AutoCloseStream(IOAdapterLoader loader) { super(loader); }

		public abstract boolean isClosed();

		public abstract String readKey() throws Exception;

		protected abstract boolean isMap();

		protected abstract String getEntityType();

		protected abstract int getItemCount() throws IllegalStateException;
		
		public final void readEntity(Object entity, IOField[] slots) throws Exception {
			AutoCloseStream in = this;

			Object oldContext = ((DataReader)in).context; 
			((DataReader)in).context = entity;
			
			if (in.isMap()) {
				while (!in.isClosed()) {
					String key = in.readKey();
					IOField slot = IOEntity.findSerializableFieldByName(key, slots);
					if (slot != null) {
						slot.setValue(entity, in);
					}
					else {
						Object obj = in.readAny();
						System.err.println(entity.getClass() + "." + key + " is not exist!");
						System.err.println("Abandoned data(" + key + ") = " + obj);
					}
				}
			}
			else {
				for (IOField slot : slots) {
					slot.setValue(entity, in);
				}
				Debug.Assert(in.isClosed());
			}
			((DataReader)in).context = oldContext;
		}

		public abstract void skipKeySeparator() throws Exception;
	}

	protected final void readInto(Object entity, IOField column) throws Exception {
		column.setValue(entity, this);
	}
	
}
