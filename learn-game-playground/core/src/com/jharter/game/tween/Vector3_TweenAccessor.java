package com.jharter.game.tween;

import com.jharter.game.primitives.Vector3_;

import aurelienribon.tweenengine.TweenAccessor;

public class Vector3_TweenAccessor implements TweenAccessor<Vector3_> {

	public Vector3_TweenAccessor() {

	}

	@Override
	public int getValues(Vector3_ v, int tweenType, float[] returnValues) {
		switch(TweenType.get(tweenType)) {
			case POSITION_X:
				returnValues[0] = v.v().x;
				return 1;
			case POSITION_Y:
				returnValues[0] = v.v().y;
				return 1;
			case POSITION_XY:
				returnValues[0] = v.v().x;
				returnValues[1] = v.v().y;
				return 2;
			default:
				return 0;
		}
	}

	@Override
	public void setValues(Vector3_ v, int tweenType, float[] newValues) {
		switch(TweenType.get(tweenType)) {
			case POSITION_X:
				v.v().x = newValues[0];
				break;
			case POSITION_Y:
				v.v().y = newValues[0];
				break;
			case POSITION_XY:
				v.v().x = newValues[0];
				v.v().y = newValues[1];
				break;
			default:
				break;
		}
	}

}
