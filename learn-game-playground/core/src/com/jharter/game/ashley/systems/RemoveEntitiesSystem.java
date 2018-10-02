package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.jharter.game.ashley.components.Components.IDComp;
import com.jharter.game.ashley.components.Components.RemoveComp;
import com.jharter.game.ashley.systems.boilerplate.GameIteratingSystem;
import com.jharter.game.network.endpoints.GameClient;
import com.jharter.game.network.packets.Packets.RemoveEntityPacket;

public class RemoveEntitiesSystem extends GameIteratingSystem {

	private PooledEngine engine;
	private GameClient client;
	
	@SuppressWarnings("unchecked")
	public RemoveEntitiesSystem(PooledEngine engine, GameClient client) {
		super(Family.all(RemoveComp.class).get());
		this.engine = engine;
		this.client = client;
	}
	
	public void processEntity(Entity entity, float deltaTime) {
		RemoveComp r = Comp.RemoveComp.get(entity);
		if(client == null && r.requestRemove) {
			r.remove = true;
		}
		if(r.remove) {
			engine.removeEntity(entity);
		} else if(client != null && r.requestRemove) {
			IDComp id = Comp.IDComp.get(entity);
			if(id != null) {
				client.send(RemoveEntityPacket.newInstance(id.id));
			}
		}
	}

}
