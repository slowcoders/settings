package org.slowcoders.io.serialize;

import org.slowcoders.util.Debug;

import java.lang.reflect.Field;

/**
 * Created by zeedh on 02/02/2018.
 */

public abstract class IOSlot {

	private String key;
	private int flags;
	private IOAdapter<Object, Object> adapter;

	protected IOSlot(String key, int flags) {
		if (flags < 0) {
			throw new IllegalArgumentException("the biggest bit of flags are reserved");
		}
		this.key = key.intern();
		this.flags = flags;
	}


	IOSlot(Field f, IOCtrl p) {
		this.key = f.getName();
		if (p != null) {
			if (p.key().length() > 0) {
				this.key = p.key();
			}
			this.flags = p.flags();
			this.adapter = IOAdapter.getAdapterInstance(p.adapter());
		}
		else {
			this.flags = 0;
		}

		if (this.adapter == null) {
			adapter = IOAdapter.getDefaultAdapter(f.getGenericType());
		}
	}

	public final String getKey() {
		return key;
	}
	

	public final IOAdapter<Object, Object> getAdapter() {
		return this.adapter;
	}

	public final int getAccessFlags() {
		return flags;
	}
	
	public String toString() {
		return this.getKey();
	}

	protected abstract void setValue(Object entity, DataReader reader) throws Exception;

	protected abstract void writeValue(Object entity, DataWriter writer) throws Exception;

	protected final void setAccessFlags(int flags) {
		Debug.Assert((flags & Integer.MIN_VALUE) == (this.flags & Integer.MIN_VALUE));
		this.flags = flags;
	}
	
	protected void setAdapter_unsafe(IOAdapter<?, ?> adapter) {
		Debug.Assert(this.adapter == null);
		this.adapter = (IOAdapter<Object, Object>)adapter;
	}

}
