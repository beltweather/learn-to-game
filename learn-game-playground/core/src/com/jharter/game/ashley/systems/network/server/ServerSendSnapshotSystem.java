package com.jharter.game.ashley.systems.network.server;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.TimeUtils;
import com.jharter.game.ashley.components.Components.IDComp;
import com.jharter.game.ashley.components.Components.InputComp;
import com.jharter.game.ashley.components.Components.PositionComp;
import com.jharter.game.ashley.components.Components.VelocityComp;
import com.jharter.game.ashley.components.Mapper;
import com.jharter.game.network.packets.Packets.SnapshotPacket;
import com.jharter.game.network.endpoints.GameServer;
import com.jharter.game.network.endpoints.GameNetwork.EntityData;

public class ServerSendSnapshotSystem extends IntervalSystem {
	
	private static final float DEFAULT_INTERVAL = 1/20f;
	
	private GameServer server;
	private ImmutableArray<Entity> entities;
	
	public ServerSendSnapshotSystem(GameServer server) {
		super(DEFAULT_INTERVAL);
		this.server = server;
	}
	
	@SuppressWarnings("unchecked")
	@Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(PositionComp.class, IDComp.class, VelocityComp.class).get());
    }

	@Override
	protected void updateInterval() {
		SnapshotPacket snapshotPacket = SnapshotPacket.newInstance();
    	snapshotPacket.sendTime = TimeUtils.millis();
    	for(Entity e : entities) {
    		IDComp id = Mapper.IDComp.get(e);
    		PositionComp p = Mapper.PositionComp.get(e);
    		InputComp in = Mapper.InputComp.get(e);
    		
    		EntityData entityData = new EntityData();
        	entityData.id = id.id;
        	entityData.x = p.position.x;
        	entityData.y = p.position.y;
        	
        	if(in != null) {
        		in.input.addInputState(entityData);
        	}
        	
        	snapshotPacket.entityDatas.add(entityData);
        	
    	}
    	server.sendToAll(snapshotPacket);
	}

}