package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.jharter.game.ashley.components.Components.IDComp;
import com.jharter.game.ashley.components.Components.RemoveComp;
import com.jharter.game.ashley.components.Mapper;
import com.jharter.game.network.GameClient;
import com.jharter.game.network.GameNetwork.RemoveEntity;

public class RemoveEntitiesSystem extends IteratingSystem {

	private PooledEngine engine;
	private GameClient client;
	
	@SuppressWarnings("unchecked")
	public RemoveEntitiesSystem(PooledEngine engine, GameClient client) {
		super(Family.all(RemoveComp.class).get());
		this.engine = engine;
		this.client = client;
	}
	
	public void processEntity(Entity entity, float deltaTime) {
		RemoveComp r = Mapper.RemoveComp.get(entity);
		if(client == null && r.requestRemove) {
			r.remove = true;
		}
		if(r.remove) {
			engine.removeEntity(entity);
		} else if(client != null && r.requestRemove) {
			IDComp id = Mapper.IDComp.get(entity);
			if(id != null) {
				client.sendTCP(new RemoveEntity(id.id));
			}
		}
	}

}
