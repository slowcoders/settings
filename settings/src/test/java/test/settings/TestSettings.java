package test.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slowcoders.io.settings.*;

public class TestSettings extends SettingModule {

    public static final TestSettings settings = new TestSettings(JsonSettingsStore.instance, "test.settings");

    public TestSettings(SettingStorage ds, String name) {
        super(ds, name);
        AccountSettings account_1 = new AccountSettings(this, "account-1.settings");
        AccountSettings account_2 = new AccountSettings(this, "account-2.settings");
        this.accounts.add(account_1);
        this.accounts.add(account_2);
    }

    private List<AccountSettings> accounts = new ArrayList<>();

    public static AccountSettings accountSettings(int idx) {
        return settings.accounts.get(idx);
    }

    public final UISettings ui = new UISettings(this);

    public final IOSettings ioSettings = new IOSettings(this);

    // Global Settings.
    public static class UISettings extends SettingProperties {
        UISettings(SettingProperties store) {
            super(store);
        }

        //@IOCtrl(key = "theme")
        public final AtomicOption<ThemeType> theme = new _Option(ThemeType.WHITE);
        //@IOCtrl(key = "number")
        public final AtomicOption<Integer> number = new _Option(null);
        //@IOCtrl(key = "filterEnable")
        public final AtomicOption<Boolean> filterEnable = new _Option(null);
        //@IOCtrl(key = "filterOption")
        public final AtomicOption<Integer> filterOption = new _Option(null);

    }

    // Multi-Instance Settings.
    public static class AccountSettings extends SettingProperties {
        public AccountSettings(SettingProperties ds, String name) {
            super(ds, name);
        }

        //@IOCtrl(key = "serverProperties")
        public final AtomicOption<ServerProperties> serverProperties = new _Option(new ServerProperties());

        //@IOCtrl(key = "name")
        public final AtomicOption<String> name = new _Option(null);

        //@IOCtrl(key = "email")
        public final AtomicOption<String> email = new _Option(null);

        //@IOCtrl(key = "photoType")
        public final AtomicOption<Integer> photoType = new _Option(null);

        //@IOCtrl(key = "defaultReplyAddress")
        public final AtomicOption<String> defaultReplyAddress = new _Option(null);

        public static class ServerProperties {

            private String email;

            private String password;

            private String domain;

            private String loginId;

            public final String getEmail() {
                return this.email;
            }

            public final String getPassword() {
                return this.password;
            }

            public final String getDomain() {
                return this.domain;
            }

            public final String getLoginId() {
                return this.loginId;
            };
        }

    }

    public static class IOSettings extends SettingProperties {

        public IOSettings(SettingProperties store) {
            super(store);
        }

        private static ArrayList<String> list = new ArrayList<>();
        private static HashMap<String, Integer> map = new HashMap<>();


        //@IOCtrl(key = "intValue")
        public final AtomicOption<Integer> intValue = new _Option(39856);
        //@IOCtrl(key = "stringValue")
        public final AtomicOption<String> stringValue = new _Option("haha");
        //@IOCtrl(key = "boolValue")
        public final AtomicOption<Boolean> boolValue = new _Option(false);
        //@IOCtrl(key = "byteValue")
        public final AtomicOption<Byte> byteValue = new _Option((byte) 23);
        //@IOCtrl(key = "shortValue")
        public final AtomicOption<Short> shortValue = new _Option((short) 456);
        //@IOCtrl(key = "longValue")
        public final AtomicOption<Long> longValue = new _Option((long) 728);
        //@IOCtrl(key = "floatValue")
        public final AtomicOption<Float> floatValue = new _Option(7833.23f);
        //@IOCtrl(key = "doubleValue")
        public final AtomicOption<Double> doubleValue = new _Option(156.895);


    //@IOCtrl(key = "testList")
        public final AtomicList<String> testList = new _List(null);
        //@IOCtrl(key = "testMap")
        public final AtomicMap<String, String> testMap = new _Map(null);

        public final AtomicSet<String> testSet = new _Set<>(null);

        public final AtomicList<TestC> objectList = new _List<>(null);

        public final AtomicMap<String, TestC> objectMap = new _Map<>(null);

        public final AtomicSet<TestC> objectSet = new _Set<>(null);

        static class TestC {

            private TestC() {}

            private String name;
            private String company;

            public String getName() {
                return this.name;
            }

            public String getCompany() {
                return this.company;
            }

            public static class Builder {
                private TestC instance = new TestC();

                public Builder setName(String name){
                    instance.name = name;
                    return this;
                }

                public Builder setCompany(String company) {
                    instance.company = company;
                    return this;
                }

                public TestC build() {
                    return this.instance;
                }
            }
        }

    }
}

