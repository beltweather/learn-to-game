package com.jharter.game.ashley.systems.network.client;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector3;
import com.jharter.game.ashley.components.Components.InputComp;
import com.jharter.game.ashley.components.Components.TargetPositionComp;
import com.jharter.game.ashley.components.M;
import com.jharter.game.ashley.entities.EntityUtil;
import com.jharter.game.ashley.systems.network.InterpolatingPacketSystem;
import com.jharter.game.control.GameInput;
import com.jharter.game.network.endpoints.GameClient;
import com.jharter.game.network.endpoints.GameNetwork.EntityData;
import com.jharter.game.network.packets.PacketManager.PacketPair;
import com.jharter.game.network.packets.Packets.RequestEntityPacket;
import com.jharter.game.network.packets.Packets.SnapshotPacket;
import com.jharter.game.stages.GameStage;
import com.jharter.game.util.id.ID;

public class ClientSnapshotPacketSystem extends InterpolatingPacketSystem<SnapshotPacket> {
	
	public ClientSnapshotPacketSystem(GameStage stage, GameClient client) {
		super(stage, client);
	}

	@Override
	public void update(GameClient client, GameStage stage, float deltaTime, PacketPair<SnapshotPacket> pair, long renderTime) {
		SnapshotPacket pastPacket = pair.pastPacket;
    	SnapshotPacket futurePacket = pair.futurePacket;
    	
		for(ID id : futurePacket.entityDatas.keys()) {
			EntityData pastEntityData = pastPacket.entityDatas.get(id);
			EntityData futureEntityData = futurePacket.entityDatas.get(id);
			
			if(pastEntityData == null || futureEntityData == null) {
				continue;
			}
			
			ID entityId = pastEntityData.id;
			
			Entity entity = M.Entity.get(entityId);
			if(entity != null) {
				InputComp inputComp = M.InputComp.get(entity);
				boolean focus = M.FocusComp.has(entity);
				if(inputComp != null) {
					GameInput in = inputComp.input;
					if(pastEntityData.input != null && !focus) {
						in.setInputState(pastEntityData.input);
					}
					if(futureEntityData.input != null && !focus) {
						in.setInputState(futureEntityData.input);
					}
				}
				
				TargetPositionComp t = M.TargetPositionComp.get(entity);
				if(t.position == null) {
					t.position = new Vector3();
				}
				
				t.position.x = getInterpolatedValue(pair, pastEntityData.x, futureEntityData.x);
				t.position.y = getInterpolatedValue(pair, pastEntityData.y, futureEntityData.y);
			} else {
				client.send(RequestEntityPacket.newInstance(entityId));
			}
		}
	}
	
	@Override
	public Class<SnapshotPacket> getPacketClass() {
		return SnapshotPacket.class;
	}

}
