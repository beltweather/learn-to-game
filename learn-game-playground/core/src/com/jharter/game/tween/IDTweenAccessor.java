package com.jharter.game.tween;

import com.badlogic.ashley.core.Entity;
import com.jharter.game.ashley.components.M;
import com.jharter.game.util.id.ID;

import aurelienribon.tweenengine.TweenAccessor;

public class IDTweenAccessor implements TweenAccessor<ID> {
	
	private EntityTweenAccessor entityTweenAccessor = new EntityTweenAccessor();
	
	public IDTweenAccessor() {
		
	}
	
	protected Entity entity(ID id) {
		return M.Entity.get(id);
	}

	@Override
	public int getValues(ID id, int tweenType, float[] returnValues) {
		return entityTweenAccessor.getValues(entity(id), tweenType, returnValues);
	}

	@Override
	public void setValues(ID id, int tweenType, float[] newValues) {
		entityTweenAccessor.setValues(entity(id), tweenType, newValues);
	}
	
}
