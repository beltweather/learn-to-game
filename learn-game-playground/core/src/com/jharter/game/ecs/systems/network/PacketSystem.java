package com.jharter.game.ecs.systems.network;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.msg.PriorityQueue;
import com.jharter.game.ecs.systems.boilerplate.GameEntitySystem;
import com.jharter.game.network.endpoints.GameEndPoint;
import com.jharter.game.network.packets.Packet;
import com.jharter.game.network.packets.PacketManager;
import com.jharter.game.network.packets.PacketManager.PacketPair;
import com.jharter.game.stages.GameStage;
import com.jharter.game.util.collections.SynchronizedPriorityQueue;
import com.jharter.game.util.id.ID;

public abstract class PacketSystem<EP extends GameEndPoint, T extends Packet<T>> extends GameEntitySystem {

	protected PacketManager<T> packetManager;
	protected EP endPoint;
	protected GameStage stage;
	
	public PacketSystem(GameStage stage, EP endPoint) {
		this.stage = stage;
		this.packetManager = new PacketManager<T>();
		this.endPoint = endPoint;
		if(endPoint != null) {
			endPoint.addPacketManager(getPacketClass(), packetManager);
		}
	}
	
	public abstract Class<T> getPacketClass();
	
	public PacketManager<T> getPacketManager() {
		return packetManager;
	}
	
	@Override
	public void performUpdate(float deltaTime) {
		if(!packetManager.hasPackets()) {
			return;
		}
		update(endPoint, stage, deltaTime);
	}
	
	public abstract void update(EP endPoint, GameStage stage, float deltaTime);
	
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
	
	public Entity findEntity(ID entityId) {
		return Comp.Entity.get(entityId);
	}

}
