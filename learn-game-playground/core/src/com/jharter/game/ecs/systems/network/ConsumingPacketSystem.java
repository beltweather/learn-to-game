package com.jharter.game.ecs.systems.network;

import com.badlogic.gdx.ai.msg.PriorityQueue;
import com.jharter.game.network.endpoints.GameEndPoint;
import com.jharter.game.network.packets.Packet;
import com.jharter.game.stages.GameStage;

public abstract class ConsumingPacketSystem<E extends GameEndPoint, T extends Packet<T>> extends PacketSystem<E, T> {

	public ConsumingPacketSystem(GameStage stage, E endPoint) {
		super(stage, endPoint);
	}

	@Override
	public void update(E endPoint, GameStage stage, float deltaTime) {
		update(endPoint, stage, deltaTime, consumePackets());
	}

	public void update(E endPoint, GameStage stage, float deltaTime, PriorityQueue<T> packets) {
		while(packets.size() > 0) {
			update(endPoint, stage, deltaTime, packets.poll());
		}
	}
	
	public abstract void update(E endPoint, GameStage stage, float deltaTime, T packet);
	
}