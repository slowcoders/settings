package org.slowcoders.io.util;

import org.slowcoders.io.serialize.IOAdapter;
import org.slowcoders.io.serialize.IOAdapterLoader;
import org.slowcoders.io.serialize.IOAdapters;
import org.slowcoders.io.serialize.ImmutableEntity;

public class NVPair implements ImmutableEntity {

    private String name;
    private String value;

    protected NVPair() {
    }

    public NVPair(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public ImmutableEntity.Builder toMutable() {
        return new Builder(this);
    }

    public static class Builder implements ImmutableEntity.Builder {

        private NVPair instance = new NVPair();

        public Builder(NVPair nvPair) {
            this.instance.name = nvPair.name;
            this.instance.value = nvPair.value;
        }

        @Override
        public ImmutableEntity build() {
            return instance;
        }
    }

    static {
        IOAdapterLoader.registerDefaultAdapter(NVPair.class, new IOAdapters._String<NVPair>() {
            IOAdapter<NVPair, ?> adapter;
            private static final char SEPERATOR = '\t';

            @Override
            public NVPair decode(String s, boolean isImmutable) throws Exception {
                int split = s.indexOf(SEPERATOR);
                String key = s.substring(0, split);
                String value = s.substring(split + 1);
                return new NVPair(key, value);
            }

            @Override
            public String encode(NVPair v) throws Exception {
                String s = v.name + SEPERATOR + (v.value == null ? "" : v.value);
                return s;
            }
        });
    }
}
