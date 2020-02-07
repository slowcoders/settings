package test.storm.settings;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestSuite;
import com.google.common.collect.ImmutableList;
import org.slowcoders.io.settings.AtomicOption;
import org.slowcoders.io.settings.JsonSettingsStore;
import org.slowcoders.io.settings.ObservableOption;
import org.slowcoders.io.settings.SettingProperties;
import org.slowcoders.io.util.NPAsyncScheduler;
import org.slowcoders.observable.ObservableData;
import org.slowcoders.observable.ObservableCollection;
import org.slowcoders.observable.ObservableList;
import org.slowcoders.observable.ChangeType;
import test.storm.settings.TestSettings.AccountSettings;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;


public class SettingsTest1 {
	static TestSettings config = TestSettings.settings;

	@Test
	public static TestSuite suite() {
		TestSuite suite = new TestSuite();
		suite.addTest(new junit.framework.JUnit4TestAdapter(Test1.class));
		suite.addTest(new junit.framework.JUnit4TestAdapter(Test2.class));
		return suite;
	}

	
	
	public static class Test1 {
	    int cntThemeChangedNoti;
		int cntUiCategoryChangedNoti;
		int cntThemeChangedNoti_beforeUiCategoryChangedNoti;

		ThemeType notiResult_theme;

		// Weak-Observer 보관용 필드.
		ObservableOption.Observer<ThemeType> themeObserver;
		SettingProperties.Observer categoryObserver;
		ChangeType lastChangeType;

		@Before
		public void setUp() {
			/**
			 * Option 값을 reset 하고, pending Async-notification 을 clear
			 */
			config.ui.resetAllOptions();
			NPAsyncScheduler.executePendingTasks();


			this.cntThemeChangedNoti = 0;
			this.cntUiCategoryChangedNoti = 0;
			this.cntThemeChangedNoti_beforeUiCategoryChangedNoti = -1;

			this.themeObserver = config.ui.theme.addWeakAsyncObserver(new ObservableOption.Observer<ThemeType>() {
				@Override
				public void onPropertyChanged(ThemeType newValue) {
					System.out.println("Value : " + newValue);
					notiResult_theme = newValue;
					cntThemeChangedNoti++;
				}
			});


			this.categoryObserver = config.ui.addWeakAsyncObserver((property) -> {
					cntUiCategoryChangedNoti ++;
					cntThemeChangedNoti_beforeUiCategoryChangedNoti = cntThemeChangedNoti;
			});

			/**
			 * 지난 Test에 사용된 WeakObserver 를 clear 한다.
			 */
			System.gc();
			
		}


		@Test
		public void testVolatileList() {
			ObservableList<Object> list = new ObservableList<Object>(ArrayList.class);
			list.addAsyncObserver(new ObservableData.Observer<ObservableCollection<Object>, ChangeType>() {
				@Override
				public void onChanged(ObservableCollection<Object> property, ChangeType data) {
					lastChangeType = data;
				}
			});
			list.add("Hello");
			NPAsyncScheduler.executePendingTasks();
			Assert.assertEquals(ChangeType.Create, lastChangeType);
		}

		/**
		 * Option 값의 직접적인 변경.
		 * Option 값 변경과 동시에 SharedOption.Observer와 SharedCategory.Observer에 Notification 전달.
		 * 
		 * @throws Exception
		 */
		@Test
		public void directChange() throws Exception {
			
			config.ui.theme.set(config.ui.theme.get());
			NPAsyncScheduler.executePendingTasks();
			// 동일값인 경우, Noti 전달하지 않음.
			assertEquals(cntThemeChangedNoti, 0);
			
			config.ui.theme.set(ThemeType.PINK);
			NPAsyncScheduler.executePendingTasks();
			assertEquals(ThemeType.PINK, notiResult_theme);

			config.ui.theme.set(ThemeType.DARK);
			NPAsyncScheduler.executePendingTasks();

			assertEquals(ThemeType.DARK, notiResult_theme);
			// Option noti 시마다 category noti 도 전달.
			assertEquals(2, cntUiCategoryChangedNoti);
			
		}
		
