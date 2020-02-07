package org.slowcoders.io.serialize;

import org.slowcoders.util.ClassUtils;

public class IOAdapterProxy<T> extends IOAdapter<T, Object> {

	protected final IOAdapter<T, Object> adapter;

	public IOAdapterProxy(String className) {
		this((IOAdapter<T, Object>)ClassUtils.tryCreateInstance(className));
	}

	public IOAdapterProxy(Class<?> clazz) {
		this(ClassUtils.newInstance((Class<IOAdapter<T, Object>>)clazz));
	}

	protected IOAdapterProxy(IOAdapter<T, Object> adapter) {
		this.adapter = adapter;
	}

	@Override
	public T read(DataReader reader) throws Exception {
		return adapter.read(reader);
	}

	@Override
	public void write(T v, DataWriter writer) throws Exception {
		adapter.write(v, writer);
	}

	@Override
	public EncodingType getPreferredTransferType() {
		return adapter.getPreferredTransferType();
	}

	@Override
	public T decode(Object encoded, boolean isImmutable) throws Exception {
		return adapter.decode(encoded, isImmutable);
	}

}
