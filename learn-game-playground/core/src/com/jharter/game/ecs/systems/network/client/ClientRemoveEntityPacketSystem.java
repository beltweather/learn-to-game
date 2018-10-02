package com.jharter.game.ecs.systems.network.client;

import com.badlogic.ashley.core.Entity;
import com.jharter.game.ecs.components.Components.RemoveComp;
import com.jharter.game.ecs.systems.network.ConsumingPacketSystem;
import com.jharter.game.network.endpoints.GameClient;
import com.jharter.game.network.packets.Packets.RemoveEntityPacket;
import com.jharter.game.stages.GameStage;

public class ClientRemoveEntityPacketSystem extends ConsumingPacketSystem<GameClient, RemoveEntityPacket> {

	public ClientRemoveEntityPacketSystem(GameStage stage, GameClient client) {
		super(stage, client);
	}

	@Override
	public void update(GameClient client, GameStage stage, float deltaTime, RemoveEntityPacket packet) {
		Entity entity = findEntity(packet.id);
		if(entity != null) {
			RemoveComp r = Comp.RemoveComp.get(entity);
			if(r != null) {
				r.remove = true;
			}
		}
	}

	@Override
	public Class<RemoveEntityPacket> getPacketClass() {
		return RemoveEntityPacket.class;
	}

}
