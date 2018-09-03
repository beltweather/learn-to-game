package com.jharter.game.tween;

import com.badlogic.gdx.utils.Array;

import aurelienribon.tweenengine.TweenAccessor;

public class FloatArrayTweenAccessor implements TweenAccessor<Array<Float>>{

	private FloatTweenAccessor fta = new FloatTweenAccessor();
	
	public FloatArrayTweenAccessor() {
		
	}
	
	@Override
	public int getValues(Array<Float> target, int tweenType, float[] returnValues) {
		return fta.getValues(target.first(), tweenType, returnValues);
	}

	@Override
	public void setValues(Array<Float> target, int tweenType, float[] newValues) {
		switch(TweenType.get(tweenType)) {
			case ALPHA:
			case ANGLE:
				for(int i = 0; i < target.size; i++) {
					target.set(i, newValues[0]);
				}
				break;
			default:
				break;
		}
	}

}
