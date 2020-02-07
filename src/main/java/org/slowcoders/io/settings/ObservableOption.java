package org.slowcoders.io.settings;

import org.slowcoders.io.serialize.IOAdapter;
import org.slowcoders.io.serialize.IOEntity;
import org.slowcoders.util.Debug;
import org.slowcoders.observable.AsyncObservable;
import org.slowcoders.util.ClassUtils;

import java.lang.reflect.Type;
import java.util.Objects;

public class ObservableOption<T> extends AsyncObservable<ObservableOption.Observer<T>> {

	private T value;
	private UpdateHook<T> hook = bypassHook;
	private final SerializeInfo slot;
	private static final SerializeInfo unresolvedSlot = new SerializeInfo();

	public interface Observer<T> {
		void onPropertyChanged(T newValue);
	}

	public static abstract class UpdateHook<T> {
		private UpdateHook hook;

		public boolean update(ObservableOption<T> property, T newValue) {
			return hook == null || hook.update(property, newValue);
		}
	}

	public ObservableOption(T initialValue) {
		this(null, initialValue);
	}

	protected ObservableOption(SettingProperties properties, T initialValue) {
		this.updateUnsafe(initialValue);
		if (properties == null) {
			this.slot = unresolvedSlot;
		}
		else {
			this.slot = new SerializeInfo(this, properties);
		}
	}

	@Override
	protected void doNotify(ObservableOption.Observer<T> observer, Object data) {
		observer.onPropertyChanged(this.value);
	}

	/*internal*/ final T getRawValue() {
		return this.value;
	}

	public T get() {
		slot.init();
		return value;
	}

	protected synchronized boolean updateUnsafe(T v) {
		if (Objects.equals(v, this.value)
				||  !hook.update(this, v)) {
			return false;
		}
		if (v == null && slot != null && (slot.flags & SettingFlags.NotNull) != 0) {
			throw new IllegalArgumentException("Can not be null " + this);
		}
		this.value = v;
		return true;
	}

	protected boolean set(T v) {
		slot.init();

		if (!updateUnsafe(v)) {
			return false;
		}

		if (this.slot.getFlag_AutoSave()) {
			SettingProperties cat = this.slot.properties;
			if (cat != null) try {
				cat.save();
			} catch (Exception e) {
				throw Debug.wtf(e);
			}
		}

		this.notifyChanged();
		return true;
	}

	public synchronized void addUpdateHook(UpdateHook<T> hook) {
		Debug.Assert(hook.hook == null);
		hook.hook = this.hook;
		this.hook = hook;
	}

	public synchronized void removeUpdateHook(UpdateHook<T> hook) {
		Debug.Assert(hook != bypassHook);
		UpdateHook prev = null;
		for (UpdateHook chain = this.hook; chain != null; chain = chain.hook) {
			if (chain == hook) {
				if (prev == null) {
					this.hook = chain.hook;
				}
				else {
					prev.hook = chain.hook;
				}
				return;
			}
		}
	}

	protected IOAdapter makeAdapter(Type[] paramTypes) {
		Class<?> clazz = ClassUtils.toClass(paramTypes[0]);
		return IOAdapter.getDefaultAdapter(paramTypes[0]);
	}



	public void notifyChanged() {
		super.postNotification(this);
		if (slot.properties != null) {
			slot.properties.postNotification(this);
		}
	}

	public synchronized T getMutableCopy() {
		try {
			T obj = (T)this.value.getClass().newInstance();
			IOEntity.copyEntity(this.value, obj);
			return obj;
		} catch (InstantiationException | IllegalAccessException e) {
			throw Debug.wtf(e);
		}
	}

	private static final UpdateHook bypassHook = new UpdateHook() {
		public boolean update(ObservableOption property, Object newValue) { return true; }
	};

	final SerializeInfo getRawSerializeInfo() {
		return this.slot;
	}

	public final SerializeInfo getSerializeInfo() {
		slot.init();
		return slot;
	}

	public final String getSerializeKey() {
		return slot.getName();
	}

	public String toString() {
		if (this.slot.field != null) {
			return this.slot.field + ": " + this.value;
		}
		else {
			return String.valueOf(this.value);
		}
	}
}
