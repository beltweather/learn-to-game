package com.jharter.game.layout;

import com.badlogic.ashley.core.Entity;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.util.id.ID;

public class IdentityLayout extends ZoneLayout {

	public IdentityLayout(ZoneComp z) {
		super(z);
	}

	@Override
	protected TweenTarget getTarget(ID id, int index, Entity entity, TweenTarget target) {
		target.setFromEntity(entity);
		return target;
	}

}
