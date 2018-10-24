package com.jharter.game.primitives;

public class boolean_ extends PendableValue<boolean_, Boolean> {

	public boolean_() {
		super();
	}

	@Override
	protected Boolean getNull(Boolean value) {
		return false;
	}

	public boolean d() {
		return getDefaultValue();
	}

	public boolean p() {
		return getPendingValue();
	}

	public boolean a() {
		return getActualValue();
	}

	public boolean v() {
		return getValue();
	}

	public boolean_ v(boolean value) {
		return setValue(value);
	}

	@Override
	protected Boolean newValueInstance() {
		return new Boolean(false);
	}

}
