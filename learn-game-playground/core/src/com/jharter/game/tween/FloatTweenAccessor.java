package com.jharter.game.tween;

import aurelienribon.tweenengine.TweenAccessor;

public class FloatTweenAccessor implements TweenAccessor<Float> {

	public FloatTweenAccessor() {

	}

	@Override
	public int getValues(Float f, int tweenType, float[] returnValues) {
		switch(TweenType.get(tweenType)) {
			case ALPHA:
			case ANGLE:
			case POSITION_X:
			case POSITION_Y:
			default:
				returnValues[0] = f;
				return 1;
		}
	}

	@Override
	public void setValues(Float f, int tweenType, float[] newValues) {
		switch(TweenType.get(tweenType)) {
			case ALPHA:
			case ANGLE:
			case POSITION_X:
			case POSITION_Y:
			default:
				f = newValues[0];
		}
	}

}
