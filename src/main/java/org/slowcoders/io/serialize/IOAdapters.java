package org.slowcoders.io.serialize;

import com.google.common.collect.CollectionBuilder;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.slowcoders.io.serialize.DataReader.AutoCloseStream;
import org.slowcoders.util.Debug;
import org.slowcoders.util.ClassUtils;
import org.slowcoders.util.EnumUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;

@SuppressWarnings("unchecked")
public abstract class IOAdapters {

	public static abstract class _Boolean<T> extends PrimitiveAdapter<T, Boolean> {

		public abstract T decode(boolean v, boolean isImmutable) throws Exception;

		public abstract boolean encode(T v) throws Exception;

		@Override
		public final EncodingType getPreferredTransferType() {
			return EncodingType.Integer;
		}

		public T read(DataReader in) throws Exception {
			boolean v = in.readBoolean();
			return in.wasNull() ? null: decode(v, in.isImmutable());
		}

		public void write(T v, DataWriter out) throws Exception {
			if (v == null)
				out.writeNull();
			else
				out.writeBoolean(encode(v));
		}

		public void writeCompatible(Object v, DataWriter out) throws Exception {
			if (v instanceof Boolean) {
				out.writeBoolean(((Boolean)v).booleanValue());
			}
			else if (v instanceof Number) {
				out.writeBoolean(((Number)v).longValue() != 0);
			}
			else {
				write((T)v, out);
			}
		}

		public T decode(Boolean encoded, boolean isImmutable) throws Exception {
			return decode(encoded == null ? false : encoded.booleanValue(), isImmutable);
		}


		static final class Adapter extends _Boolean<Boolean> {
			final PrimitiveAdapter primitiveAdapter;

			Adapter(boolean isPrimitive) {
				this.primitiveAdapter = isPrimitive ? this : null;
			}

			final PrimitiveAdapter asPrimitiveAdapter() { return this.primitiveAdapter; }

			public Boolean decode(boolean v, boolean isImmutable) { return v; }

			public boolean encode(Boolean v) { return v; }

			void setValue(Object entity, Field field, DataReader in) throws Exception {
				boolean v = in.readBoolean();
				field.setBoolean(entity, v);
			}

			void writeValue(Object entity, Field field, DataWriter out) throws Exception {
				boolean v = field.getBoolean(entity);
				out.writeBoolean(v);
			}

		}
	}


	public static abstract class _Byte<T> extends PrimitiveAdapter<T, Number> {

		public abstract T decode(byte v, boolean isImmutable) throws Exception;

		public abstract byte encode(T v) throws Exception;

		@Override
		public final EncodingType getPreferredTransferType() {
			return EncodingType.Integer;
		}

		public T read(DataReader in) throws Exception {
			byte v = in.readByte();
			return in.wasNull() ? null: decode(v, in.isImmutable());
		}

		public void write(T v, DataWriter out) throws Exception {
			if (v == null)
				out.writeNull();
			else
				out.writeByte(encode(v));
		}

		public void writeCompatible(Object v, DataWriter out) throws Exception {
			if (v instanceof Number) {
				out.writeByte(((Number)v).byteValue());
			}
			else {
				write((T)v, out);
			}
		}

		public T decode(Number encoded, boolean isImmutable) throws Exception {
			return decode(encoded == null ? 0 : encoded.byteValue(), isImmutable);
		}

		static final class Adapter extends _Byte<Byte> {
			final PrimitiveAdapter primitiveAdapter;

			Adapter(boolean isPrimitive) {
				this.primitiveAdapter = isPrimitive ? this : null;
			}

			final PrimitiveAdapter asPrimitiveAdapter() { return this.primitiveAdapter; }

			public Byte decode(byte v, boolean isImmutable) { return v; }

			public byte encode(Byte v) { return v; }

			void setValue(Object entity, Field field, DataReader in) throws Exception {
				byte v = in.readByte();
				field.setByte(entity, v);
			}

			void writeValue(Object entity, Field field, DataWriter out) throws Exception {
				byte v = field.getByte(entity);
				out.writeByte(v);
			}

		}
	}


	public static abstract class _Char<T> extends PrimitiveAdapter<T, Character> {

		public abstract T decode(char v, boolean isImmutable) throws Exception;

		public abstract char encode(T v) throws Exception;

		@Override
		public final EncodingType getPreferredTransferType() {
			return EncodingType.Integer;
		}

		public T read(DataReader in) throws Exception {
			char v = in.readChar();
			return in.wasNull() ? null: decode(v, in.isImmutable());
		}

		public void write(T v, DataWriter out) throws Exception {
			if (v == null)
				out.writeNull();
			else
				out.writeChar(encode(v));
		}

