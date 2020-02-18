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


public class SettingsTest {
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

		// for Weak-Observer.
		ObservableOption.Observer<ThemeType> themeObserver;
		SettingProperties.Observer categoryObserver;
		ChangeType lastChangeType;

		@Before
		public void setUp() {
			/**
			 * reset values of option and
			 * clear pending Async-notifications
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
			 * clear WeakObservers from the last test
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
		 * directly change value of option
		 * when options is changed notifications have to be sent
		 */
		@Test
		public void directChange() throws Exception {
			
			config.ui.theme.set(config.ui.theme.get());
			NPAsyncScheduler.executePendingTasks();
			// when setting same value,
			// update does not occur
			assertEquals(cntThemeChangedNoti, 0);
			
			config.ui.theme.set(ThemeType.PINK);
			NPAsyncScheduler.executePendingTasks();
			assertEquals(ThemeType.PINK, notiResult_theme);

			config.ui.theme.set(ThemeType.DARK);
			NPAsyncScheduler.executePendingTasks();

			assertEquals(ThemeType.DARK, notiResult_theme);
			// properties notification has to be sent as well
			// when option is changed
			assertEquals(2, cntUiCategoryChangedNoti);
			
		}
		
		/**
		 * change option with Editor.
		 * After commit, option has to be updated
		 * and notifications have to be delivered
		 */
		@Test
		public void updateWithEditor() throws Exception {

			SettingProperties.Editor tr = config.edit();
			// change with key
			assertEquals(config.ui.theme.getDefaultValue(), config.ui.theme.get());
			tr.setProperty(config.getOptionKey(config.ui.theme), ThemeType.WHITE);
			tr.setProperty(config.ui.theme, ThemeType.DARK);

			// until commit, there have to be no change
			assertNull(notiResult_theme);
			assertEquals(cntThemeChangedNoti, 0);
			assertEquals(cntUiCategoryChangedNoti, 0);
			tr.commit();

			// notification has to be delivered after commit
			NPAsyncScheduler.executePendingTasks();
			assertEquals(ThemeType.DARK, notiResult_theme);
			assertEquals(1, cntThemeChangedNoti);
			assertEquals(1, cntUiCategoryChangedNoti);

			// after notification for option is delivered,
			// notification for category has to be delivered
			assertEquals(1, cntThemeChangedNoti_beforeUiCategoryChangedNoti);
		}

		/**
		 * Reflection Field Test  
		 */
		@Test
		public void AccessEntityFieldOption() throws Exception {

	    	AccountSettings account = TestSettings.accountSettings(0);

	    	SettingProperties.Editor tr = account.edit();
	    	String prefix = account.getOptionKey(account.serverProperties) + ":";
			tr.setProperty(prefix + "email", "test@email.com");
			tr.setProperty(prefix + "password", "Password-1234");

			// there has to be no change until commit
			tr.commit();

			// notification has to be delivered after commit
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

	public static class Test2 {

		private TestSettings.IOSettings.TestC obj1 = new TestSettings.IOSettings.TestC.Builder()
				.setCompany("slowcoders1")
				.setName("coder1")
				.build();

		private TestSettings.IOSettings.TestC obj2 = new TestSettings.IOSettings.TestC.Builder()
				.setCompany("slowcoders2")
				.setName("coder2")
				.build();

		private TestSettings.IOSettings.TestC obj3 = new TestSettings.IOSettings.TestC.Builder()
				.setCompany("slowcoders3")
				.setName("coder3")
				.build();

		private TestSettings.IOSettings.TestC obj4 = new TestSettings.IOSettings.TestC.Builder()
				.setCompany("slowcoders4")
				.setName("coder4")
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
			set.add("coder");
			set.add("slowcoders");
			TestSettings.settings.ioSettings.testSet.set(set);

			TestSettings.settings.exportTo(JsonSettingsStore.instance, "test.settings-3");

			TestSettings settings = new TestSettings(JsonSettingsStore.instance, "test.settings-3");
			ImmutableSet<String> result = settings.ioSettings.testSet.get();

			Assert.assertTrue(result.contains("coder"));
			Assert.assertTrue(result.contains("slowcoders"));
		}

		@Test
		public void observableList_Map() {
			List<String> testList = new LinkedList<>();
			testList.add("slow");
			testList.add("coder");
			testList.add("ggg");
			testList.add("haha");

			Map<String, String> testMap = new HashMap<>();
			testMap.put("name", "coder.lee");
			testMap.put("age", "27");
			testMap.put("company", "slowcoders");

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
		 *  Test if weakObserver is removed after gc
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

			// set null so that observer is no more referenced
			strongObserver = null;
			weakObserver = null;

			// GarbageCollector
			System.gc();

			TestSettings.settings.ui.theme.set(ThemeType.WHITE);

			NPAsyncScheduler.executePendingTasks();
			// weak-referenced observer does not do anything
			assertEquals(1, strongNotiCnt.get());
			assertEquals(0, weakNotiCnt.get());
		}

		@Test
		public void asyncUiTask(){
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
