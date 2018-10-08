package com.jharter.game.layout;

import com.badlogic.ashley.core.Entity;
import com.jharter.game.ecs.components.Components.InvisibleTag;
import com.jharter.game.ecs.entities.IEntityHandler;
import com.jharter.game.util.id.ID;

public class HiddenLayout  extends ZoneLayout {

	public HiddenLayout(IEntityHandler handler) {
		super(handler);
	}

	@Override
	protected TweenTarget getTarget(ID id, int index, Entity entity, TweenTarget target) {
		target.setFromEntity(this, entity);
		target.alpha = 0;
		return target;
	}
	
	@Override
	protected void modifyEntity(ID id, int index, Entity entity, TweenTarget target) {
		Comp.add(InvisibleTag.class, entity);
	}

}
