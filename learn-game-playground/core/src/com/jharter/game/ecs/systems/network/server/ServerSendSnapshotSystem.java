package com.jharter.game.ecs.systems.network.server;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.TimeUtils;
import com.jharter.game.ecs.components.Components.IDComp;
import com.jharter.game.ecs.components.Components.InputComp;
import com.jharter.game.ecs.components.Components.SpriteComp;
import com.jharter.game.ecs.components.Components.VelocityComp;
import com.jharter.game.ecs.systems.boilerplate.GameIntervalSystem;
import com.jharter.game.network.endpoints.GameNetwork.EntityData;
import com.jharter.game.network.endpoints.GameServer;
import com.jharter.game.network.packets.Packets.SnapshotPacket;

public class ServerSendSnapshotSystem extends GameIntervalSystem {
	
	private static final float DEFAULT_INTERVAL = 1/20f;
	
	private GameServer server;
	private ImmutableArray<Entity> entities;
	
	public ServerSendSnapshotSystem(GameServer server) {
		super(DEFAULT_INTERVAL);
		this.server = server;
	}
	
	@Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(SpriteComp.class, IDComp.class, VelocityComp.class).get());
    }

	@Override
	protected void updateInterval() {
		SnapshotPacket snapshotPacket = SnapshotPacket.newInstance();
    	snapshotPacket.sendTime = TimeUtils.millis();
    	for(Entity e : entities) {
    		IDComp id = Comp.IDComp.get(e);
    		SpriteComp s = Comp.SpriteComp.get(e);
    		InputComp in = Comp.InputComp.get(e);
    		
    		EntityData entityData = new EntityData();
        	entityData.id = id.id;
        	entityData.x = s.position.x;
        	entityData.y = s.position.y;
        	
        	if(in != null) {
        		in.input.addInputState(entityData);
        	}
        	
        	snapshotPacket.entityDatas.put(entityData.id, entityData);
        	
    	}
    	server.sendToAll(snapshotPacket);
	}

}
