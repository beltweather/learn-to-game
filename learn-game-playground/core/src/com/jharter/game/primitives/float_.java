package com.jharter.game.primitives;

public class float_ extends Value<float_, Float> {

	public float_() {
		super();
	}

	@Override
	protected Float getNull(Float value) {
		return 0f;
	}

	public float d() {
		return getDefaultValue();
	}

	public float p() {
		return getPendingValue();
	}

	public float a() {
		return getActualValue();
	}

	public float v() {
		return getValue();
	}

	public float_ v(float value) {
		return setValue(value);
	}

	public float_ incr(float value) {
		return setValue(v() + value);
	}

	public float_ decr(float value) {
		return setValue(v() - value);
	}

	public float_ mult(float value) {
		return setValue(v() * value);
	}

	public float_ div(float value) {
		return setValue(v() / value);
	}

	@Override
	protected Float newValueInstance() {
		return new Float(0f);
	}

}