		public void writeCompatible(Object v, DataWriter out) throws Exception {
			if (v instanceof Character) {
				out.writeChar(((Character)v).charValue());
			}
			else if (v instanceof Number) {
				out.writeChar((char)((Number)v).intValue());
			}
			else {
				write((T)v, out);
			}
		}

		public T decode(Character encoded, boolean isImmutable) throws Exception {
			return decode(encoded == null ? 0 : encoded.charValue(), isImmutable);
		}

		static final class Adapter extends _Char<Character> {
			final PrimitiveAdapter primitiveAdapter;

			Adapter(boolean isPrimitive) {
				this.primitiveAdapter = isPrimitive ? this : null;
			}

			final PrimitiveAdapter asPrimitiveAdapter() { return this.primitiveAdapter; }

			public Character decode(char v, boolean isImmutable) { return v; }

			public char encode(Character v) { return v; }

			void setValue(Object entity, Field field, DataReader in) throws Exception {
				char v = in.readChar();
				field.setChar(entity, v);
			}

			void writeValue(Object entity, Field field, DataWriter out) throws Exception {
				char v = field.getChar(entity);
				out.writeChar(v);
			}

		}
	}



	public static abstract class _Short<T> extends PrimitiveAdapter<T, Number> {

		public abstract T decode(short v, boolean isImmutable) throws Exception;

		public abstract short encode(T v) throws Exception;

		@Override
		public final EncodingType getPreferredTransferType() {
			return EncodingType.Integer;
		}

		public T read(DataReader in) throws Exception {
			short v = in.readShort();
			return in.wasNull() ? null: decode(v, in.isImmutable());
		}

		public void write(T v, DataWriter out) throws Exception {
			if (v == null)
				out.writeNull();
			else
				out.writeShort(encode(v));
		}

		public void writeCompatible(Object v, DataWriter out) throws Exception {
			if (v instanceof Number) {
				out.writeShort(((Number)v).shortValue());
			}
			else {
				write((T)v, out);
			}
		}

		public T decode(Number encoded, boolean isImmutable) throws Exception {
			return decode(encoded == null ? 0 : encoded.shortValue(), isImmutable);
		}

		static final class Adapter extends _Short<Short> {
			final PrimitiveAdapter primitiveAdapter;

			Adapter(boolean isPrimitive) {
				this.primitiveAdapter = isPrimitive ? this : null;
			}

			final PrimitiveAdapter asPrimitiveAdapter() { return this.primitiveAdapter; }

			public Short decode(short v, boolean isImmutable) { return v; }

			public short encode(Short v) { return v; }

			void setValue(Object entity, Field field, DataReader in) throws Exception {
				short v = in.readShort();
				field.setShort(entity, v);
			}

			void writeValue(Object entity, Field field, DataWriter out) throws Exception {
				short v = field.getShort(entity);
				out.writeShort(v);
			}

		}
	}


	public static abstract class _Int<T> extends PrimitiveAdapter<T, Number> {

		public abstract T decode(int v, boolean isImmutable) throws Exception;

		public abstract int encode(T v) throws Exception;

		@Override
		public final EncodingType getPreferredTransferType() {
			return EncodingType.Integer;
		}

		public T read(DataReader in) throws Exception {
			int v = in.readInt();
			return in.wasNull() ? null: decode(v, in.isImmutable());
		}

		public void write(T v, DataWriter out) throws Exception {
			if (v == null)
				out.writeNull();
			else
				out.writeInt(encode(v));
		}

		public void writeCompatible(Object v, DataWriter out) throws Exception {
			if (v instanceof Number) {
				out.writeInt(((Number)v).intValue());
			}
			else {
				write((T)v, out);
			}
		}

		public T decode(Number encoded, boolean isImmutable) throws Exception {
			return decode(encoded == null ? 0 : encoded.intValue(), isImmutable);
		}

		static final class Adapter extends _Int<Integer> {
			final PrimitiveAdapter primitiveAdapter;

			Adapter(boolean isPrimitive) {
				this.primitiveAdapter = isPrimitive ? this : null;
			}

			final PrimitiveAdapter asPrimitiveAdapter() { return this.primitiveAdapter; }

			public Integer decode(int v, boolean isImmutable) { return v; }

			public int encode(Integer v) { return v; }

			void setValue(Object entity, Field field, DataReader in) throws Exception {
				int v = in.readInt();
				field.setInt(entity, v);
			}

			void writeValue(Object entity, Field field, DataWriter out) throws Exception {
				int v = field.getInt(entity);
				out.writeInt(v);
			}

		}
	}


	public static abstract class _Long<T> extends PrimitiveAdapter<T, Number> {

		public abstract T decode(long v, boolean isImmutable) throws Exception;

		public abstract long encode(T v) throws Exception;

		@Override
		public final EncodingType getPreferredTransferType() {
			return EncodingType.Integer;
		}

