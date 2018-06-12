package com.jharter.game.ashley.systems.packets;

import com.badlogic.gdx.ai.msg.PriorityQueue;
import com.jharter.game.network.GameClient;
import com.jharter.game.network.GameEndPoint;
import com.jharter.game.network.GameServer;
import com.jharter.game.network.packets.Packet;
import com.jharter.game.stages.GameStage;

public abstract class ConsumingPacketSystem<T extends Packet<T>> extends PacketSystem<T> {

	public ConsumingPacketSystem(GameStage stage, GameEndPoint endPoint) {
		super(stage, endPoint);
	}

	@Override
	public void update(GameServer server, GameStage stage, float deltaTime) {
		update(server, stage, deltaTime, consumePackets());
	}

	@Override
	public void update(GameClient client, GameStage stage, float deltaTime) {
		update(client, stage, deltaTime, consumePackets());
	}

	public void update(GameServer server, GameStage stage, float deltaTime, PriorityQueue<T> packets) {
		while(packets.size() > 0) {
			update(server, stage, deltaTime, packets.poll());
		}
	}
	
	public void update(GameClient client, GameStage stage, float deltaTime, PriorityQueue<T> packets) {
		while(packets.size() > 0) {
			update(client, stage, deltaTime, packets.poll());
		}
	}
	
	public abstract void update(GameServer server, GameStage stage, float deltaTime, T packet);
	public abstract void update(GameClient client, GameStage stage, float deltaTime, T packet);
	
}