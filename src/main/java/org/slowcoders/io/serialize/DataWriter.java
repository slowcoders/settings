package org.slowcoders.io.serialize;

import org.slowcoders.util.Debug;
import org.slowcoders.util.EnumUtils;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public abstract class DataWriter {

    private IOAdapterLoader loader;

    public DataWriter(IOAdapterLoader loader) {
        this.loader = loader;
    }

    public final IOAdapterLoader getLoader() {
        return this.loader;
    }

    public boolean shouldEncodeEnumToBitSet() {
        return true;
    }

    public boolean isPersistentStream() {
        return true;
    }

    public abstract void writeBoolean(boolean value) throws Exception;

    public abstract void writeByte(byte value) throws Exception;

    public abstract void writeChar(char value) throws Exception;

    public abstract void writeShort(short value) throws Exception;

    public abstract void writeInt(int value) throws Exception;

    public abstract void writeLong(long value) throws Exception;

    public abstract void writeFloat(float value) throws Exception;

    public abstract void writeDouble(double value) throws Exception;

    public abstract void writeNumber(Number value) throws Exception;

    public final void writeBoolean(Boolean value) throws Exception {
        if (value == null) {
            this.writeNull();
        }
        else {
            this.writeBoolean(value.booleanValue());
        }
    }

    public final void writeByte(Byte value) throws Exception {
        if (value == null) {
            this.writeNull();
        }
        else {
            this.writeByte(value.byteValue());
        }
    }

    public final void writeChar(Character value) throws Exception {
        if (value == null) {
            this.writeNull();
        }
        else {
            this.writeChar(value.charValue());
        }
    }

    public final void writeShort(Short value) throws Exception {
        if (value == null) {
            this.writeNull();
        }
        else {
            this.writeShort(value.shortValue());
        }
    }

    public final void writeInt(Integer value) throws Exception {
        if (value == null) {
            this.writeNull();
        }
        else {
            this.writeInt(value.intValue());
        }
    }

    public final void writeLong(Long value) throws Exception {
        if (value == null) {
            this.writeNull();
        }
        else {
            this.writeLong(value.longValue());
        }
    }

    public final void writeFloat(Float value) throws Exception {
        if (value == null) {
            this.writeNull();
        }
        else {
            this.writeFloat(value.floatValue());
        }
    }

    public final void writeDouble(Double value) throws Exception {
        if (value == null) {
            this.writeNull();
        }
        else {
            this.writeDouble(value.doubleValue());
        }
    }

    public void writeEnum(Enum v) throws Exception {
        if (this.shouldEncodeEnumToBitSet()) {
            int i = (v == null) ? -1 : v.ordinal();
            this.writeInt(i);
        }
        else {
            String name = (v == null) ? null : v.name();
            this.writeString(name);
        }
    }

    public void writeEnumSet(EnumSet<? extends Enum> v) throws Exception {
        if (v == null) {
            this.writeNull();
            return;
        }
        if (this.shouldEncodeEnumToBitSet()) {
            long bits = EnumUtils.toBitSet(v);
            this.writeLong(bits);
        }
        else {
            String[] values = EnumUtils.toStringArray(v);
            this.writeStringArray(values);
        }
    }


    public void writeObject(Object v) throws Exception {
        if (v == null) {
            this.writeNull();
        }
        else {
            IOAdapter<Object, ?> adapter = loader.loadAdapter(v.getClass());
            adapter.write(v, this);
        }
    }

    public abstract void writeNull() throws Exception;

    public abstract void writeString(String value) throws Exception;

    public abstract void writeBooleanArray(boolean[] values) throws Exception;

    public abstract void writeByteArray(byte[] values) throws Exception;

    public abstract void writeCharArray(char[] values) throws Exception;

    public abstract void writeShortArray(short[] values) throws Exception;

    public abstract void writeIntArray(int[] values) throws Exception;

    public abstract void writeLongArray(long[] values) throws Exception;

    public abstract void writeFloatArray(float[] values) throws Exception;

    public abstract void writeDoubleArray(double[] values) throws Exception;

    public abstract void writeStringArray(String[] values) throws Exception;

//    protected abstract void writeAggregated(byte[] values) throws Exception;

    protected abstract AggregatedStream beginAggregate(String compositeType, boolean isMap) throws Exception;

    public static abstract class AggregatedStream extends DataWriter {

        public AggregatedStream(IOAdapterLoader loader) {
            super(loader);
        }

        protected abstract boolean isMap();

        public void writeEntity(Object entity) throws Exception {
            IOField[] slots = IOEntity.registerSerializableFields(entity.getClass());
            writeEntity(entity, slots);
        }

        public void writeEntity(Object entity, IOField[] slots) throws Exception {
            if (entity instanceof IOEntity) {
                IOEntity ioEntity = (IOEntity)entity;
                for (IOField slot : slots) {
                    if (isMap()) {
                        this.writeString(slot.getKey());
                    }
                    ioEntity.writeProperty(slot, this);
                }
            }
            else {
                for (IOField slot : slots) {
                    if (isMap()) {
                        this.writeString(slot.getKey());
                    }
                    slot.writeValue(entity, this);
                }
            }
        }

        protected abstract void close() throws Exception;
    }

    public final <T> void writeCollection(Collection values, IOAdapter<T, ?> itemAdapter) throws Exception {
        if (values == null) {
            this.writeNull();
        }
        else {
            this.writeCollection(values.getClass().getName(), values.iterator(), values.size(), itemAdapter);
        }
    }

    public final <T> void writeCollection(String collectionType, Iterator<T> values, int length, IOAdapter<T, ?> itemAdapter) throws Exception {
        if (collectionType.contains("SingletonImmutableList")) {
            Debug.trap();
        }
        AggregatedStream out = this.beginAggregate(collectionType, false);

        while (values.hasNext()) {
            T v = values.next();
            if (v == null) {
                out.writeNull();
            }
            else {
                itemAdapter.write(v, out);
            }
        }
        out.close();
        return;
    }

    public final <K, V> void writeMap(Map map, IOAdapter<K, ?> keyAdapter, IOAdapter<V, ?> valueAdapter) throws Exception {
        if (map == null) {
            this.writeNull();
        }
        else {
            writeMap(map.getClass().getName(), map.entrySet().iterator(), keyAdapter, valueAdapter);
        }
    }

    public final <K, V> void writeMap(String mapType, Iterator<Map.Entry<K, V>> entries, IOAdapter<K, ?> keyAdapter, IOAdapter<V, ?> valueAdapter) throws Exception {
        AggregatedStream out = this.beginAggregate(mapType, true);
        while (entries.hasNext()) {
            Entry<K, V> e = entries.next();
            K key = e.getKey();
            V value = e.getValue();
            keyAdapter.write(key, out);
            valueAdapter.write(value, out);
        }
        out.close();
        return;
    }

//    public final void writeValue(Object v, IOAdapter<Object> tr) throws Exception {
//        tr.write(v, this);
//    }

    public final void writeValue(Object entity, IOField field) throws Exception {
        field.writeValue(entity, this);
    }

}
