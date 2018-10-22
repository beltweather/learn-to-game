package com.jharter.game.tween;

import com.jharter.game.primitives.Vector2_;

import aurelienribon.tweenengine.TweenAccessor;

public class Vector2_TweenAccessor implements TweenAccessor<Vector2_> {

	public Vector2_TweenAccessor() {

	}

	@Override
	public int getValues(Vector2_ v, int tweenType, float[] returnValues) {
		switch(TweenType.get(tweenType)) {
			case SCALE_X:
				returnValues[0] = v.v().x;
				return 1;
			case SCALE_Y:
				returnValues[0] = v.v().y;
				return 1;
			case SCALE_XY:
				returnValues[0] = v.v().x;
				returnValues[1] = v.v().y;
				return 2;
			default:
				return 0;
		}
	}

	@Override
	public void setValues(Vector2_ v, int tweenType, float[] newValues) {
		switch(TweenType.get(tweenType)) {
			case SCALE_X:
				v.v().x = newValues[0];
				break;
			case SCALE_Y:
				v.v().y = newValues[0];
				break;
			case SCALE_XY:
				v.v().x = newValues[0];
				v.v().y = newValues[1];
				break;
			default:
				break;
		}
	}

}
