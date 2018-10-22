package com.jharter.game.tween;

import com.jharter.game.primitives.Array_;

import aurelienribon.tweenengine.TweenAccessor;

public class FloatArray_TweenAccessor implements TweenAccessor<Array_<Float>>{

	private FloatTweenAccessor fta = new FloatTweenAccessor();

	public FloatArray_TweenAccessor() {

	}

	@Override
	public int getValues(Array_<Float> target, int tweenType, float[] returnValues) {
		return fta.getValues(target.v().first(), tweenType, returnValues);
	}

	@Override
	public void setValues(Array_<Float> target, int tweenType, float[] newValues) {
		switch(TweenType.get(tweenType)) {
			case ALPHA:
			case ANGLE:
				for(int i = 0; i < target.v().size; i++) {
					target.v().set(i, newValues[0]);
				}
				break;
			default:
				break;
		}
	}

}
