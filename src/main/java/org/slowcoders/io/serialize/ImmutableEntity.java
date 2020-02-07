package org.slowcoders.io.serialize;

public interface ImmutableEntity<T extends ImmutableEntity.Builder> {

    T toMutable();

    interface Builder {
        ImmutableEntity build();
    }
}
