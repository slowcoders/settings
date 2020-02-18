package org.slowcoders.serialize.sample;

import com.google.common.collect.ImmutableList;
import org.slowcoders.io.settings.*;

import java.util.List;

import androidx.preference.PreferenceCategory;

public class AndroidSettings extends SettingModule {

    private AndroidSettings(SettingStorage storage, String name) {
        super(storage, name);
    }

    public static AndroidSettings pref = new AndroidSettings(JsonSettingsStore.instance, "Settings");

    public final AtomicOption<Boolean> checkBoxPreference = new _Option<>(false);

    public final AtomicOption<String> editTextPreference = new _Option<>(null);

    public final AtomicOption<String> listPreference = new _Option<>(null);

    public final AtomicOption<Boolean> switchPreference = new _Option<>(false);

    public final AtomicOption<Person> dialogPreference = new _Option<>(null);

    public final AtomicOption<String> dropDownPreference = new _Option<>(null);

    public final AtomicSet<String> multiSelectListPreference = new _Set<>(null);

    public final PreferenceCategory preferenceCategory = new PreferenceCategory(this, "preferenceCategory");

    public final ObservablePreference observablePreference = new ObservablePreference(this, "observablePreference");

    public final ObservableCategory observableCategory = new ObservableCategory(this, "observableCategory");


    public static final class PreferenceCategory extends SettingProperties {

        private PreferenceCategory(SettingProperties parent, String name) {
            super(parent, name);
        }

        public final AtomicOption<Boolean> checkBoxPreference = new _Option<>(false);

        public final AtomicOption<String> editTextPreference = new _Option<>(null);

        public final SubCategory subCategory = new SubCategory(this, "subCategory");

        public static class SubCategory extends SettingProperties {

            private SubCategory(SettingProperties parent, String name) {
                super(parent, name);
            }

            public final AtomicOption<Boolean> checkBoxPreference = new _Option<>(false);
        }
    }

    public static final class ObservablePreference extends SettingProperties {

        private ObservablePreference(SettingProperties parent, String name) {
            super(parent, name);
        }

        public final AtomicOption<Boolean> observableSwitch = new _Option<>(false);

        public final AtomicOption<String> dependentEditText = new _Option<>(null);
    }

    public static final class ObservableCategory extends SettingProperties {

        private ObservableCategory(SettingProperties parent, String name) {
            super(parent, name);
        }

        public final AtomicOption<Boolean> checkBoxPreference = new _Option<>(false);

        public final AtomicOption<Boolean> switchPreference = new _Option<>(false);

        public final AtomicOption<String> listPreference = new _Option<>(null);
    }

    public final AtomicOption<Boolean> dependentSwitch = new _Option<>(false);

    public static class Person {
        private String name;
        private int age;
        private List<String> emailAddresses;
        private Dog dog;

        private Person() {}

        public String getName() {
            return name;
        }

        public List<String> getEmailAddresses() {
            return emailAddresses;
        }

        public int getAge() {
            return age;
        }


        public Dog getDog() {
            return dog;
        }

        public static class Builder {

            private Person instance;

            public Builder() {
                instance = new Person();
            }

            public Builder(Person person) {
                this();
                if (person != null) {
                    instance.age = person.age;
                    instance.dog = person.dog;
                    instance.name = person.name;
                    instance.emailAddresses = person.emailAddresses;
                }
            }

            public void setAge(int age) {
                this.instance.age = age;
            }

            public void setName(String name) {
                this.instance.name = name;
            }

            public void setEmailAddresses(List<String> emailAddresses) {
                this.instance.emailAddresses = emailAddresses;
            }

            public void setDog(Dog dog) {
                this.instance.dog = dog;
            }

            public Person build() {
                return instance;
            }
        }

        public static class Dog {
            private String name;
            private int age;

            private Dog() {}

            public String getName() {
                return name;
            }

            public int getAge() {
                return age;
            }

            public static class Builder {

                private Dog instance;

                public Builder() {
                    instance = new Dog();
                }

                public Builder(Dog dog) {
                    this();
                    if (dog != null) {
                        instance.name = dog.name;
                        instance.age = dog.age;
                    }
                }

                public void setName(String name) {
                    this.instance.name = name;
                }

                public void setAge(int age) {
                    this.instance.age = age;
                }

                public Dog build() {
                    return instance;
                }

            }
        }
    }
}
