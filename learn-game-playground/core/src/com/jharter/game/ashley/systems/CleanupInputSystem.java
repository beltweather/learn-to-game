package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.jharter.game.stages.GameStage;

public class CleanupInputSystem extends EntitySystem {

	private GameStage stage;
	
	public CleanupInputSystem(GameStage stage) {
		this.stage = stage;
	}
	
	@Override
	public void update (float deltaTime) {
		stage.getInput().setProcessedClick(true);
	}
	
}
