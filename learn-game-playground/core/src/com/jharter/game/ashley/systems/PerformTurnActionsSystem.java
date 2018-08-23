package com.jharter.game.ashley.systems;

import java.util.Comparator;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.jharter.game.ashley.components.Components.ActionReadyComp;
import com.jharter.game.ashley.components.Components.ActionSpentComp;
import com.jharter.game.ashley.components.Components.TurnActionComp;
import com.jharter.game.ashley.components.Mapper;

public class PerformTurnActionsSystem extends SortedIteratingSystem {
	
	public static final float DEFAULT_INTERVAL = 10f;
	
	@SuppressWarnings("unchecked")
	public PerformTurnActionsSystem() {
		super(Family.all(ActionReadyComp.class, TurnActionComp.class).get(), new PrioritySort());
	}
	
	@Override
	public void processEntity(Entity entity, float deltaTime) {
		TurnActionComp t = Mapper.TurnActionComp.get(entity);
		if(t != null && t.turnAction.priority == 0) {
			t.turnAction.performAcceptCallback();
		}
		entity.remove(ActionReadyComp.class);
		entity.add(Mapper.Comp.get(ActionSpentComp.class));
	}
	
	private static class PrioritySort implements Comparator<Entity> {
		private ComponentMapper<TurnActionComp> t = ComponentMapper.getFor(TurnActionComp.class);
		
		@Override
		public int compare(Entity entityA, Entity entityB) {
			return (int) t.get(entityB).turnAction.priority - t.get(entityA).turnAction.priority;
		}
	}
	
}