		/**
		 * Editor 를 이용한 Option 값 변경.
		 * Editor.commit 호출 이후에 실제 Option 값이 변경되고,
		 * Option 값 변경과 동시에 SharedOption.Observer와 SharedCategory.Observer에 Notification 전달.
		 * 
		 * @throws Exception
		 */
		@Test
		public void updateWithEditor() throws Exception {

			SettingProperties.Editor tr = config.edit();
			// key 문자열을 이용한 변경.
			assertEquals(config.ui.theme.getDefaultValue(), config.ui.theme.get());
			tr.setProperty(config.getOptionKey(config.ui.theme), ThemeType.WHITE);
			tr.setProperty(config.ui.theme, ThemeType.DARK);

			// commit 전까지는 내용변경 없음. 
			assertEquals(null, notiResult_theme);
			assertEquals(cntThemeChangedNoti, 0);
			assertEquals(cntUiCategoryChangedNoti, 0);
			tr.commit();

			// commit 후에 Noti 전달.
			NPAsyncScheduler.executePendingTasks();
			assertEquals(ThemeType.DARK, notiResult_theme);
			assertEquals(1, cntThemeChangedNoti);
			assertEquals(1, cntUiCategoryChangedNoti);
			
			// Option 변경 Noti 전달 후에, Category 변경 Noti 전달.
			assertEquals(1, cntThemeChangedNoti_beforeUiCategoryChangedNoti);
		}

		/**
		 * Reflection Field Test  
		 * 
		 * @throws Exception
		 */
		@Test
		public void AccessEntityFieldOption() throws Exception {

	    	AccountSettings account = TestSettings.accountSettings(0);

	    	SettingProperties.Editor tr = account.edit();
	    	String prefix = account.getOptionKey(account.serverProperties) + ":";
			tr.setProperty(prefix + "email", "test@email.com");
			tr.setProperty(prefix + "password", "Password-1234");

			// commit 전까지는 내용변경 없음. 
			tr.commit();

			// commit 후에 Noti 전달. 
			assertEquals(account.serverProperties.get().getEmail(), "test@email.com");
			assertEquals(account.serverProperties.get().getPassword(), "Password-1234");
			
		}



	    @Test
	    public void createACMSettingsWithJson() throws Exception {
	    	TestSettings.settings.ui.theme.set(ThemeType.DARK);
	    	TestSettings.settings.ui.filterEnable.set(true);
	    	TestSettings.settings.ui.filterOption.set(123);

			TestSettings.settings.exportTo(JsonSettingsStore.instance, "test.settings-2");

			TestSettings settings2 = new TestSettings(JsonSettingsStore.instance, "test.settings-2");
	        assertEquals(settings2.ui.theme.get(), ThemeType.DARK);
	        assertEquals(settings2.ui.filterEnable.get(), true);
	        assertEquals(settings2.ui.filterOption.get().intValue(), 123);
	    }
		

	    @Test
	    public void encryptionTest() throws Exception {
	    	TestSettings.settings.ui.theme.set(ThemeType.DARK);
			assertEquals(TestSettings.settings.ui.theme.get(), ThemeType.DARK);
	    	TestSettings.settings.ui.filterEnable.set(true);
	    	TestSettings.settings.ui.filterOption.set(123);

			assertEquals(TestSettings.settings.ui.theme.get(), ThemeType.DARK);
			TestSettings.settings.exportTo(JsonSettingsStore.instance, "test.settings-2");

			TestSettings settings2 = new TestSettings(JsonSettingsStore.instance, "test.settings-2");
	        assertEquals(settings2.ui.theme.get(), ThemeType.DARK);
	        assertEquals(settings2.ui.filterEnable.get(), true);
	        assertEquals(settings2.ui.filterOption.get().intValue(), 123);
	    }

	}

	// JJ
	public static class Test2 {

		private TestSettings.IOSettings.TestC obj1 = new TestSettings.IOSettings.TestC.Builder()
				.setCompany("nineFolders1")
				.setName("jonghoon1")
				.build();

