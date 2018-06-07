package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.jharter.game.stages.GameStage;

public class UpdatePhysicsSystem extends EntitySystem {

	private GameStage stage;
	
	public UpdatePhysicsSystem(GameStage stage) {
		this.stage = stage;
	}
	
	@Override
	public void update (float deltaTime) {
		stage.getBox2DWorld().tick(stage.getCamera(), stage.getInput());
	}
}
