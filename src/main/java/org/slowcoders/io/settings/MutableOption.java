package org.slowcoders.io.settings;

public interface MutableOption<T> {

    T get();

    boolean set(T value);

    T getDefaultValue();

    void resetToDefaultValue();

    void replaceDefaultValueByCurrentValue();

}
