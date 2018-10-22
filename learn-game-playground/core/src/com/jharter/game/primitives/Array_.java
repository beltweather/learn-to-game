package com.jharter.game.primitives;

import com.badlogic.gdx.utils.Array;

public class Array_<E> extends ObjectValue<Array_<E>, Array<E>> {

	public Array_() {
		super();
	}

	@Override
	protected Array<E> handleClear(Array<E> value) {
		value.clear();
		return value;
	}

	@Override
	protected Array<E> newValueInstance() {
		return new Array<E>();
	}

}
