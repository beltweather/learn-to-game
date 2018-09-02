package com.jharter.game.layout;

import com.badlogic.ashley.core.Entity;
import com.jharter.game.util.id.ID;

public class IdentityLayout extends ZoneLayout {

	public IdentityLayout() {
		super();
	}

	@Override
	protected TweenTarget getTarget(ID id, int index, Entity entity, TweenTarget target) {
		target.setFromEntity(entity);
		return target;
	}

}
