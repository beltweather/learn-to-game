package com.jharter.game.tween;

import com.badlogic.ashley.core.Entity;
import com.jharter.game.ashley.components.Components.SpriteComp;
import com.jharter.game.ashley.entities.EntityFactory;
import com.jharter.game.ashley.entities.IEntityFactory;

import aurelienribon.tweenengine.TweenAccessor;

public class EntityTweenAccessor extends EntityFactory implements TweenAccessor<Entity> {
	
	public EntityTweenAccessor(IEntityFactory factory) {
		super(factory);
	}
	
	@Override
	public int getValues(Entity entity, int tweenType, float[] returnValues) {
		SpriteComp s = Comp.SpriteComp.get(entity);
		switch(TweenType.get(tweenType)) {
			case POSITION_X: 
				returnValues[0] = s.position.x; 
				return 1;
			case POSITION_Y: 
				returnValues[0] = s.position.y; 
				return 1;
			case POSITION_XY: 
				returnValues[0] = s.position.x;
				returnValues[1] = s.position.y;
				return 2;
			case  POSITION_XY_SCALE:
				returnValues[0] = s.position.x;
				returnValues[1] = s.position.y;
				returnValues[2] = s.scale.x;
				return 3;
			case SCALE_X: 
				returnValues[0] = s.scale.x;
				return 1;
			case SCALE_Y:
				returnValues[0] = s.scale.y;
				return 1;
			case SCALE_XY:
				returnValues[0] = s.scale.x;
				returnValues[1] = s.scale.y;
				return 2;
			case ALPHA:
				returnValues[0] = s.alpha;
				return 1;
			case ANGLE:
				returnValues[0] = s.angleDegrees;
				return 1;
			default:
				return 0;
		}
	}

	@Override
	public void setValues(Entity entity, int tweenType, float[] newValues) {
		SpriteComp s = Comp.SpriteComp.get(entity);
		switch(TweenType.get(tweenType)) {
			case POSITION_X:
				s.position.x = newValues[0];
				break;
			case POSITION_Y:
				s.position.y = newValues[0];
				break;
			case POSITION_XY:
				s.position.x = newValues[0];
				s.position.y = newValues[1];
				break;
			case POSITION_XY_SCALE:
				s.position.x = newValues[0];
				s.position.y = newValues[1];
				s.scale.x = newValues[2];
				s.scale.y = newValues[2];
				break;
			case SCALE_X: 
				s.scale.x = newValues[0];
				break;
			case SCALE_Y:
				s.scale.y = newValues[0];
				break;
			case SCALE_XY:
				s.scale.x = newValues[0];
				s.scale.y = newValues[1];
				break;
			case ALPHA:
				s.alpha = newValues[0];
				break;
			case ANGLE:
				s.angleDegrees = newValues[0];
				break;
			default:
				break;
		}
	}
	
}
