package com.jharter.game.layout;

import com.badlogic.ashley.core.Entity;
import com.jharter.game.ashley.entities.IEntityHandler;
import com.jharter.game.util.id.ID;

public class IdentityLayout extends ZoneLayout {

	public IdentityLayout(IEntityHandler handler) {
		super(handler);
	}

	@Override
	protected TweenTarget getTarget(ID id, int index, Entity entity, TweenTarget target) {
		target.setFromEntity(this, entity);
		return target;
	}

}
