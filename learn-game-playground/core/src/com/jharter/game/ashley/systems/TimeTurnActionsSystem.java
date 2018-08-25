package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.jharter.game.ashley.components.Components.ActionQueuedComp;
import com.jharter.game.ashley.components.Components.ActionReadyComp;
import com.jharter.game.ashley.components.Mapper;
import com.jharter.game.ashley.systems.boilerplate.CustomIntervalIteratingSystem;

public class TimeTurnActionsSystem extends CustomIntervalIteratingSystem {
	
	public static final float DEFAULT_INTERVAL = 10f;
	
	@SuppressWarnings("unchecked")
	public TimeTurnActionsSystem() {
		super(Family.all(ActionQueuedComp.class).get(), DEFAULT_INTERVAL);
	}
	
	@Override
	public void update (float deltaTime) {
		if(Mapper.TurnEntity.TurnTimerComp().isStopped()) {
			return;
		}
		accumulator += deltaTime;
		Mapper.TurnEntity.TurnTimerComp().accumulator = accumulator;
		
		if(entities.size() == 4) { // HACK
	
			updateInterval();
			Mapper.TurnEntity.TurnTimerComp().stop();
			System.out.println("Stop the clock");
			accumulator = 0;
			
		} else {
		
			while (accumulator >= interval) {
				accumulator -= interval;
				//Mapper.TurnEntity.TurnTimerComp().accumulator = accumulator;
				updateInterval();
				Mapper.TurnEntity.TurnTimerComp().stop();
			}
			
		}
	}
	
	@Override
	public void processEntity(Entity entity) {
		entity.remove(ActionQueuedComp.class);
		entity.add(Mapper.Comp.get(ActionReadyComp.class));
	}
	
}
