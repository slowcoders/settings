package org.slowcoders.io.serialize;

import org.joda.time.*;

import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.util.TimeZone;

public class PredefinedAdapters {

    public synchronized static Adapters registerAdapters() {
        return Adapters.instance;
    }

    public static class Adapters {
        private static Adapters instance = new Adapters();

        private Adapters() {
            System.out.print("Predefined adapter registered.");
        }

        public final IOAdapter<DateTimeZone, ?> dateTimeZoneAdapter = IOAdapterLoader.registerDefaultAdapter(
                DateTimeZone.class, new IOAdapters._String<DateTimeZone>() {
                    @Override
                    public String encode(DateTimeZone v) throws Exception {
                        return v.getID();
                    }

                    @Override
                    public DateTimeZone decode(String encoded, boolean isImmutable) throws Exception {
                        return DateTimeZone.forID(encoded);
                    }
                });

        public final IOAdapter<Class, ?> classAdapter = IOAdapterLoader.registerDefaultAdapter(
                Class.class, new IOAdapters._String<Class>() {

                    public Class decode(String v, boolean isImmutable) throws Exception {
                        Class c = Class.forName(v);
                        return c;
                    }

                    public String encode(Class v) throws Exception {
                        return v.getName();
                    }
                });

        public final IOAdapter<InetAddress, ?> inetAddressAdapter = IOAdapterLoader.registerDefaultAdapter(
                InetAddress.class, new IOAdapters._ByteArray<InetAddress>() {

                    public InetAddress decode(byte[] v, boolean isImmutable) throws Exception {
                        InetAddress c = InetAddress.getByAddress(v);
                        return c;
                    }

                    public byte[] encode(InetAddress v) throws Exception {
                        byte[] addr = v.getAddress();
                        return addr;
                    }
                });

        public final IOAdapter<URI, ?> uriAdapter = IOAdapterLoader.registerDefaultAdapter(
                URI.class, new IOAdapters._String<URI>() {

                    public URI decode(String v, boolean isImmutable) throws Exception {
                        return URI.create(v);
                    }

                    public String encode(URI v) throws Exception {
                        return v.toString();
                    }
                });

        public final IOAdapter<URL, ?> urlAdapter = IOAdapterLoader.registerDefaultAdapter(
                URL.class, new IOAdapters._String<URL>() {

                    public URL decode(String v, boolean isImmutable) throws Exception {
                        return new URL(v);
                    }

                    public String encode(URL v) throws Exception {
                        return v.toString();
                    }
                });

        public final IOAdapter<TimeZone, ?> timeZoneAdapter = IOAdapterLoader.registerDefaultAdapter(
                TimeZone.class, new IOAdapters._String<TimeZone>() {

                    @Override
                    public TimeZone decode(String v, boolean isImmutable) throws Exception {
                        TimeZone tz = TimeZone.getTimeZone(v);
                        return tz;
                    }

                    @Override
                    public String encode(TimeZone v) throws Exception {
                        String tz = v.getID();
                        return tz;
                    }
                });

        public final IOAdapter<DateTime, ?> jodaDateTimeAdapter = IOAdapterLoader.registerDefaultAdapter(
                DateTime.class, new IOAdapters._Long<DateTime>() {

                    @Override
                    public DateTime decode(long v, boolean isImmutable) throws Exception {
                        return new DateTime(v);
                    }

                    @Override
                    public long encode(DateTime v) throws Exception {
                        return v.getMillis();
                    }

                });

        public final IOAdapter<LocalDate, ?> jodaLocalDateAdapter = IOAdapterLoader.registerDefaultAdapter(
                LocalDate.class, new IOAdapters._Long<LocalDate>() {

                    @Override
                    public LocalDate decode(long v, boolean isImmutable) throws Exception {
                        return new LocalDate(v, DateTimeZone.UTC);
                    }

                    @Override
                    public long encode(LocalDate v) throws Exception {
                        return JodaTimeHack.getLocalMillis(v);
                    }
                });

        public final IOAdapter<LocalTime, ?> jodaLocalTimeAdapter = IOAdapterLoader.registerDefaultAdapter(
                LocalTime.class, new IOAdapters._Long<LocalTime>() {

                    @Override
                    public LocalTime decode(long v, boolean isImmutable) throws Exception {
                        return new LocalTime(v, DateTimeZone.UTC);
                    }

                    @Override
                    public long encode(LocalTime v) throws Exception {
                        return JodaTimeHack.getLocalMillis(v);
                    }
                });

        public final IOAdapter<LocalDateTime, ?> jodaLocalDateTimeAdapter = IOAdapterLoader.registerDefaultAdapter(
                LocalDateTime.class, new IOAdapters._Long<LocalDateTime>() {

                    @Override
                    public LocalDateTime decode(long v, boolean isImmutable) throws Exception {
                        return new LocalDateTime(v, DateTimeZone.UTC);
                    }

                    @Override
                    public long encode(LocalDateTime v) throws Exception {
                        return JodaTimeHack.getLocalMillis(v);
                    }
                });

        public final IOAdapter<Duration, ?> jodaDurationAdapter = IOAdapterLoader.registerDefaultAdapter(
                Duration.class, new IOAdapters._Long<Duration>() {

                    @Override
                    public Duration decode(long v, boolean isImmutable) throws Exception {
                        return new Duration(v);
                    }

                    @Override
                    public long encode(Duration v) throws Exception {
                        return v.getMillis();
                    }
                });

    }
}