		public T read(DataReader in) throws Exception {
			long v = in.readLong();
			return in.wasNull() ? null: decode(v, in.isImmutable());
		}

		public void write(T v, DataWriter out) throws Exception {
			if (v == null)
				out.writeNull();
			else
				out.writeLong(encode(v));
		}

		public void writeCompatible(Object v, DataWriter out) throws Exception {
			if (v instanceof Number) {
				out.writeLong(((Number)v).longValue());
			}
			else {
				write((T)v, out);
			}
		}

		public T decode(Number encoded, boolean isImmutable) throws Exception {
			return decode(encoded == null ? 0 : encoded.longValue(), isImmutable);
		}

		static final class Adapter extends _Long<Long> {
			final PrimitiveAdapter primitiveAdapter;

			Adapter(boolean isPrimitive) {
				this.primitiveAdapter = isPrimitive ? this : null;
			}

			final PrimitiveAdapter asPrimitiveAdapter() { return this.primitiveAdapter; }

			public Long decode(long v, boolean isImmutable) { return v; }

			public long encode(Long v) { return v; }

			void setValue(Object entity, Field field, DataReader in) throws Exception {
				long v = in.readLong();
				field.setLong(entity, v);
			}

			void writeValue(Object entity, Field field, DataWriter out) throws Exception {
				long v = field.getLong(entity);
				out.writeLong(v);
			}

		}
	}



	public static abstract class _Float<T> extends PrimitiveAdapter<T, Number> {

		public abstract T decode(float v, boolean isImmutable) throws Exception;

		public abstract float encode(T v) throws Exception;

		@Override
		public final EncodingType getPreferredTransferType() {
			return EncodingType.Real;
		}

		public T read(DataReader in) throws Exception {
			float v = in.readFloat();
			return in.wasNull() ? null: decode(v, in.isImmutable());
		}

		public void write(T v, DataWriter out) throws Exception {
			if (v == null)
				out.writeNull();
			else
				out.writeFloat(encode(v));
		}

		public void writeCompatible(Object v, DataWriter out) throws Exception {
			if (v instanceof Number) {
				out.writeFloat(((Number)v).floatValue());
			}
			else {
				write((T)v, out);
			}
		}

		public T decode(Number encoded, boolean isImmutable) throws Exception {
			return decode(encoded == null ? 0 : encoded.floatValue(), isImmutable);
		}


		static final class Adapter extends _Float<Float> {
			final PrimitiveAdapter primitiveAdapter;

			Adapter(boolean isPrimitive) {
				this.primitiveAdapter = isPrimitive ? this : null;
			}

			final PrimitiveAdapter asPrimitiveAdapter() { return this.primitiveAdapter; }

			public Float decode(float v, boolean isImmutable) { return v; }

			public float encode(Float v) { return v; }

			void setValue(Object entity, Field field, DataReader in) throws Exception {
				float v = in.readFloat();
				field.setFloat(entity, v);
			}

			void writeValue(Object entity, Field field, DataWriter out) throws Exception {
				float v = field.getFloat(entity);
				out.writeFloat(v);
			}

		}
	}


	public static abstract class _Double<T> extends PrimitiveAdapter<T, Number> {

		public abstract T decode(double v, boolean isImmutable) throws Exception;

		public abstract double encode(T v) throws Exception;

		@Override
		public final EncodingType getPreferredTransferType() {
			return EncodingType.Real;
		}

		public T read(DataReader in) throws Exception {
			double v = in.readDouble();
			return in.wasNull() ? null: decode(v, in.isImmutable());
		}

		public void write(T v, DataWriter out) throws Exception {
			if (v == null)
				out.writeNull();
			else
				out.writeDouble(encode(v));
		}

		public void writeCompatible(Object v, DataWriter out) throws Exception {
			if (v instanceof Number) {
				out.writeDouble(((Number)v).doubleValue());
			}
			else {
				write((T)v, out);
			}
		}

		public T decode(Number encoded, boolean isImmutable) throws Exception {
			return decode(encoded == null ? 0 : encoded.doubleValue(), isImmutable);
		}

		static final class Adapter extends _Double<Double> {
			final PrimitiveAdapter primitiveAdapter;

			Adapter(boolean isPrimitive) {
				this.primitiveAdapter = isPrimitive ? this : null;
			}

			final PrimitiveAdapter asPrimitiveAdapter() { return this.primitiveAdapter; }

			public Double decode(double v, boolean isImmutable) { return v; }

			public double encode(Double v) { return v; }

			void setValue(Object entity, Field field, DataReader in) throws Exception {
				double v = in.readDouble();
				field.setDouble(entity, v);
			}

			void writeValue(Object entity, Field field, DataWriter out) throws Exception {
				double v = field.getDouble(entity);
				out.writeDouble(v);
			}

		}
	}

