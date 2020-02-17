Serialize 는 데이터 직렬화를 위한 모듈이며 어플리케이션 내에서 파일로 기록되는 데이터들를 객체 지향적으로 다룰 수 있도록
설계되어 있습니다. Serialize 에서 제공하는 api 들로 데이터를 관리하면 간단한 메서드 호출로 데이터를 파일에 기록하고 불러올 수 있으며
이 동작들은 모두 밑단에서 자동적으로 처리되기 때문에 사용자는 파일 IO 관련된 부분에 신경을 쓰지 않아도 됩니다.

아래에서 우리는 간단한 샘플을 통해 Serialize 를 어떻게 활용할 수 있는지 알아봅니다.

# Sample 
샘플에서 우리는 계정 및 ui 와 관련된 정보를 파일로 기록해야하는 상황을 가정합니다. 
어플리케이션의 설정값들을 관리할 ApplicationSettings 클래스를 만들고 설정값들은 모두 이 클래스 내에 선언하도록 하겠습니다.
결과적으로 우리가 선언된 설정값들은 다음과 같은 디렉토리 구조로 생성될 겁니다.

![sample](Sample1.png)

### SettingModule
```java
public class ApplicationSettings {
    // declare root
    private static SettingModule root = new SettingModule(JsonSettingsStore.instance, "application.settings");
}
```

SettingModule 은 디렉토리 정보를 갖고 있는 클래스입니다. 생성자의 첫번째 인자인 DataStore 는 SettingModule 밑의 데이터가 어떤 형식으로 저장될지 결정하는데
위의 예제에서는 JsonSettingStore 의 객체를 인자로 넘겨 json 파일 형식으로 데이터를 저장하도록 선언하고 있습니다. 두 번째 인자인 문자열은
SettingModule 의 이름입니다. 이 이름으로 폴더가 생성되며 해당 SettingModule 밑에 선언된 하위 노드들은 모두 지정된 디렉토리 밑에 생성됩니다.

### SettingProperties
SettingProperties 는 일종의 Setting 값들의 집합입니다. 이 클래스 내에는 단일 설정값을 저장하는 AtomicOption/List/Map/Set 이나
또 다른 SettingProperties 를 선언할 수 있습니다.

아래는 AccountSettings 클래스이며 계정과 관계된 설정들이 선언되어 있습니다.
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
각각의 설정값들은 AtomicOption/List/Map/Set 클래스를 통해 선언할 수 있습니다. 제네릭으로 값의 타입을 설정할 수 있으며
생성자에 넘겨주는 값은 기본값입니다. 맨 처음 설정이 파일에 기록될 때 설정된 기본값으로 값이 세팅됩니다.

이렇게 선언된 SettingProperties 는 다음과 같이 SettingModule 에 추가할 수 있습니다.
아래와 같이 SettingModule 을 부모로 설정해 SettingProperties 를 초기화하면 SettingModule 디렉토리 밑에 설정값들이 저장됩니다.
```java
    public class ApplicationSettings {
    
        private static SettingModule root = new SettingModule(JsonSettingsStore.instance, "application.settings");
        
        public static final AccountSettings account = new AccountSettings(root); // SettingModule 을 부모로 설정
    
    }
```

SettingProperties 에 하위 SettingProperties 를 추가하는 것도 가능합니다. AccountSettings 클래스 내부를 보면 이너 클래스로 ServerSetting
를 선언하고 AccountSettings 를 부모로 인자를 전달해 초기화하고 있습니다. 이 경우 ServerSetting 를 저장하는 파일은 AccountSetting 의 하위 디렉토리에 
생성됩니다.


### AtomicOption
이제 우리가 정의한 설정값들을 다음과 같이 사용할 수 있습니다.

```java
        String emailAddress = ApplicationSettings.account.emailAddress.get();
        String password = ApplicationSettings.account.password.get();
        
        Application.account.emailAddress.set("slowcoder@ggg.com");
```
위와 같이 get, set 함수들을 통해 설정값을 읽고 쓸 수 있습니다. 파일에서 실제로 데이터를 읽고 새로운 값을 저장하는 동작은 모두 Serialize 내부에서 처리됩니다.

### Object persistence
Serialize 는 모든 객체를 저장할 수 있습니다. Ui 관련된 설정값들을 저장하기 위해서 UiSetting 클래스를 추가했습니다. 
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
이 클래스는 노티피케이션과 관련된 설정을 포함하고 있으며 해당 설정은 NotificationOption 클래스를 타입으로 갖고 있습니다.
사용자가 정의한 클래스를 활용할 때에도 내부에서 자동으로 직렬화가 이루어지기 때문에 사용방법은 똑같습니다.
```java
        NotificationOption notiOption = ApplicationSettings.ui.notification.get();
        
        NotificationOption noti = new NotificationOption();
        noti.setRingtone("ringtone1");
        noti.setLight("light");
        noti.setEnableSound(true);
        
        ApplicationSettings.ui.notification.set(noti);
```

### Custom adapter
때로는 우리가 특정한 방식으로 직렬화 및 역직렬화를 해야하는 타입이 있을 수 있습니다. 이런 경우에는 우리가 만든 adapter 를 등록하여 이 adapter 로
직렬화를 수행하도록 할 수 있습니다. 객체의 타입에 맞는 adapter 를 찾는 일을 수행하는 IOAdapterLoader 에는 registerDefaultAdapter() 라는 정적
메서드가 정의되어 있습니다. 이 함수를 이용하면 특정한 타입에 대해서는 우리가 만든 adapter 를 사용하도록 정할 수 있습니다.

### Observer
모든 Atomic 객체들과 SettingProperties 객체에는 observer 를 등록해 변경사항이 있을 때마다 노티피케이션을 받을 수 있습니다.

```java
        ApplicationSettings.ui.appColor.addAsyncObserver(newValue -> {
            // do something
        });

        ApplicationSettings.ui.addAsyncObserver(properties -> {
            // do something
        });

```
위 예제의 첫 번째 코드에서는 appColor 의 변경사항을 체크할 수 있도록 observer 를 등록해주고 있습니다. observer 가 등록된 후로는
appColor 가 변경될 때마다 observer 에 정의된 함수가 불리게 됩니다.

두 번째 코드에서는 SettingProperties 를 상속한 UiSetting 에 observer 를 등록해주고 있습니다. SettingProperties 에 observer 를
등록하면 SettingProperties 내의 Atomic 객체 중 하나라도 변경사항이 있으면 노티피케이션을 받을 수 있습니다. 




