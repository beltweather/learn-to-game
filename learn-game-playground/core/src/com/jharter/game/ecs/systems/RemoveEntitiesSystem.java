package com.jharter.game.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.jharter.game.ecs.components.Components.IDComp;
import com.jharter.game.ecs.components.Components.RemoveComp;
import com.jharter.game.ecs.systems.boilerplate.GameIteratingSystem;
import com.jharter.game.network.endpoints.GameClient;
import com.jharter.game.network.packets.Packets.RemoveEntityPacket;

public class RemoveEntitiesSystem extends GameIteratingSystem {

	private GameClient client;
	
	public RemoveEntitiesSystem(GameClient client) {
		super(Family.all(RemoveComp.class).get());
		this.client = client;
	}
	
	public void processEntity(Entity entity, float deltaTime) {
		RemoveComp r = Comp.RemoveComp.get(entity);
		if(client == null && r.requestRemove) {
			r.remove = true;
		}
		if(r.remove) {
			getEngine().removeEntity(entity);
		} else if(client != null && r.requestRemove) {
			IDComp id = Comp.IDComp.get(entity);
			if(id != null) {
				client.send(RemoveEntityPacket.newInstance(id.id));
			}
		}
	}

}