		private TestSettings.IOSettings.TestC obj2 = new TestSettings.IOSettings.TestC.Builder()
				.setCompany("nineFolders2")
				.setName("jonghoon2")
				.build();

		private TestSettings.IOSettings.TestC obj3 = new TestSettings.IOSettings.TestC.Builder()
				.setCompany("nineFolders3")
				.setName("jonghoon3")
				.build();

		private TestSettings.IOSettings.TestC obj4 = new TestSettings.IOSettings.TestC.Builder()
				.setCompany("nineFolders4")
				.setName("jonghoon4")
				.build();


		@Test
		public void objectList() throws Exception {
			ArrayList<TestSettings.IOSettings.TestC> list = Lists.newArrayList(obj1, obj2, obj3, obj4);
			TestSettings.settings.ioSettings.objectList.set(list);
			TestSettings.settings.exportTo(JsonSettingsStore.instance, "test.settings-3");

			TestSettings settings = new TestSettings(JsonSettingsStore.instance, "test.settings-3");
			ImmutableList<TestSettings.IOSettings.TestC> results = settings.ioSettings.objectList.get();

			for (int i = 0, count = list.size(); i < count; i++) {
				TestSettings.IOSettings.TestC result = results.get(i);
				TestSettings.IOSettings.TestC obj = list.get(i);

				Assert.assertEquals(obj.getName(), result.getName());
				Assert.assertEquals(obj.getCompany(), result.getCompany());
			}
		}

		@Test
		public void objectMap() throws Exception {
			HashMap<String, TestSettings.IOSettings.TestC> map = new HashMap<>();
			map.put("obj1", obj1);
			map.put("obj2", obj2);
			map.put("obj3", obj3);
			map.put("obj4", obj4);

			TestSettings.settings.ioSettings.objectMap.set(map);
			TestSettings.settings.exportTo(JsonSettingsStore.instance, "test.settings-3");

			TestSettings settings = new TestSettings(JsonSettingsStore.instance, "test.settings-3");
			ImmutableMap<String, TestSettings.IOSettings.TestC> results = settings.ioSettings.objectMap.get();

			for (int i = 1, count = map.size(); i <= count; i++) {
				TestSettings.IOSettings.TestC result = results.get("obj" + i);
				TestSettings.IOSettings.TestC obj = map.get("obj" + i);

				Assert.assertEquals(obj.getName(), result.getName());
				Assert.assertEquals(obj.getCompany(), result.getCompany());
			}
		}

		@Test
		public void objectSet() throws Exception {
			HashSet<TestSettings.IOSettings.TestC> set = new HashSet<>();
			set.add(obj1);
			set.add(obj2);
			set.add(obj3);
			set.add(obj4);

			TestSettings.settings.ioSettings.objectSet.set(set);
			TestSettings.settings.exportTo(JsonSettingsStore.instance, "test.settings-3");

			TestSettings settings = new TestSettings(JsonSettingsStore.instance, "test.settings-3");
			ImmutableSet<TestSettings.IOSettings.TestC> results = settings.ioSettings.objectSet.get();

			ImmutableList<TestSettings.IOSettings.TestC> list = results.asList();
			for (int i = 0, count = list.size(); i < count; i++) {
				TestSettings.IOSettings.TestC result = list.get(i);
				TestSettings.IOSettings.TestC obj = list.get(i);

				Assert.assertEquals(obj.getName(), result.getName());
				Assert.assertEquals(obj.getCompany(), result.getCompany());
			}
		}

		@Test
		public void atomicSet() throws Exception {
			HashSet<String> set = new HashSet<>();
			set.add("jonghoon");
			set.add("ninefolders");
			TestSettings.settings.ioSettings.testSet.set(set);

			TestSettings.settings.exportTo(JsonSettingsStore.instance, "test.settings-3");

			TestSettings settings = new TestSettings(JsonSettingsStore.instance, "test.settings-3");
			ImmutableSet<String> result = settings.ioSettings.testSet.get();
			ImmutableList<String> list = result.asList();

			Assert.assertEquals("jonghoon", list.get(0));
			Assert.assertEquals("ninefolders", list.get(1));
		}