	//=================================================================================//

	static abstract class _BitStream<DecodeT, EncodeT> extends IOAdapter<DecodeT, EncodeT> {

		public abstract EncodeT encode(DecodeT v) throws Exception;

		@Override
		public EncodingType getPreferredTransferType() {
			return EncodingType.Numbers;
		}

	}

	public static abstract class _BooleanArray<T> extends _BitStream<T, boolean[]> {

		public T read(DataReader in) throws Exception {
			boolean[] v = in.readBooleanArray();
			return v == null ? null: decode(v, in.isImmutable());
		}

		public void write(T v, DataWriter out) throws Exception {
			boolean[] data = v == null ? null : encode(v);
			out.writeBooleanArray(data);
		}

		public void writeCompatible(Object v, DataWriter out) throws Exception {
			if (v instanceof boolean[]) {
				out.writeBooleanArray((boolean[])v);
			}
			else {
				write((T)v, out);
			}
		}
		
		static final _BooleanArray adapter = new _BooleanArray<boolean[]>() {

			public boolean[] decode(boolean[] v, boolean isImmutable) { return v; }

			@Override
			public boolean[] encode(boolean[] v) {
				return (boolean[])v;
			}
		};
	}

	public static abstract class _ByteArray<T> extends _BitStream<T, byte[]> {

		public T read(DataReader in) throws Exception {
			byte[] v = in.readByteArray();
			return v == null ? null: decode(v, in.isImmutable());
		}

		public void write(T v, DataWriter out) throws Exception {
			byte[] data = v == null ? null : encode(v);
			out.writeByteArray(data);
		}

		public void writeCompatible(Object v, DataWriter out) throws Exception {
			if (v instanceof byte[]) {
				out.writeByteArray((byte[])v);
			}
			else {
				write((T)v, out);
			}
		}

		static final _ByteArray<byte[]> adapter = new _ByteArray<byte[]>() {

			public byte[] decode(byte[] v, boolean isImmutable) { return v; }

			public byte[] encode(byte[] v) { return v; }
		};
	}

	public static abstract class _CharArray<T> extends _BitStream<T, char[]> {

		public T read(DataReader in) throws Exception {
			char[] v = in.readCharArray();
			return v == null ? null: decode(v, in.isImmutable());
		}

		public void write(T v, DataWriter out) throws Exception {
			char[] data = v == null ? null : encode(v);
			out.writeCharArray(data);
		}

		public void writeCompatible(Object v, DataWriter out) throws Exception {
			if (v instanceof char[]) {
				out.writeCharArray((char[])v);
			}
			else {
				write((T)v, out);
			}
		}

		static final _CharArray<char[]> adapter = new _CharArray<char[]>() {

			public char[] decode(char[] v, boolean isImmutable) { return v; }

			public char[] encode(char[] v) { return v; }
		};
	}

	public static abstract class _ShortArray<T> extends _BitStream<T, short[]> {

		public T read(DataReader in) throws Exception {
			short[] v = in.readShortArray();
			return v == null ? null: decode(v, in.isImmutable());
		}

		public void write(T v, DataWriter out) throws Exception {
			short[] data = v == null ? null : encode(v);
			out.writeShortArray(data);
		}
		
		public void writeCompatible(Object v, DataWriter out) throws Exception {
			if (v instanceof short[]) {
				out.writeShortArray((short[])v);
			}
			else {
				write((T)v, out);
			}
		}


		static final _ShortArray<short[]> adapter = new _ShortArray<short[]>() {

			public short[] decode(short[] v, boolean isImmutable) { return v; }

			public short[] encode(short[] v) { return v; }
		};
	}

	public static abstract class _IntArray<T> extends _BitStream<T, int[]> {

		public T read(DataReader in) throws Exception {
			int[] v = in.readIntArray();
			return v == null ? null: decode(v, in.isImmutable());
		}

		public void write(T v, DataWriter out) throws Exception {
			int[] data = v == null ? null : encode(v);
			out.writeIntArray(data);
		}

		public void writeCompatible(Object v, DataWriter out) throws Exception {
			if (v instanceof int[]) {
				out.writeIntArray((int[])v);
			}
			else {
				write((T)v, out);
			}
		}

		static final _IntArray<int[]> adapter = new _IntArray<int[]>() {

			public int[] decode(int[] v, boolean isImmutable) { return v; }

			public int[] encode(int[] v) { return v; }
		};
	}


	public static abstract class _LongArray<T> extends _BitStream<T, long[]> {

		public _LongArray() {
			Debug.trap();
		}

		public T read(DataReader in) throws Exception {
			long[] v = in.readLongArray();
			return v == null ? null: decode(v, in.isImmutable());
		}

		public void write(T v, DataWriter out) throws Exception {
			long[] data = v == null ? null : encode(v);
			out.writeLongArray(data);
		}

