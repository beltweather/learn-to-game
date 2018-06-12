package com.jharter.game.ashley.systems.packets;

import com.jharter.game.network.GameClient;
import com.jharter.game.network.GameEndPoint;
import com.jharter.game.network.GameServer;
import com.jharter.game.network.packets.Packet;
import com.jharter.game.stages.GameStage;

public abstract class PollingPacketSystem<T extends Packet<T>> extends PacketSystem<T> {

	public PollingPacketSystem(GameStage stage, GameEndPoint endPoint) {
		super(stage, endPoint);
	}

	@Override
	public void update(GameServer server, GameStage stage, float deltaTime) {
		T packet = nextPacket();
		if(packet != null) {
			update(server, stage, deltaTime, packet);
		}
	}

	@Override
	public void update(GameClient client, GameStage stage, float deltaTime) {
		T packet = nextPacket();
		if(packet != null) {
			update(client, stage, deltaTime, packet);
		}
	}

	public abstract void update(GameServer server, GameStage stage, float deltaTime, T packet);
	public abstract void update(GameClient client, GameStage stage, float deltaTime, T packet);
	
}