		@Test
		public void observableList_Map() {
			List<String> testList = new LinkedList<>();
			testList.add("lee");
			testList.add("jong");
			testList.add("hoon");
			testList.add("haha");

			Map<String, String> testMap = new HashMap<>();
			testMap.put("name", "jonghoon.lee");
			testMap.put("age", "27");
			testMap.put("company", "ninefolders");

			TestSettings.settings.ioSettings.testList.set(testList);
			TestSettings.settings.ioSettings.testMap.set(testMap);
			//TestSettings.settings.writeJSON("test.settings");

			ImmutableList<String> list = TestSettings.settings.ioSettings.testList.get();
//			Map<String, String> map = TestSettings.settings.testMap.get();

			for (int i = 0; i < testList.size(); i++){
				assertEquals(testList.get(i), list.get(i));
			}
		}

		@Test
		public void removeObserver() throws Exception {
			AtomicInteger notiCnt = new AtomicInteger();

			TestSettings.settings.ui.theme.set(ThemeType.PINK);
			ThemeType theme = TestSettings.settings.ui.theme.get();

			assertEquals(ThemeType.PINK, theme);

			AtomicOption.Observer<ThemeType> observer = new AtomicOption.Observer<ThemeType>() {
				@Override
				public void onPropertyChanged(ThemeType property) {
					notiCnt.incrementAndGet();
				}
			};

			TestSettings.settings.ui.theme.addAsyncObserver(observer);

			TestSettings.settings.ui.theme.set(ThemeType.DARK);
			TestSettings.settings.ui.theme.set(ThemeType.WHITE);

			NPAsyncScheduler.executePendingTasks();
			assertEquals(2, notiCnt.get());

			TestSettings.settings.ui.theme.removeObserver(observer);
			TestSettings.settings.ui.theme.set(ThemeType.PINK);

			NPAsyncScheduler.executePendingTasks();

			assertNotEquals(3, notiCnt.get());
		}

		/*
		 * 	WeakObserver 로 추가된 observer가 gc 이후에 존재하는지 확인하는 테스트
		 * */
		@Test
		public void weaklyReferencedObserver() throws Exception {
			AtomicInteger strongNotiCnt = new AtomicInteger(0);
			AtomicInteger weakNotiCnt = new AtomicInteger(0);

			TestSettings.settings.ui.theme.set(ThemeType.DARK);
			AtomicOption.Observer<ThemeType> strongObserver = new AtomicOption.Observer<ThemeType>() {
				@Override
				public void onPropertyChanged(ThemeType property) {
					strongNotiCnt.incrementAndGet();
				}
			};
			AtomicOption.Observer<ThemeType> weakObserver = new AtomicOption.Observer<ThemeType>() {
				@Override
				public void onPropertyChanged(ThemeType property) {
					weakNotiCnt.incrementAndGet();
				}
			};

			TestSettings.settings.ui.theme.addWeakAsyncObserver(weakObserver);
			TestSettings.settings.ui.theme.addAsyncObserver(strongObserver);

			// observer 를 참조하는 곳이 없도록 null 값 설정
			strongObserver = null;
			weakObserver = null;

			// GarbageCollector 발동
			System.gc();

			TestSettings.settings.ui.theme.set(ThemeType.WHITE);

			NPAsyncScheduler.executePendingTasks();
			// weakReference로 연결된 observer는 발동되지 않음
			assertEquals(1, strongNotiCnt.get());
			assertEquals(0, weakNotiCnt.get());
		}

		@Test
		public void asyncUiTask(){
			// TODO: initScheduler 크로스 플랫폼 구현

			TestSettings.settings.ui.theme.addAsyncObserver(new AtomicOption.Observer<ThemeType>() {
				@Override
				public void onPropertyChanged(ThemeType property) {
					System.out.println(property);
				}
			});


			TestSettings.settings.ui.theme.set(ThemeType.DARK);
			TestSettings.settings.ui.theme.set(ThemeType.WHITE);
		}

	}

}