		public void writeCompatible(Object v, DataWriter out) throws Exception {
			if (v instanceof long[]) {
				out.writeLongArray((long[])v);
			}
			else {
				write((T)v, out);
			}
		}

		static final _LongArray<long[]> adapter = new _LongArray<long[]>() {

			public long[] decode(long[] v, boolean isImmutable) { return v; }

			public long[] encode(long[] v) { return v; }
		};
	}

	public static abstract class _FloatArray<T> extends _BitStream<T, float[]> {

		public T read(DataReader in) throws Exception {
			float[] v = in.readFloatArray();
			return v == null ? null: decode(v, in.isImmutable());
		}

		public void write(T v, DataWriter out) throws Exception {
			float[] data = v == null ? null : encode(v);
			out.writeFloatArray(data);
		}

		public void writeCompatible(Object v, DataWriter out) throws Exception {
			if (v instanceof float[]) {
				out.writeFloatArray((float[])v);
			}
			else {
				write((T)v, out);
			}
		}

		static final _FloatArray<float[]> adapter = new _FloatArray<float[]>() {

			public float[] decode(float[] v, boolean isImmutable) { return v; }

			public float[] encode(float[] v) { return v; }
		};
	}

	public static abstract class _DoubleArray<T> extends _BitStream<T, double[]> {

		public T read(DataReader in) throws Exception {
			double[] v = in.readDoubleArray();
			return v == null ? null: decode(v, in.isImmutable());
		}

		public void write(T v, DataWriter out) throws Exception {
			double[] data = v == null ? null : encode(v);
			out.writeDoubleArray(data);
		}

		public void writeCompatible(Object v, DataWriter out) throws Exception {
			if (v instanceof double[]) {
				out.writeDoubleArray((double[])v);
			}
			else {
				write((T)v, out);
			}
		}

		static final _DoubleArray<double[]> adapter = new _DoubleArray<double[]>() {

			public double[] decode(double[] v, boolean isImmutable) { return v; }

			public double[] encode(double[] v) { return v; }
		};
	}

	//=================================================================================//

	public static abstract class _String<T> extends _BitStream<T, String> {

		@Override
		public EncodingType getPreferredTransferType() {
			return EncodingType.PrintableText;
		}

		public T read(DataReader in) throws Exception {
			String v = in.readString();
			return v == null ? null: decode(v, in.isImmutable());
		}

		public void write(T v, DataWriter out) throws Exception {
			String data = v == null ? null : encode(v);
			out.writeString(data);
		}

		public void writeCompatible(Object v, DataWriter writer) throws Exception {
			if (v instanceof String) {
				writer.writeString(v.toString());
			}
			else {
				this.write((T)v, writer);
			}
		}

		public static final _String<String> adapter = new _String<String>() {

			@Override
			public String decode(String v, boolean isImmutable) {
				return v;
			}

			@Override
			public String encode(String v) {
				return v;
			}
		};
	}


	public static class _Number extends IOAdapter<Number, Number> {

		public Number read(DataReader in) throws Exception {
			Number v = in.readNumber();
			return v;
		}

		public void write(Number v, DataWriter out) throws Exception {
			out.writeNumber(v);
		}

		@Override
		public final EncodingType getPreferredTransferType() {
			return EncodingType.Integer;
		}

		@Override
		public Number decode(Number number, boolean isImmutable) throws Exception {
			return number;
		}

		public static final _Number adapter = new _Number();
	}


	public static abstract class _Aggregate<T> extends IOAdapter<T, Object> {

		@Override
		public void writeCompatible(Object v, DataWriter writer) throws Exception {
			EncodingType encodeType = this.getPreferredTransferType();
			if (encodeType == EncodingType.PrintableText) {
				if (v instanceof String) {
					writer.writeString(v.toString());
					return;
				} else if (v instanceof String[]) {
					writer.writeStringArray((String[]) v);
					return;
				}
			} else if (encodeType == EncodingType.BitStream) {
				if (v instanceof byte[]) {
					writer.writeByteArray((byte[]) v);
					return;
				}
			}
			write((T)v, writer);
		}

		public T decode(Object encoded, boolean isImmutable) throws Exception {
			return (T)encoded;
		}


	}

	public static abstract class _Iterable<T, E> extends _Aggregate<T> {

		private final IOAdapter itemAdapter;

		protected _Iterable(IOAdapter itemAdapter) {
			this.itemAdapter = itemAdapter;
		}

		public final IOAdapter getItemAdapter() {
		    return this.itemAdapter;
        }

		protected boolean isSet() {
			return false;
		}

		@Override
		public EncodingType getPreferredTransferType() {
			EncodingType pt = itemAdapter.getPreferredTransferType();
			if (pt != EncodingType.PrintableText) {
				pt = EncodingType.BitStream;
			}
			return pt;
		}


