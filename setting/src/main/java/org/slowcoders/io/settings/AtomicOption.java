package org.slowcoders.io.settings;

public class AtomicOption<T> extends ObservableOption<T> implements MutableOption<T> {

	private T defValue;

	public AtomicOption(T defValue) {
		super(defValue);
		this.defValue = super.getRawValue();
	}

	public synchronized boolean set(T v) {
		return super.set(v);
	}

	public final T getDefaultValue() {
		return this.defValue;
	}

	public void resetToDefaultValue() {
		this.set(defValue);
	}

	//==========================================================================//
	// Internal methods
	//--------------------------------------------------------------------------//

	/*internal*/ AtomicOption(SettingProperties category, T defValue) {
		super(category, defValue);
		this.defValue = defValue;
	}

	public void replaceDefaultValueByCurrentValue() {
		this.defValue = super.get();
	}

}
