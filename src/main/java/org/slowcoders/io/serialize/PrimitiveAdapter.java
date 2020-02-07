package org.slowcoders.io.serialize;

import org.slowcoders.util.Debug;

import java.lang.reflect.Field;


/*internal*/ abstract class PrimitiveAdapter<T, Encoded> extends IOAdapter<T, Encoded> {

    void setValue(Object entity, Field field, DataReader reader) throws Exception {
        throw Debug.shouldNotBeHere();
    }

    void writeValue(Object entity, Field field, DataWriter reader) throws Exception {
        throw Debug.shouldNotBeHere();
    }

}
