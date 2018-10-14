package com.jharter.game.layout;

import com.badlogic.ashley.core.Entity;
import com.jharter.game.ecs.entities.IEntityHandler;
import com.jharter.game.util.id.ID;

public class EnemyLayout extends ZoneLayout {

	public EnemyLayout(IEntityHandler handler) {
		super(handler);
	}

	@Override
	protected TweenTarget getTarget(ID id, int index, Entity entity, TweenTarget target) {
		target.setFromEntity(this, entity);
		if(Comp.UntargetableComp.has(entity) && !Comp.CursorTargetComp.has(entity)) {
			target.alpha = 0.25f;
		} else {
			target.alpha = 1f;
		}
		return target;
	}
	
}