		public T read(DataReader in) throws Exception {
			DataReader.DataIterator<E> iterator = in.readCollection(itemAdapter);
			if (iterator == null) {
				return isSet() ? (T)ImmutableSet.of() : (T)ImmutableList.of();
			}

			int cntItem = iterator.getItemCount();
			String type$ = iterator.getCollectionType();
			CollectionBuilder<E> builder = new CollectionBuilder<>(cntItem < 0 ? 4 : cntItem);

			while (iterator.hasNext()) {
				builder.add(iterator.next());
			}
			T res = this.build(builder, type$, in.isImmutable());

			return res;
		}

        protected abstract T build(CollectionBuilder<E> builder, String type$, boolean isImmutable) throws Exception;

		public static abstract class _Factory implements IOAdapterFactory {
			public synchronized IOAdapter createAdapter(Type iterableType, IOAdapterLoader adapterLoader) {
				Type[] paramTypes = ClassUtils.getGenericParameters(iterableType);
				Debug.Assert(paramTypes != null && paramTypes.length > 0);
				Type itemType = paramTypes[0];
				return makeAdapter(itemType, adapterLoader);
			}

			protected _Iterable makeAdapter(Type itemType, IOAdapterLoader adapterLoader) {
                IOAdapter itemAdapter = adapterLoader.loadAdapter(itemType);
				return createAdapter(itemAdapter);
			}

			public abstract _Iterable createAdapter(IOAdapter itemAdapter);
		}

	}

    public static class _Array<E> extends _Iterable<Object, E> {

        public _Array(IOAdapter itemAdapter) {
            super(itemAdapter);
        }

        @Override
        protected Object build(CollectionBuilder<E> builder, String arrayType, boolean isImmutable) throws Exception {
        	Object values = isImmutable ? builder.build() : builder.toArray(arrayType);
            return values;
        }

        public void write(Object v, DataWriter out) throws Exception {
            if (v == null) {
                out.writeNull();
            }
            else {
            	Object[] values = (Object[])v;
                Iterator iterator = Arrays.asList(values).iterator();
                String type$ = values.getClass().getName();
                out.writeCollection(type$, iterator, values.length, getItemAdapter());
            }
        }

        static _Factory factory = new _Factory() {
            public _Iterable createAdapter(IOAdapter itemAdapter) {
                return new _Array(itemAdapter);
            }
        };
    }

    public static class _Collection<T extends Collection<E>, E> extends _Iterable<T, E> {

        public _Collection(IOAdapter itemAdapter) {
            super(itemAdapter);
        }

        @Override
        protected T build(CollectionBuilder<E> builder, String collectionType, boolean isImmutable) throws Exception {
        	if (isImmutable) {
        		return isSet() ? (T)builder.buildSet() : (T)builder.build();
			}
            T values = createCollection(collectionType);
            for (int i = 0, end = builder.size(); i < end; i ++) {
                values.add(builder.get(i));
            }
            return values;
        }

        public void write(T values, DataWriter out) throws Exception {
            out.writeCollection(values, getItemAdapter());
        }

        protected T createCollection(String collectionType) throws Exception {
            if (collectionType == null) {
                return isSet() ? (T)new HashSet() : (T)new ArrayList() ;
            } else {
                return (T)Class.forName(collectionType).newInstance();
            }
        }

		protected static abstract class _CachedFactory extends _Iterable._Factory {
			private final HashMap<Type,_Collection> adapters = new HashMap<>();

			public synchronized _Collection makeAdapter(Type itemType, IOAdapterLoader adapterLoader) {
				_Collection adapter = adapters.get(itemType);
				if (adapter == null) {
					adapter = (_Collection)super.makeAdapter(itemType, adapterLoader);
					adapters.put(itemType, adapter);
				}
				return adapter;
			}
		}

		static _Factory factory = new _CachedFactory() {
            public _Iterable createAdapter(IOAdapter itemAdapter) {
                return new _Collection(itemAdapter);
            }
        };
    }


    public static class _Set<T extends Set<E>, E> extends _Collection<T, E> {

        public _Set(IOAdapter itemAdapter) {
            super(itemAdapter);
        }

		protected boolean isSet() {
			return true;
		}
        static _Factory factory = new _CachedFactory() {
            public _Iterable createAdapter(IOAdapter itemAdapter) {
            	if (itemAdapter instanceof _Enum.Adapter) {
            		return new _EnumSet.Adapter(((_Enum.Adapter)itemAdapter).getEnumType());
				}
                return new _Set(itemAdapter);
            }
        };
    }


    public static abstract class _Dictionary<T, K, V> extends _Aggregate<T> {
		private final IOAdapter keyAdapter;
		private final IOAdapter valueAdapter;

