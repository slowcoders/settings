Settings suggests a object-oriented way to handle data that need to be stored in a file.
With apis Settings provides, we can write and load data through simple method calls 
and these operations are all done internally so users do not have to care about complex details such as file IO.

We will look into details how Settings can be utilized through a sample.

Note) This project includes submodules so use --recursive option to clone full package
```text
    git clone --recursive https://github.com/slowcoders/settings.git
```

### Gradle
To use Settings, in build gradle, targetPlatform has to be declared.
In a java application, add this to your build.gradle file
```groovy
    ext {
        targetPlatform = 'java'
    }
```

In an android applcation, add this to your build.gradle file.
```groovy
    ext {
        targetPlatform = 'android'
    }
```


# Sample 
In a sample, we suppose that we are making an application that needs to store account and ui-related settings to a file.
We will make a class named "ApplicationSettings" and put all setting properties in it.
Files will be created in a structure :

![sample](sample/Sample1.png)


### SettingModule
```java
public class ApplicationSettings {
    // declare root
    private static SettingModule root = new SettingModule(JsonSettingsStore.instance, "application.settings");
}
```

SettingModule is a class with directory information. 
First augment taken in constructor is DataStore which decides in what format data will be stored.
In the example, we passed JsonSettingStore so that data is saved in json format. Second argument is a String which is a
name of SettingModule. A folder named after this String will be created and subordinate setting nodes declared inside
SettingModule will be created in the folder. 

### SettingProperties
SettingProperties is a group of setting properties. In this class, we can declared AtomicOption/List/Map/Set which
store a datum.

AccountSettings is a class that extends SettingProperties and 
some account-related options are declared inside.
```java
    public static class AccountSettings extends SettingProperties {

        private AccountSettings(SettingProperties parent) {
            super(parent);
        }

        public final AtomicOption<String> emailAddress = new _Option<>(null);

        public final AtomicOption<String> name = new _Option<>(null);

        public final AtomicOption<String> password = new _Option<>(null);

        public final AtomicOption<DateTime> registeredTime = new _Option<>(null);

        public final ServerSetting serverSetting = new ServerSetting(this);

        public static class ServerSetting extends SettingProperties {

            private ServerSetting(SettingProperties parent) {
                super(parent);
            }

            public final AtomicOption<String> serverName = new _Option<>(null);

            public final AtomicOption<String> serverUrl = new _Option<>(null);
        }
    }
```
Each setting property can be declared through AtomicOption/List/Map/Set classes. We can determine type by generic
and value that is passed to constructor is default value. When setting file is first created, default value will be saved.

We can add declared SettingProperties to SettingModule.
SettingProperties takes another SettingProperties as an argument in its constructor and this will be parent node.
```java
    public class ApplicationSettings {
    
        private static SettingModule root = new SettingModule(JsonSettingsStore.instance, "application.settings");
        
        public static final AccountSettings account = new AccountSettings(root); // SettingModule 을 부모로 설정
    
    }
```

It is possible to add another SettingProperties to SettingProperties as subordinate node. Inside AccountSettings class,
there is a declared ServerSetting which extends SettingProperties and it is initialized with its parent being AccountSettings. 
In this case, ServerSetting file will be created in a directory of AccountSetting.

### AtomicOption
Now we can use setting properties like below.
```java
        String emailAddress = ApplicationSettings.account.emailAddress.get();
        String password = ApplicationSettings.account.password.get();
        
        Application.account.emailAddress.set("slowcoder@ggg.com");
```
Through get(), set() methods we can load and write data. File IO will be executed automatically. 

### Object persistence
Settings can save all types of data. 
In the example, we added UiSetting class to save ui-related settings.
```java
    public static class UiSettings extends SettingProperties {

        private UiSettings(SettingProperties parent) {
            super(parent);
        }

        public enum Theme {
            Dark,
            White
        }

        public final AtomicOption<Theme> theme = new _Option<>(Theme.Dark);

        public final AtomicOption<Integer> appColor = new _Option<>(0x0);

        public final AtomicOption<Boolean> showIcon = new _Option<>(true);

        public final AtomicOption<NotificationOption> notification = new _Option<>(null);

        public static class NotificationOption {
            private String ringtone;
            private String light;
            private boolean soundEnabled;

            public String getRingtone() {
                return ringtone;
            }

            public void setRingtone(String ringtone) {
                this.ringtone = ringtone;
            }

            public String getLight() {
                return light;
            }

            public void setLight(String light) {
                this.light = light;
            }

            public boolean isSoundEnabled() {
                return soundEnabled;
            }

            public void setSoundEnabled(boolean soundEnabled) {
                this.soundEnabled = soundEnabled;
            }
        }
    }
```
This class contains a AtomicOption whose type is notificationOption which defines notification settings.
This setting can be used in a same way with all other settings.
```java
        NotificationOption notiOption = ApplicationSettings.ui.notification.get();
        
        NotificationOption noti = new NotificationOption();
        noti.setRingtone("ringtone1");
        noti.setLight("light");
        noti.setEnableSound(true);
        
        ApplicationSettings.ui.notification.set(noti);
```

### Custom adapter
Sometimes it is needed to specify a way object is serialized. In this case, we can register adapter that we created.
There is a class named IOAdapterLoader which takes charges of resolving adapter and there is a static method
registerDefaultAdapter() inside IOAdapterLoader. Through this method we can specify adapters for certain types of object.

### Observer
To all Atomic instances and SettingProperties instances, we can register observer and we can take 
notifications whenever these get changed.
```java
        ApplicationSettings.ui.appColor.addAsyncObserver(newValue -> {
            // do something
        });

```
We added observer to appColor so that we get notified whenever appColor is changed.
```java
        ApplicationSettings.ui.addAsyncObserver(properties -> {
            // do something
        });

```
In the example above, we registered observer to a SettingProperties. Once we added observer to SettingProperties,
whenever one of its subordinate nodes changes, we get notified.



