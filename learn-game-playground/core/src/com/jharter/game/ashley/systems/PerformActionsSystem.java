package com.jharter.game.ashley.systems;

import java.util.Comparator;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.jharter.game.ashley.components.Components.ActionReadyComp;
import com.jharter.game.ashley.components.Components.ActionSpentComp;
import com.jharter.game.ashley.components.Components.TargetingComp;
import com.jharter.game.ashley.components.Mapper;

public class PerformActionsSystem extends SortedIteratingSystem {
	
	public static final float DEFAULT_INTERVAL = 10f;
	
	@SuppressWarnings("unchecked")
	public PerformActionsSystem() {
		super(Family.all(ActionReadyComp.class, TargetingComp.class).get(), new PrioritySort());
	}
	
	@Override
	public void processEntity(Entity entity, float deltaTime) {
		TargetingComp t = Mapper.TargetingComp.get(entity);
		if(t != null) {
			t.performAcceptCallback();
		}
		entity.remove(ActionReadyComp.class);
		entity.add(Mapper.Comp.get(ActionSpentComp.class));
	}
	
	private static class PrioritySort implements Comparator<Entity> {
		private ComponentMapper<TargetingComp> t = ComponentMapper.getFor(TargetingComp.class);
		
		@Override
		public int compare(Entity entityA, Entity entityB) {
			return (int) t.get(entityB).priority - t.get(entityA).priority;
		}
	}
	
}
