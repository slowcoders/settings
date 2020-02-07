package org.slowcoders.io.serialize;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;

import org.slowcoders.util.Debug;

/**
 * Created by zeedh on 02/02/2018.
 */

public class IOField extends IOSlot {
	
	private Field field;

	protected IOField(String key, int flags) {
		super(key, flags);
	}

	protected IOField(Field f, IOCtrl p) {
		super(f, p);
		this.setReflectionField(f);
	}

	public final String getFieldName() {
		return field.getName();
	}

	public Type getValueType() {
		return this.field.getGenericType();
	}

	public final Field getReflectionField() {
		return this.field;
	}

	public final Class<?> getDeclaringClass() {
		return this.field.getDeclaringClass();
	}
	
	protected Object getFieldValue(Object entity) {
		try {
			Object value = field.get(entity);
			return value;
		} catch (Exception e) {
			throw Debug.wtf(e);
		}
	}

	protected void setFieldValue(Object entity, Object value) {
		try {
			field.set(entity, value);
		} catch (Exception e) {
			throw Debug.wtf(e);
		}
	}
	
	
	protected void setValue(Object entity, DataReader reader) throws Exception {
		IOAdapter<Object, ?> adapter = getAdapter();
		PrimitiveAdapter primitiveAdapter = adapter.asPrimitiveAdapter();
		if (primitiveAdapter != null) {
			primitiveAdapter.setValue(entity, this.field, reader);
		}
		else {
			Object v = adapter.read(reader);
			Class<?> type = this.getReflectionField().getType();
			if (type.isArray()){
//				@msg.Jonghoon.To_Daehoon("땜빵 코드")
				if (v instanceof List){
					List list = (List) v;
					Object[] v2 = (Object[]) Array.newInstance(type.getComponentType(), list.size());
					list.toArray(v2);
					v = v2;
				}
			}
			this.setFieldValue(entity, v);
		}
	}

	protected void writeValue(Object entity, DataWriter writer) throws Exception {
		IOAdapter<Object, ?> adapter = getAdapter();
		Debug.Assert(adapter != null);
		PrimitiveAdapter primitiveAdapter = adapter.asPrimitiveAdapter();
		if (primitiveAdapter != null) {
			primitiveAdapter.writeValue(entity, this.field, writer);
		}
		else {
			Object v = this.getFieldValue(entity);
			adapter.write(v, writer);
		}
	}

	public String toString() {
		return this.getValueType() + " " + this.getFieldName();
	}


	protected final void setReflectionField(Field f) {
		if (this.field != null) {
			Debug.Assert(f.equals(this.field));
		}
		else {
			this.field = f;
			field.setAccessible(true);
		}
	}

}
