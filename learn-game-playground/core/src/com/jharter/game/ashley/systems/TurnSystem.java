package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.jharter.game.ashley.components.Components.ActionQueuedComp;
import com.jharter.game.ashley.components.Components.ActionReadyComp;
import com.jharter.game.ashley.components.Mapper;
import com.jharter.game.ashley.systems.boilerplate.CustomIntervalIteratingSystem;

public class TurnSystem extends CustomIntervalIteratingSystem {
	
	public static final float DEFAULT_INTERVAL = 10f;
	
	@SuppressWarnings("unchecked")
	public TurnSystem() {
		super(Family.all(ActionQueuedComp.class).get(), DEFAULT_INTERVAL);
	}
	
	@Override
	public void update (float deltaTime) {
		accumulator += deltaTime;
		Mapper.getTurnTimerComp().accumulator = accumulator;
		while (accumulator >= interval) {
			accumulator -= interval;
			Mapper.getTurnTimerComp().accumulator = accumulator;
			updateInterval();
		}
	}
	
	@Override
	public void processEntity(Entity entity) {
		entity.remove(ActionQueuedComp.class);
		entity.add(Mapper.Comp.get(ActionReadyComp.class));
	}
	
}
