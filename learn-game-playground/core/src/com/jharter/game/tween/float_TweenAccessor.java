package com.jharter.game.tween;

import com.jharter.game.primitives.float_;

import aurelienribon.tweenengine.TweenAccessor;

public class float_TweenAccessor implements TweenAccessor<float_> {

	public float_TweenAccessor() {

	}

	@Override
	public int getValues(float_ f, int tweenType, float[] returnValues) {
		switch(TweenType.get(tweenType)) {
			case ALPHA:
			case ANGLE:
			case POSITION_X:
			case POSITION_Y:
			default:
				returnValues[0] = f.v();
				return 1;
		}
	}

	@Override
	public void setValues(float_ f, int tweenType, float[] newValues) {
		switch(TweenType.get(tweenType)) {
			case ALPHA:
			case ANGLE:
			case POSITION_X:
			case POSITION_Y:
			default:
				f.v(newValues[0]);
		}
	}

}
