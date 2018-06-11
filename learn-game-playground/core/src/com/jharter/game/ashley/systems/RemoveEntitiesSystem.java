package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.jharter.game.ashley.components.Components.BodyComp;
import com.jharter.game.ashley.components.Components.RemoveComp;
import com.jharter.game.ashley.components.Components.SensorComp;
import com.jharter.game.ashley.components.Mapper;

import uk.co.carelesslabs.box2d.Box2DWorld;

public class RemoveEntitiesSystem extends IteratingSystem {

	private PooledEngine engine;
	private Box2DWorld box2D;
	
	@SuppressWarnings("unchecked")
	public RemoveEntitiesSystem(PooledEngine engine, Box2DWorld box2D) {
		super(Family.all(RemoveComp.class).get());
		this.engine = engine;
		this.box2D = box2D;
	}
	
	public void processEntity(Entity entity, float deltaTime) {
		RemoveComp r = Mapper.RemoveComp.get(entity);
		if(r.remove) {
			
			BodyComp b = Mapper.BodyComp.get(entity);
			SensorComp s = Mapper.SensorComp.get(entity);
			if(b != null && b.body != null) {
				box2D.world.destroyBody(b.body);
			}
			if(s != null && s.sensor != null) {
				box2D.world.destroyBody(s.sensor);
			}
			engine.removeEntity(entity);
			
		} else if(r.requestRemove) {
			// XXX Send message to remove requester
		}
	}

}
