package com.jharter.game.primitives;

public class int_ extends PendableValue<int_, Integer> {

	public int_() {
		super();
	}

	@Override
	protected Integer getNull(Integer value) {
		return 0;
	}

	public int d() {
		return getDefaultValue();
	}

	public int p() {
		return getPendingValue();
	}

	public int a() {
		return getActualValue();
	}

	public int v() {
		return getValue();
	}

	public int_ v(int value) {
		return setValue(value);
	}

	public int_ incr(int value) {
		return setValue(v() + value);
	}

	public int_ decr(int value) {
		return setValue(v() - value);
	}

	public int_ mult(int value) {
		return setValue(v() * value);
	}

	public int_ div(int value) {
		return setValue(v() / value);
	}

	@Override
	protected Integer newValueInstance() {
		return new Integer(0);
	}

}
