package com.jharter.game.ecs.systems.network;

import com.jharter.game.network.endpoints.GameEndPoint;
import com.jharter.game.network.packets.Packet;
import com.jharter.game.stages.GameStage;

public abstract class PollingPacketSystem<E extends GameEndPoint, T extends Packet<T>> extends PacketSystem<E, T> {

	public PollingPacketSystem(GameStage stage, E endPoint) {
		super(stage, endPoint);
	}

	@Override
	public void update(E endPoint, GameStage stage, float deltaTime) {
		T packet = nextPacket();
		if(packet != null) {
			update(endPoint, stage, deltaTime, packet);
		}
	}

	public abstract void update(E endPoint, GameStage stage, float deltaTime, T packet);
	
}
