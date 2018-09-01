package com.jharter.game.tween;

import com.badlogic.gdx.math.Vector3;
import com.jharter.game.util.Sys;

import aurelienribon.tweenengine.TweenAccessor;

public class Vector3TweenAccessor implements TweenAccessor<Vector3> {
	
	public Vector3TweenAccessor() {
		
	}
	
	@Override
	public int getValues(Vector3 v, int tweenType, float[] returnValues) {
		switch(TweenType.get(tweenType)) {
			case POSITION_X: 
				returnValues[0] = v.x; 
				return 1;
			case POSITION_Y: 
				returnValues[0] = v.y; 
				return 1;
			case POSITION_XY: 
				returnValues[0] = v.x;
				returnValues[1] = v.y;
				return 2;
			default:
				return 0;
		}
	}

	@Override
	public void setValues(Vector3 v, int tweenType, float[] newValues) {
		switch(TweenType.get(tweenType)) {
			case POSITION_X:
				Sys.out.println("X: " + newValues[0]);
				v.x = newValues[0];
				break;
			case POSITION_Y:
				v.y = newValues[0];
				break;
			case POSITION_XY:
				v.x = newValues[0];
				v.y = newValues[1];
				break;
			default:
				break;
		}
	}
	
}
