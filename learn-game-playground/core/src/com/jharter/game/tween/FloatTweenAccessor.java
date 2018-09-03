package com.jharter.game.tween;

import aurelienribon.tweenengine.TweenAccessor;

public class FloatTweenAccessor implements TweenAccessor<Float> {
	
	public FloatTweenAccessor() {
		
	}

	@Override
	public int getValues(Float f, int tweenType, float[] returnValues) {
		switch(TweenType.get(tweenType)) {
			case ALPHA:
				returnValues[0] = f;
				return 1;
			case ANGLE:
				returnValues[0] = f;
				return 1;
			default:
				return 0;
		}
	}

	@Override
	public void setValues(Float f, int tweenType, float[] newValues) {
		switch(TweenType.get(tweenType)) {
			case ALPHA:
			case ANGLE:
				f = newValues[0];
				break;
			default:
				break;
		}
	}
	
}
