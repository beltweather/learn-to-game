package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.jharter.game.stages.GameStage;

public class SelectInputSystem extends EntitySystem {

	private GameStage stage;
	
	public SelectInputSystem(GameStage stage) {
		this.stage = stage;
	}
	
	@Override
	public void update (float deltaTime) {
		stage.getInput().setRenderStateToRealTimeState();
	}
	
}
