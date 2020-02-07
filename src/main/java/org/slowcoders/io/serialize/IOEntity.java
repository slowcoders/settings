package org.slowcoders.io.serialize;

import org.slowcoders.json.JSONObject;
import org.slowcoders.util.Debug;
import org.slowcoders.util.ClassUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Created by zeedh on 02/02/2018.
 */

public interface IOEntity {

    default Object getFieldValue(IOField field) {
        return field.getFieldValue(this);
    }

    default void setFieldValue(IOField field, Object v) {
        field.setFieldValue(this, v);
    }

    default void writeProperty(IOField field, DataWriter out) throws Exception {
        field.writeValue(this, out);
    }

    default void setProperty(IOField field, DataReader in) throws Exception {
        field.setValue(this, in);
    }

    static void setProperty_unsafe(IOField field, Object entity, DataReader in) throws Exception {
        field.setValue(entity, in);
    }

    static void writeProperty_unsafe(IOField field, Object entity, DataWriter out) throws Exception {
        field.writeValue(entity, out);
    }

    static IOField[] getSerializableFields(Class<?> c) {
        return Cache.slotsMap.get(c);
    }

    static void registerSerializableFields_unsafe(Class<?> klass, IOField[] ioFields) {
        synchronized (Cache.slotsMap) {
            Cache.slotsMap.put(klass, ioFields);
        }
    }

    static IOField[] registerSerializableFields(Class<?> c) {
        synchronized (Cache.slotsMap) {
            IOField[] fields = getSerializableFields(c);
            if (fields == null) {
                ArrayList<IOField> fs = new ArrayList<IOField>();
                Cache.init(fs, c);
                fields = fs.toArray(new IOField[fs.size()]);
                registerSerializableFields_unsafe(c, fields);
            }
            return fields;
        }
    }

    static IOField findSerializableFieldByName(String name, IOField[] fields) {
        name = name.intern();
        for (IOField f : fields) {
            if (f.getFieldName() == name) {
                return f;
            }
        }
        return null;
    }

    static IOField getSerializableFieldByName(String name, IOField[] fields) throws NoSuchFieldException {
        IOField f = findSerializableFieldByName(name, fields);
        if (f == null) {
            throw new NoSuchFieldException(name);
        }
        return f;
    }

    static IOField findSerializableFieldByKey(String key, IOField[] fields) throws NoSuchFieldException {
        key = key.intern();
        for (IOField f : fields) {
            if (f.getKey() == key) {
                return f;
            }
        }
        return null;
    }

    static IOField getSerializableFieldByKey(String key, IOField[] fields) throws NoSuchFieldException {
        IOField f = findSerializableFieldByKey(key, fields);
        if (f == null) {
            throw new NoSuchFieldException(key);
        }
        return f;
    }

    static void copyEntity(Object src, Object dst) {
        Debug.Assert(src.getClass().isAssignableFrom(dst.getClass()));
        for (IOField slot : registerSerializableFields(src.getClass())) {
            Object v = slot.getFieldValue(src);
            slot.setFieldValue(dst, v);
        }
    }

    static Object getProperty(Object entity, IOField field) {
        try {
            return field.getReflectionField().get(entity);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw Debug.wtf(e);
        }
    }

    static void setProperty(Object entity, IOField field, Object value) {
        try {
            field.getReflectionField().set(entity, value);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw Debug.wtf(e);
        }
    }

    static boolean equals(Object o1, Object o2) {
        if (o1 == o2) {
            return true;
        }

        if (o1 == null || o2 == null || o1.getClass() != o2.getClass()) {
            return false;
        }

        IOField[] fields = registerSerializableFields(o1.getClass());
        for (IOField si : fields) {
            Object v1 = si.getFieldValue(o1);
            Object v2 = si.getFieldValue(o2);
            if (!Objects.deepEquals(v1, v2)) {
                return false;
            }
        }
        return true;
    }

    static JSONObject toJSON(Object obj) {
        IOField[] fields = registerSerializableFields(obj.getClass());
        JSONObject json = new JSONObject();
        try {
            for (IOField si : fields) {
                Object v = si.getFieldValue(obj);
                if (v == null) {
                    v = JSONObject.NULL;
                }
    //			else if (v instanceof IOSerializer) {
    //				v = ((IOSerializer)v).toJSON();
    //			}
                json.put(si.getKey(), v);
            }
        }
        catch (Exception e) {
            throw Debug.wtf(e);
        }
        return json;
    }

    static String toString(Object obj) {
        StringBuilder sb = new StringBuilder();
        dumpDebugInfo(obj, sb, ",\n");
        return sb.toString();
    }

    static void dumpDebugInfo(Object obj, StringBuilder sb, String separator) {
        try {
            IOField[] fields = registerSerializableFields(obj.getClass());
            for (IOField si : fields) {
                Debug.Assert(si != null);
                if (si.getReflectionField() != null) {
                    sb.append(si.getFieldName()).append(':');
                    sb.append(si.getFieldValue(obj));
                    sb.append(separator);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        sb.setLength(sb.length() - separator.length());
    }

    static void initEntity(Object entity, HashMap<String, Object> map) throws Exception {
        IOField[] slots = registerSerializableFields(entity.getClass());
        for (Map.Entry<String, Object> e : map.entrySet()) {
            String key = e.getKey();
            IOField slot = getSerializableFieldByName(key, slots);
            Object value = map.get(key);
            if (value instanceof Map && !(slot.getValueType() instanceof Map)) {
                Object obj = slot.getFieldValue(entity);
                if (obj == null) {
                    obj = ClassUtils.toClass(slot.getValueType()).newInstance();
                    slot.setFieldValue(entity, value);
                }
                initEntity(obj, (HashMap<String, Object>)value);
            }
            else {
                slot.setFieldValue(entity, value);
            }
        }
    }

    class Cache {
        static final Hashtable<Class<?>, IOField[]> slotsMap = new Hashtable<>();

        static void init(ArrayList<IOField> fs, Class<?> c) {
            if (c.getAnnotation(DoNotInheritIOProperties.class) == null) {
                Class<?> c2 = c.getSuperclass();
                if (c2 != Object.class) {
                    IOField[] fields = registerSerializableFields(c2);
                    for (IOField si : fields) {
                        fs.add(si);
                    }
                }
            }

            boolean isExplicit = c.isAssignableFrom(ExplicitIOProperties.class);
            for (Field f : c.getDeclaredFields()) {
                if (f.isSynthetic() || (f.getModifiers() & (Modifier.TRANSIENT | Modifier.STATIC | Modifier.FINAL)) != 0) {
                    continue;
                }

                IOCtrl p = f.getAnnotation(IOCtrl.class);
                if (p == null && isExplicit) {
                    continue;
                }

                IOField si = new IOField(f, p);
                fs.add(si);
            }
        }

    }
}
