package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.jharter.game.ashley.components.Components.InputComp;
import com.jharter.game.ashley.components.Mapper;

public class SelectInputSystem extends IteratingSystem {

	public SelectInputSystem() {
		super(Family.all(InputComp.class).get());
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		InputComp i = Mapper.InputComp.get(entity);
		i.input.setRenderStateToRealTimeState();
	}
	
}
