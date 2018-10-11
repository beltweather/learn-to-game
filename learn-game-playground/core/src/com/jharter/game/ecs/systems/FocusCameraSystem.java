package com.jharter.game.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.jharter.game.debug.Debug;
import com.jharter.game.ecs.components.Components.FocusTag;
import com.jharter.game.ecs.components.Components.SpriteComp;
import com.jharter.game.ecs.systems.boilerplate.GameIteratingSystem;

import uk.co.carelesslabs.Rumble;

public class FocusCameraSystem extends GameIteratingSystem {

	private OrthographicCamera camera;
	
	public FocusCameraSystem(OrthographicCamera camera) {
		super(Family.all(FocusTag.class, SpriteComp.class).get());
		this.camera = camera;
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		if (Rumble.getRumbleTimeLeft() > 0){
            Rumble.tick(deltaTime);
            camera.translate(Rumble.getPos());
        } else {
            camera.position.lerp(Debug.LOCK_CAMERA ? Debug.LOCKED_CAMERA_CENTER : Comp.SpriteComp.get(entity).position, .2f);
        }
		camera.update();
	}
}
