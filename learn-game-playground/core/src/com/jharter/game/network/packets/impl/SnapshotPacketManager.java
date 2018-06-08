package com.jharter.game.network.packets.impl;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;
import com.esotericsoftware.kryonet.Connection;
import com.jharter.game.ashley.components.Components.InputComp;
import com.jharter.game.ashley.components.Components.TargetPositionComp;
import com.jharter.game.ashley.components.Mapper;
import com.jharter.game.ashley.entities.EntityUtil;
import com.jharter.game.control.Input;
import com.jharter.game.network.GameClient;
import com.jharter.game.network.GameNetwork.EntityData;
import com.jharter.game.network.GameNetwork.SnapshotPacket;
import com.jharter.game.network.GameServer;
import com.jharter.game.network.packets.PacketManager;

public class SnapshotPacketManager extends PacketManager<SnapshotPacket> {
	
	@Override
	public void received(GameServer server, Connection connection, SnapshotPacket packet) {
		
	}
	
	@Override
	public void update(GameServer server, float deltaTime) {
		
	}
	
	@Override
	public void received(GameClient client, Connection connection, SnapshotPacket packet) {
		
	}
	
	@Override
	public void update(GameClient client, float deltaTime) {
	  	long renderTime = getRenderTime(client, TimeUtils.millis());
    	PacketPair<SnapshotPacket> pair = getPacketsBeforeAndAfter(renderTime);
    	if(pair == null) {
    		return;
    	}
		
    	SnapshotPacket pastPacket = pair.pastPacket;
    	SnapshotPacket futurePacket = pair.futurePacket;
    	
		// Handle case that should soon be deprecated where we've removed an entity
		// in between these packets. In reality, these packets should store entities
		// as maps so that we can key in to them and update accordingly. Using index
		// is far too wonky and unpredictable
		if(pastPacket.entityDatas.size() != futurePacket.entityDatas.size()) {
			return;
		}
		
		for(int i = 0; i < pastPacket.entityDatas.size(); i++) {
			EntityData pastEntityData = pastPacket.entityDatas.get(i);
			EntityData futureEntityData = futurePacket.entityDatas.get(i);
			String entityId = pastEntityData.id;
			
			Entity entity = EntityUtil.findEntity(entityId);
			if(entity != null) {
				InputComp inputComp = Mapper.InputComp.get(entity);
				boolean focus = Mapper.FocusComp.has(entity);
				if(inputComp != null) {
					Input in = inputComp.input;
					if(pastEntityData.input != null && !focus) {
						in.setInputState(pastEntityData.input);
					}
					if(futureEntityData.input != null && !focus) {
						in.setInputState(futureEntityData.input);
					}
				}
				
				TargetPositionComp t = Mapper.TargetPositionComp.get(entity);
				if(t.position == null) {
					t.position = new Vector3();
				}
				
				t.position.x = getInterpolatedValue(pastPacket.time, pastEntityData.x,
													futurePacket.time, futureEntityData.x,
						   							renderTime);
				t.position.y = getInterpolatedValue(pastPacket.time, pastEntityData.y,
													futurePacket.time, futureEntityData.y,
													renderTime);
			}
		}
	}
	
	private long getRenderTime(GameClient client, long currentTime) {
    	return currentTime - (client.getPing() + 50);
    }
	
	private float getInterpolatedValue(long timeSnapshot1, float valueSnapshot1, long timeSnapshot2, float valueSnapshot2, long timeCurrent) {
    	return (timeCurrent - timeSnapshot1) / (timeSnapshot2 - timeSnapshot1) * (valueSnapshot2 - valueSnapshot1) + valueSnapshot1;
    }

}
