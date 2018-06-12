package com.jharter.game.ashley.systems.packets;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.ai.msg.PriorityQueue;
import com.jharter.game.collections.SynchronizedPriorityQueue;
import com.jharter.game.network.GameClient;
import com.jharter.game.network.GameEndPoint;
import com.jharter.game.network.GameServer;
import com.jharter.game.network.packets.Packet;
import com.jharter.game.network.packets.PacketManager;
import com.jharter.game.network.packets.PacketManager.PacketPair;
import com.jharter.game.stages.GameStage;

public abstract class PacketSystem<T extends Packet<T>> extends EntitySystem {

	protected PacketManager<T> packetManager;
	protected GameServer server;
	protected GameClient client;
	protected GameStage stage;
	
	public PacketSystem(GameStage stage, GameEndPoint endPoint) {
		this.stage = stage;
		this.packetManager = new PacketManager<T>();
		if(endPoint != null) {
			this.server = endPoint instanceof GameServer ? (GameServer) endPoint : null;
			this.client = endPoint instanceof GameClient ? (GameClient) endPoint : null;
			endPoint.addPacketManager(getPacketClass(), packetManager);
		}
	}
	
	public abstract Class<T> getPacketClass();
	
	public PacketManager<T> getPacketManager() {
		return packetManager;
	}
	
	@Override
	public void update(float deltaTime) {
		if(!packetManager.hasPackets()) {
			return;
		}
		if(server != null) {
			update(server, stage, deltaTime);
		} else if(client != null) {
			update(client, stage, deltaTime);
		}
	}
	
	public abstract void update(GameServer server, GameStage stage, float deltaTime);
	public abstract void update(GameClient client, GameStage stage, float deltaTime);
	
	// ------------- DELEGATORS ------------------------
	
	public SynchronizedPriorityQueue<T> getPackets() {
		return packetManager.getPackets();
	}
	
	public T nextPacket() {
		return packetManager.nextPacket();
	}
	
	public PriorityQueue<T> consumePackets() {
		return packetManager.consumePackets();
	}
	
	public boolean hasPackets() {
		return packetManager.hasPackets();
	}
	
	public PacketPair<T> getPacketsBeforeAndAfter(long time) {
		return packetManager.getPacketsBeforeAndAfter(time);
	}

}
