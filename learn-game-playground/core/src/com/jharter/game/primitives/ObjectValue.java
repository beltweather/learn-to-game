package com.jharter.game.primitives;

abstract class ObjectValue<C extends ObjectValue<C,T>,T> extends PendableValue<C,T> {

	public ObjectValue() {
		super();
	}

	public T d() {
		return getDefaultValue();
	}

	public T p() {
		return getPendingValue();
	}

	public T a() {
		return getActualValue();
	}

	public T v() {
		return getValue();
	}

	public C v(T value) {
		return setValue(value);
	}

}
