package com.jharter.game.ashley.systems.packets.impl;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector3;
import com.jharter.game.ashley.components.Components.InputComp;
import com.jharter.game.ashley.components.Components.TargetPositionComp;
import com.jharter.game.ashley.components.Mapper;
import com.jharter.game.ashley.entities.EntityUtil;
import com.jharter.game.ashley.systems.packets.InterpolatingPacketSystem;
import com.jharter.game.ashley.systems.packets.impl.Packets.RequestEntityPacket;
import com.jharter.game.ashley.systems.packets.impl.Packets.SnapshotPacket;
import com.jharter.game.control.Input;
import com.jharter.game.network.GameClient;
import com.jharter.game.network.GameEndPoint;
import com.jharter.game.network.GameNetwork.EntityData;
import com.jharter.game.network.GameServer;
import com.jharter.game.network.packets.PacketManager.PacketPair;
import com.jharter.game.stages.GameStage;
import com.jharter.game.util.id.ID;

public class SnapshotPacketSystem extends InterpolatingPacketSystem<SnapshotPacket> {
	
	public SnapshotPacketSystem(GameStage stage, GameEndPoint endPoint) {
		super(stage, endPoint);
	}

	@Override
	public void update(GameServer server, GameStage stage, float deltaTime, PacketPair<SnapshotPacket> pair, long renderTime) {
		
	}
	
	@Override
	public void update(GameClient client, GameStage stage, float deltaTime, PacketPair<SnapshotPacket> pair, long renderTime) {
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
			ID entityId = pastEntityData.id;
			
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
				
				t.position.x = getInterpolatedValue(pair, pastEntityData.x, futureEntityData.x);
				t.position.y = getInterpolatedValue(pair, pastEntityData.y, futureEntityData.y);
			} else {
				client.sendUDP(RequestEntityPacket.newInstance(entityId));
			}
		}
	}
	
	@Override
	public Class<SnapshotPacket> getPacketClass() {
		return SnapshotPacket.class;
	}

}
