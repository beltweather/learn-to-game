package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.M;
import com.jharter.game.layout.TweenTarget;
import com.jharter.game.tween.TweenUtil;
import com.jharter.game.util.Sys;
import com.jharter.game.util.id.ID;

import uk.co.carelesslabs.Enums.ZoneType;

public class ZoneLayoutSystem extends IteratingSystem {

	@SuppressWarnings("unchecked")
	public ZoneLayoutSystem() {
		super(Family.all(ZoneComp.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		ZoneComp z = M.ZoneComp.get(entity);
		for(ID id : z.objectIDs) {
			z.layout.revalidate();
			Entity zEntity = M.Entity.get(id);
			if(M.AnimatingComp.has(zEntity)) {
				continue;
			}
			TweenTarget target = z.layout.getTarget(id, true);
			if(!z.layout.matchesTarget(zEntity, target)) {
				TweenUtil.start(id, target);
			}
		}
	}

}
