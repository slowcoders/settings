package org.slowcoders.io.serialize;

import java.lang.reflect.Type;

public interface IOAdapterFactory {
    IOAdapter createAdapter(Type genericType, IOAdapterLoader adapterLoader);
}
