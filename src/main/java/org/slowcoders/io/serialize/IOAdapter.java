package org.slowcoders.io.serialize;

import org.slowcoders.util.ClassUtils;

import java.lang.reflect.Type;
import java.util.HashMap;

public abstract class IOAdapter<T, E> {

	public enum EncodingType {
		Integer,
		Real,
		Numbers,
		Enum,
		EnumSet,
		BitStream,
		PrintableText,
	}

	public static final String TEXT_SEPARATOR = "\0";

	public abstract T read(DataReader reader) throws Exception;

	public abstract void write(T v, DataWriter writer) throws Exception;

	public abstract EncodingType getPreferredTransferType();

	public abstract T decode(E encoded, boolean isImmutable) throws Exception;

	public void writeCompatible(Object v, DataWriter writer) throws Exception {
		this.write((T)v, writer);
	}

	public synchronized static IOAdapterLoader getLoader(boolean readImmutable) {
		return Singleton.defLoader;
	}

	/*internal*/ PrimitiveAdapter asPrimitiveAdapter() { return null; }

	public static IOAdapter getDefaultAdapter(Type itemType) {
		return getLoader(false).loadAdapter(itemType);
	}

	public static IOAdapter getImmutableListAdapter(Type itemType) {
		IOAdapter adapter = IOAdapters._Collection.factory.makeAdapter(itemType, Singleton.defLoader);
		return adapter;
	}

	public static IOAdapter getImmutableSetAdapter(Type itemType) {
		IOAdapter adapter = IOAdapters._Set.factory.makeAdapter(itemType, Singleton.defLoader);
		return adapter;
	}

	public static IOAdapter getImmutableMapAdapter(Type keyType, Type valueType) {
		IOAdapter adapter = IOAdapters._Map.factory.makeAdapter(keyType, valueType, Singleton.defLoader);
		return adapter;
	}

	private static final HashMap<Class<?>, IOAdapter> adapters = new HashMap<>();
	public static IOAdapter getAdapterInstance(Class<IOAdapter> adapterType) {
		if (adapterType == IOAdapter.class) return null;

		synchronized (adapters) {
			IOAdapter adapter = adapters.get(adapterType);
			if (adapter == null) {
				adapter = ClassUtils.newInstance(adapterType);
				adapters.put(adapterType, adapter);
			}
			return adapter;
		}
	}

	static class Singleton {
		static IOAdapterLoader defLoader = new IOAdapterLoader();
	}


}
