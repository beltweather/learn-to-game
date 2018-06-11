package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.jharter.game.ashley.components.Components.FocusComp;
import com.jharter.game.ashley.components.Components.PositionComp;
import com.jharter.game.ashley.components.Mapper;

import uk.co.carelesslabs.Rumble;

public class CameraSystem extends IteratingSystem {

	private OrthographicCamera camera;
	
	@SuppressWarnings("unchecked")
	public CameraSystem(OrthographicCamera camera) {
		super(Family.all(FocusComp.class, PositionComp.class).get());
		this.camera = camera;
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		if (Rumble.getRumbleTimeLeft() > 0){
            Rumble.tick(deltaTime);
            camera.translate(Rumble.getPos());
        } else {
            camera.position.lerp(Mapper.PositionComp.get(entity).position, .2f);
        }
		camera.update();
	}
}
