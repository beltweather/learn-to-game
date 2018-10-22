package com.jharter.game.primitives;

import com.badlogic.gdx.math.Vector3;

public class Vector3_ extends ObjectValue<Vector3_, Vector3> {

	public Vector3_() {
		super();
	}

	@Override
	protected Vector3 handleClear(Vector3 value) {
		value.setZero();
		return value;
	}

	@Override
	protected Vector3 newValueInstance() {
		return new Vector3();
	}

}
