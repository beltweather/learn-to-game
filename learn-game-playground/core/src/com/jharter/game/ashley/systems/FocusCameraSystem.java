package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.jharter.game.ashley.components.Comp;
import com.jharter.game.ashley.components.Components.FocusComp;
import com.jharter.game.ashley.components.Components.SpriteComp;
import com.jharter.game.debug.Debug;

import uk.co.carelesslabs.Rumble;

public class FocusCameraSystem extends IteratingSystem {

	private OrthographicCamera camera;
	
	@SuppressWarnings("unchecked")
	public FocusCameraSystem(OrthographicCamera camera) {
		super(Family.all(FocusComp.class, SpriteComp.class).get());
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