		public _Dictionary(IOAdapter keyAdapter, IOAdapter valueAdapter) {
			this.keyAdapter = keyAdapter;
			this.valueAdapter = valueAdapter;
		}

		public final IOAdapter getKeyAdapter() {
			return keyAdapter;
		}

		public final IOAdapter getValueAdapter() {
			return valueAdapter;
		}

		@Override
		public EncodingType getPreferredTransferType() {
			if (keyAdapter.getPreferredTransferType() != EncodingType.BitStream
			&&  valueAdapter.getPreferredTransferType() != EncodingType.BitStream) {
				return EncodingType.BitStream;
			}
			return EncodingType.BitStream;
		}

		public T read(DataReader in) throws Exception {
			DataReader.KeyValueCursor<K, V> dictionary = in.readDictionary(keyAdapter, valueAdapter);
			if (dictionary == null){
				return null;
			}
			String type$ = dictionary.getMapType();
			return build(dictionary, type$, in.isImmutable());
		}

        protected abstract T build(DataReader.KeyValueCursor<K, V> keyValueCursor, String dictionaryType, boolean isImmutable) throws Exception;

        protected T createDictionary(String dictionaryType) {
            if (dictionaryType == null) {
                return (T)new HashMap<>();
            }
            else {
                return ClassUtils.newInstance(dictionaryType);
            }
        }

        public static abstract class _Factory implements IOAdapterFactory {

			public synchronized IOAdapter createAdapter(Type mapType, IOAdapterLoader adapterLoader) {
                Type[] paramTypes = ClassUtils.getGenericParameters(mapType);
                return makeAdapter(paramTypes[0], paramTypes[1], adapterLoader);
			}

            protected _Dictionary makeAdapter(Type keyType, Type valueType, IOAdapterLoader adapterLoader) {
                IOAdapter keyAdapter = adapterLoader.loadAdapter(keyType);
                IOAdapter valueAdapter = adapterLoader.loadAdapter(valueType);
                _Dictionary adapter = createAdapter(keyAdapter, valueAdapter);
                return adapter;
            }

            protected abstract _Dictionary createAdapter(IOAdapter keyAdapter, IOAdapter valueAdapter);

        }
	}


    public static class _Map<T extends Map, K, V> extends _Dictionary<T, K, V> {

        public _Map(IOAdapter keyAdapter, IOAdapter valueAdapter) {
            super(keyAdapter, valueAdapter);
        }

        @Override
        protected T build(DataReader.KeyValueCursor keyValueCursor, String dictionaryType, boolean isImmutable) throws Exception {
        	if (isImmutable) {
				ImmutableMap.Builder builder = new ImmutableMap.Builder();
				while (keyValueCursor.next()) {
					if (keyValueCursor.getValue() == null) {
						Debug.wtf("Map cannot have null value : " + keyValueCursor.getKey() + " is null");
					}
					builder.put(keyValueCursor.getKey(), keyValueCursor.getValue());
				}
				return (T)builder.build();
			}
        	else {
				T values = createDictionary(dictionaryType);
				while (keyValueCursor.next()) {
					values.put(keyValueCursor.getKey(), keyValueCursor.getValue());
				}
				return values;
			}
        }

        @Override
        public void write(T map, DataWriter writer) throws Exception {
           writer.writeMap(map, getKeyAdapter(), getValueAdapter());
        }

		static _Factory factory = new _Factory() {
			private final HashMap<String, _Map> adapters = new HashMap<>();

			@Override
			protected _Map makeAdapter(Type keyType, Type valueType, IOAdapterLoader adapterLoader) {
				String key = ClassUtils.toClass(keyType).getName() + ":" + ClassUtils.toClass(valueType).getName();
				_Map adapter = adapters.get(key);
				if (adapter == null) {
					adapter = (_Map)super.makeAdapter(keyType, valueType, adapterLoader);
					adapters.put(key, adapter);
				}
				return adapter;
			}

			@Override
			protected _Map createAdapter(IOAdapter keyAdapter, IOAdapter valueAdapter) {
				return new _Map(keyAdapter, valueAdapter);
			}
        };
    };


    public static class _Object<T> extends _Aggregate<T> {

		@SuppressWarnings("unchecked")
		public T newInstance(String entityType, Object context) throws Exception {
			Class<T> clazz = (Class<T>) Class.forName(entityType);
			IOCtrl ioctrl = clazz.getAnnotation(IOCtrl.class);
			if (ioctrl != null) {
				//Factory factory = getFactory(clazz, null);
			}
			return ClassUtils.newInstance(clazz);
		}

		@Override
		public EncodingType getPreferredTransferType() {
			return EncodingType.BitStream;
		}

