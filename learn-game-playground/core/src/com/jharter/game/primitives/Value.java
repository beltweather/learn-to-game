package com.jharter.game.primitives;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

abstract class Value<C extends Value<C,T>,T> {

	public static enum ValueType {
		DEFAULT, ACTUAL, PENDING;
	}

	private T actualValue;
	private T pendingValue;
	private T defaultValue;
	private ValueType type = ValueType.ACTUAL;
	private boolean hasPending = false;

	public Value() {
		defaultValue = newValueInstance();
		actualValue = newValueInstance();
		pendingValue = newValueInstance();
	}

	public boolean hasPending() {
		return hasPending;
	}

	public C d(T value) {
		return setDefaultValue(value).setToDefault();
	}

	public C v(T value) {
		return setValue(value);
	}

	public C setToDefault() {
		setValueType(ValueType.ACTUAL);
		pendingValue = handleClear(pendingValue);
		hasPending = false;
		return setActualValue(handleUpdateValue(actualValue, defaultValue));
	}

	public C resetPending() {
		return setHasPending(false).setPendingValue(handleUpdateValue(getPendingValue(), getActualValue()));
	}

	public C beginPending(boolean pending) {
		return asPending(pending);
	}

	public C endPending() {
		return useActual();
	}

	public void clear() {
		setValueType(ValueType.ACTUAL);
		setHasPending(false);
		defaultValue = handleClear(defaultValue);
		actualValue = handleClear(actualValue);
		pendingValue = handleClear(pendingValue);
	}

	@SuppressWarnings({ "unchecked" })
	private C me() {
		return (C) this;
	}

	protected C setHasPending(boolean hasPending) {
		this.hasPending = hasPending;
		return me();
	}

	protected ValueType getValueType() {
		return type;
	}

	protected C setValueType(ValueType type) {
		this.type = type;
		return me();
	}

	protected T getValue() {
		return getValue(type);
	}

	protected T getValue(ValueType type) {
		T value = null;
		switch(type) {
			case DEFAULT:
				value = defaultValue;
				break;
			case PENDING:
				value = pendingValue;
				break;
			case ACTUAL:
			default:
				value = actualValue;
				break;
		}
		if(value == null) {
			return getNull(null);
		}
		return value;
	}

	protected T getDefaultValue() {
		return getValue(ValueType.DEFAULT);
	}

	protected T getActualValue() {
		return getValue(ValueType.ACTUAL);
	}

	protected T getPendingValue() {
		return getValue(ValueType.PENDING);
	}

	protected C setDefaultValue(T value) {
		return setValue(ValueType.DEFAULT, value);
	}

	protected C setActualValue(T value) {
		return setValue(ValueType.ACTUAL, value);
	}

	protected C setPendingValue(T value) {
		return setValue(ValueType.PENDING, value);
	}

	protected C setValue(T value) {
		return setValue(type, value);
	}

	protected C setValue(ValueType type, T value) {
		if(value == null) {
			value = getNull(value);
		}
		switch(type) {
		case DEFAULT:
			defaultValue = value;
			break;
		case PENDING:
			pendingValue = value;
			break;
		case ACTUAL:
			actualValue = value;
			break;
		}
		return me();
	}

	protected C asPending(boolean pending) {
		if(pending) {
			return usePending();
		}
		return useActual();
	}

	protected C useActual() {
		setValueType(ValueType.ACTUAL);
		return me();
	}

	protected C usePending() {
		setHasPending(true);
		setValueType(ValueType.PENDING);
		return me();
	}

	/**
	 * Override this to determine how an old value should be
	 * converted to a new value. By default it will just
	 * return the new value, but for certain objects like
	 * Arrays and vectors, one might prefer to copy the values
	 * from new into old and return old.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected T handleUpdateValue(T oldValue, T newValue) {
		if(oldValue instanceof Vector2) {
			((Vector2) oldValue).set((Vector2) newValue);
			return oldValue;
		}
		if(oldValue instanceof Vector3) {
			((Vector3) oldValue).set((Vector3) newValue);
			return oldValue;
		}
		if(oldValue instanceof Array) {
			((Array) oldValue).clear();
			((Array) oldValue).addAll((Array) newValue);
			return oldValue;
		}
		return newValue;
	}

	/**
	 * Override this to determine what should happen when a value
	 * should be cleared out. By default, this will simply return
	 * null, but for objects like Arrays and vectors, they may
	 * wish to be cleared out instead of set to null.
	 */
	protected T handleClear(T value) {
		return null;
	}

	protected T getNull(T value) {
		return null;
	}

	protected abstract T newValueInstance();

}
