package com.jharter.game.tween;

import com.badlogic.ashley.core.Entity;
import com.jharter.game.ashley.components.Components.AlphaComp;
import com.jharter.game.ashley.components.Components.PositionComp;
import com.jharter.game.ashley.components.Components.SizeComp;
import com.jharter.game.ashley.components.Mapper;

import aurelienribon.tweenengine.TweenAccessor;

public class EntityTweenAccessor implements TweenAccessor<Entity> {
	
	public EntityTweenAccessor() {
		
	}
	
	protected PositionComp p(Entity entity) {
		return Mapper.PositionComp.get(entity);
	}
	
	protected SizeComp s(Entity entity) {
		return Mapper.SizeComp.get(entity);
	}
	
	protected AlphaComp a(Entity entity) {
		return Mapper.AlphaComp.get(entity);
	}

	@Override
	public int getValues(Entity e, int tweenType, float[] returnValues) {
		switch(TweenType.get(tweenType)) {
			case POSITION_X: 
				returnValues[0] = p(e).position.x; 
				return 1;
			case POSITION_Y: 
				returnValues[0] = p(e).position.y; 
				return 1;
			case POSITION_XY: 
				returnValues[0] = p(e).position.x;
				returnValues[1] = p(e).position.y;
				return 2;
			case  POSITION_XY_SCALE:
				returnValues[0] = p(e).position.x;
				returnValues[1] = p(e).position.y;
				returnValues[2] = s(e).scale.x;
				return 3;
			case SCALE_X: 
				returnValues[0] = s(e).scale.x;
				return 1;
			case SCALE_Y:
				returnValues[0] = s(e).scale.y;
				return 1;
			case SCALE_XY:
				returnValues[0] = s(e).scale.x;
				returnValues[1] = s(e).scale.y;
				return 2;
			case ALPHA:
				returnValues[0] = a(e).alpha;
				return 1;
			case ANGLE:
				returnValues[0] = p(e).angleDegrees;
				return 1;
			default:
				return 0;
		}
	}

	@Override
	public void setValues(Entity e, int tweenType, float[] newValues) {
		switch(TweenType.get(tweenType)) {
			case POSITION_X:
				p(e).position.x = newValues[0];
				break;
			case POSITION_Y:
				p(e).position.y = newValues[0];
				break;
			case POSITION_XY:
				p(e).position.x = newValues[0];
				p(e).position.y = newValues[1];
				break;
			case POSITION_XY_SCALE:
				p(e).position.x = newValues[0];
				p(e).position.y = newValues[1];
				s(e).scale.x = newValues[2];
				s(e).scale.y = newValues[2];
				break;
			case SCALE_X: 
				s(e).scale.x = newValues[0];
				break;
			case SCALE_Y:
				s(e).scale.y = newValues[0];
				break;
			case SCALE_XY:
				s(e).scale.x = newValues[0];
				s(e).scale.y = newValues[1];
				break;
			case ALPHA:
				a(e).alpha = newValues[0];
				break;
			case ANGLE:
				p(e).angleDegrees = newValues[0];
				break;
			default:
				break;
		}
	}
	
}
