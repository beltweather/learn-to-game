package com.jharter.game.ashley.systems;

import java.util.Comparator;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.jharter.game.ashley.components.Comp;
import com.jharter.game.ashley.components.Components.ActivePlayerComp;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.systems.boilerplate.CustomSortedIteratingSystem;
import com.jharter.game.layout.TweenTarget;
import com.jharter.game.layout.ZoneLayout;
import com.jharter.game.tween.TweenUtil;
import com.jharter.game.util.id.ID;

public class ZoneLayoutSystem extends CustomSortedIteratingSystem {

	@SuppressWarnings("unchecked")
	public ZoneLayoutSystem() {
		super(Family.all(ZoneComp.class).get(), new PrioritySort());
		add(ActivePlayerComp.class, Family.all(ActivePlayerComp.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		ZoneComp z = Comp.ZoneComp.get(entity);
		ID activePlayerID = getActivePlayerID();
		for(ID id : z.objectIDs) {
			z.layout.setSystem(this, activePlayerID);
			z.layout.revalidate();
			Entity zEntity = Comp.Entity.get(id);
			if(Comp.AnimatingComp.has(zEntity)) {
				continue;
			}
			TweenTarget target = z.layout.getTarget(id, true);
			if(target != null && !z.layout.matchesTarget(zEntity, target)) {
				TweenUtil.start(getEngine(), id, target);
			}
			
			// Clear out the system since this should only be
			// a temporary reference.
			z.layout.clearSystem();
		}
	}
	
	private ID getActivePlayerID() {
		return getFirstComponent(ActivePlayerComp.class).activePlayerID;
	}
	
	private static class PrioritySort implements Comparator<Entity> {
		@Override
		public int compare(Entity entityA, Entity entityB) {
			ZoneLayout layoutA = Comp.ZoneComp.get(entityA).layout;
			ZoneLayout layoutB = Comp.ZoneComp.get(entityB).layout;
			if(layoutA == null && layoutB != null) {
				return -1;
			} else if(layoutA != null && layoutB == null) {
				return 1;
			} else if(layoutA == null && layoutB == null) {
				return 0;
			}
			return (int) (layoutB.getPriority() - layoutA.getPriority());
		}
	}

}