		public final T read(DataReader reader) throws Exception {
			AutoCloseStream in = reader.openChunkedStream();
			if (in == null) {
				return null;
			}
			String type = in.getEntityType();
			T entity = this.newInstance(type, in.getContext());
			this.readContent(entity, in);
			return entity;
		}

		public void write(T entity, DataWriter writer) throws Exception {
			if (entity == null) {
				writer.writeNull();
				return;
			}
			
			String entityType = this.getEntityType(entity);
			DataWriter.AggregatedStream out = writer.beginAggregate(entityType, writer.isPersistentStream());
			writeContent(entity, out);
			out.close();
		}

		protected String getEntityType(T entity) {
			return entity.getClass().getName();
		}

		protected void readContent(T entity, AutoCloseStream in) throws Exception {
			IOField[] slots = IOEntity.registerSerializableFields(entity.getClass());
			in.readEntity(entity, slots);
		}

		protected void writeContent(T entity, DataWriter.AggregatedStream out) throws Exception {
			out.writeEntity(entity);
		}

		protected static final _Object adapter = new _Object<Object>();

	}


	public static abstract class _Enum<T, E extends Enum<E>> extends _BitStream<T, E> {

		private Class<E> enumType;

		protected _Enum(Class<E> enumType) {
			this.enumType = enumType;
		}

		public T read(DataReader in) throws Exception {
			E e = in.readEnum(enumType);
			return e == null ? null : decode(e, in.isImmutable());
		}

		public void write(T v, DataWriter out) throws Exception {
			E e = v == null ? null : encode(v);
			out.writeEnum(e);
		}

		public final Class<E> getEnumType() {
			return this.enumType;
		}

		@Override
		public EncodingType getPreferredTransferType() {
			return EncodingType.Enum;
		}

		@Override
		public void writeCompatible(Object v, DataWriter writer) throws Exception {
			if (v instanceof Number) {
				E e = enumType.getEnumConstants()[((Number) v).intValue()];
				writer.writeEnum(e);
			}
			else if (v instanceof String) {
				E e = EnumUtils.toEnum(enumType, v.toString());
				writer.writeEnum(e);
			}
			else {
				write((T) v, writer);
			}
		}

		static final class Adapter<E extends Enum<E>> extends _Enum<E, E> {

			protected Adapter(Class<E> enumType) {
				super(enumType);
			}

			public E decode(E v, boolean isImmutable) { return v; }

			public E encode(E v) { return v; }
		}
	}

	public static abstract class _EnumSet<T extends Set<E>, E extends Enum<E>> extends _Set<T, E> {

		private Class<E> enumType;

		protected _EnumSet(Class<E> enumType) {
			super(null);
			this.enumType = enumType;
		}

		public T read(DataReader in) throws Exception {
			Set<E> enums = in.readEnumSet(enumType, true);
			return decode(enums, in.isImmutable());
		}

		public abstract Set<E> encode(Set<E> v) throws Exception;

		public void write(T v, DataWriter out) throws Exception {
			EnumSet<E> enums = v == null ? null : (EnumSet<E>) encode(v);
			out.writeEnumSet(enums);
		}


		@Override
		public EncodingType getPreferredTransferType() {
			return EncodingType.Enum;
		}

		@Override
		protected final T build(CollectionBuilder<E> builder, String type$, boolean isImmutable) throws Exception {
			throw Debug.notImplemented();
		}

		@Override
		public void writeCompatible(Object v, DataWriter writer) throws Exception {
			if (v instanceof Number) {
				EnumSet<E> enumSet = (EnumSet<E>) EnumUtils.bitsToEnumSet(enumType, ((Number) v).longValue(), false);
				writer.writeEnumSet(enumSet);
			}
			else if (v instanceof String[]) {
				EnumSet<E> enumSet = (EnumSet<E>) EnumUtils.toEnumSet(enumType, (String[])v, false);
				writer.writeEnumSet(enumSet);
			}
			else if (v instanceof Enum[]) {
				EnumSet<E> enumSet = EnumUtils.toEnumSet(enumType, (E[])v);
				writer.writeEnumSet(enumSet);
			}
			else if (v instanceof Enum) {
				EnumSet<E> enumSet = EnumUtils.toEnumSet(enumType, (E)v);
				writer.writeEnumSet(enumSet);
			}
			else {
				write((T)v, writer);
			}
		}

		public Class<E> getEnumType() {
			return enumType;
		}

		static final class Adapter<E extends Enum<E>> extends _EnumSet<Set<E>, E> {

			protected Adapter(Type enumType) {
				super((Class<E>)ClassUtils.toClass(enumType));
			}

			public Set<E> decode(Set<E> v, boolean isImmutable) { return v; }

			public Set<E> encode(Set<E> v) {
				if (v.isEmpty()) {
					return EnumSet.noneOf(getEnumType());
				}
				return EnumSet.copyOf(v);
			}

		}
	}
}
