package com.jharter.game.ecs.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.jharter.game.stages.GameStage;

public class CleanupInputSystemOld extends EntitySystem {

	private GameStage stage;
	
	public CleanupInputSystemOld(GameStage stage) {
		this.stage = stage;
	}
	
	@Override
	public void update (float deltaTime) {
		if(stage.getInput() != null) {
			stage.getInput().setProcessedClick(true);
		}
	}
	
}
