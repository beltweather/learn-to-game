package com.jharter.game.tween;

import com.badlogic.ashley.core.Entity;
import com.jharter.game.ashley.entities.EntityFactory;
import com.jharter.game.ashley.entities.IEntityFactory;
import com.jharter.game.util.id.ID;

import aurelienribon.tweenengine.TweenAccessor;

public class IDTweenAccessor extends EntityFactory implements TweenAccessor<ID> {
	
	private EntityTweenAccessor entityTweenAccessor = new EntityTweenAccessor(this);
	
	public IDTweenAccessor(IEntityFactory factory) {
		super(factory);
	}
	
	protected Entity entity(ID id) {
		return Comp.Entity.get(id);
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
