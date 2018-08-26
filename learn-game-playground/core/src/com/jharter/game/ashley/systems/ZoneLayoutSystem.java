package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.Mapper;
import com.jharter.game.layout.LayoutTarget;
import com.jharter.game.tween.TweenUtil;
import com.jharter.game.util.id.ID;

public class ZoneLayoutSystem extends IteratingSystem {

	@SuppressWarnings("unchecked")
	public ZoneLayoutSystem() {
		super(Family.all(ZoneComp.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		ZoneComp z = Mapper.ZoneComp.get(entity);
		for(ID id : z.objectIDs) {
			z.layout.revalidate();
			Entity zEntity = Mapper.Entity.get(id);
			if(Mapper.AnimatingComp.has(zEntity)) {
				continue;
			}
			LayoutTarget target = z.layout.getTarget(id);
			if(!z.layout.matchesTarget(zEntity, target)) {
				TweenUtil.tween(id, target);
			}
		}
	}

}