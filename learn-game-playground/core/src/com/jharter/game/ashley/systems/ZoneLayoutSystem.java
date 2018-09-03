package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.jharter.game.ashley.components.Comp;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.layout.TweenTarget;
import com.jharter.game.tween.TweenUtil;
import com.jharter.game.util.id.ID;

public class ZoneLayoutSystem extends IteratingSystem {

	@SuppressWarnings("unchecked")
	public ZoneLayoutSystem() {
		super(Family.all(ZoneComp.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		ZoneComp z = Comp.ZoneComp.get(entity);
		for(ID id : z.objectIDs) {
			z.layout.setSystem(this);
			z.layout.revalidate();
			Entity zEntity = Comp.Entity.get(id);
			if(Comp.AnimatingComp.has(zEntity)) {
				continue;
			}
			TweenTarget target = z.layout.getTarget(id, true);
			if(!z.layout.matchesTarget(zEntity, target)) {
				TweenUtil.start(getEngine(), id, target);
			}
			
			// Clear out the system since this should only be
			// a temporary reference.
			z.layout.setSystem(null);
		}
	}

}
