package com.jharter.game.tween;

import com.badlogic.gdx.math.Vector2;

import aurelienribon.tweenengine.TweenAccessor;

public class Vector2TweenAccessor implements TweenAccessor<Vector2> {
	
	public Vector2TweenAccessor() {
		
	}

	@Override
	public int getValues(Vector2 v, int tweenType, float[] returnValues) {
		switch(TweenType.get(tweenType)) {
			case SCALE_X: 
				returnValues[0] = v.x;
				return 1;
			case SCALE_Y:
				returnValues[0] = v.y;
				return 1;
			case SCALE_XY:
				returnValues[0] = v.x;
				returnValues[1] = v.y;
				return 2;
			default:
				return 0;
		}
	}

	@Override
	public void setValues(Vector2 v, int tweenType, float[] newValues) {
		switch(TweenType.get(tweenType)) {
			case SCALE_X: 
				v.x = newValues[0];
				break;
			case SCALE_Y:
				v.y = newValues[0];
				break;
			case SCALE_XY:
				v.x = newValues[0];
				v.y = newValues[1];
				break;
			default:
				break;
		}
	}
	
}
