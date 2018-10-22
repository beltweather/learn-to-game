package com.jharter.game.primitives;

import com.badlogic.gdx.math.Vector2;

public class Vector2_ extends ObjectValue<Vector2_, Vector2> {

	public Vector2_() {
		super();
	}

	@Override
	protected Vector2 handleClear(Vector2 value) {
		value.setZero();
		return value;
	}

	@Override
	protected Vector2 newValueInstance() {
		return new Vector2();
	}

}
