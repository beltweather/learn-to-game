package com.jharter.game.network.packets;

import com.esotericsoftware.kryonet.Connection;
import com.jharter.game.ashley.systems.PacketSystem;
import com.jharter.game.collections.SynchronizedPriorityQueue;
import com.jharter.game.network.GameClient;
import com.jharter.game.network.GameServer;

public abstract class PacketManager<T extends Packet<T>> {

	private SynchronizedPriorityQueue<T> packets;
	private PacketPair<T> pair = new PacketPair<T>();
	
	public PacketManager() {
		this.packets = new SynchronizedPriorityQueue<T>();
	}
	
	public SynchronizedPriorityQueue<T> getPackets() {
		return packets;
	}
	
	public PacketPair<T> getPacketsBeforeAndAfter(long time) {
		if(packets.size() < 2) {
			pair.pastPacket = null;
			pair.futurePacket = null;
    		return null;
    	}
    	
    	T pastPacket = null;
    	T futurePacket = null;
    	T packet = packets.peek();
		while(packet != null && futurePacket == null) {
			if(packet.time <= time) {
				pastPacket = packets.poll();
				packet = packets.peek();
			} else {
				futurePacket = packet;
			}
		}
		if(pastPacket == null || futurePacket == null || (futurePacket.time - pastPacket.time) <= 0) {
			// Do a little cleanup here in case we just haven't received a future packet yet.
			// In general, this should be a rare occurance due to our render time calculation.
			if(pastPacket != null && futurePacket == null) {
				packets.add(pastPacket);
			}
			pair.pastPacket = null;
			pair.futurePacket = null;
			return null;
		}
		
		pair.pastPacket = pastPacket;
		pair.futurePacket = futurePacket;
		return pair;
	}
	
	public PacketSystem<T> buildSystem(GameClient client) {
		return new PacketSystem<T>(this, null, client);
	}
	
	public PacketSystem<T> buildSystem(GameServer server) {
		return new PacketSystem<T>(this, server, null);
	}
	
	public abstract void received(GameServer server, Connection connection, T packet);
	public abstract void update(GameServer server, float deltaTime);
	public abstract void received(GameClient client, Connection connection, T packet);
	public abstract void update(GameClient client, float deltaTime);
	
	public static class PacketPair<T> {
		public T pastPacket, futurePacket;
		
		public PacketPair() {
			this(null, null);
		}
		
		public PacketPair(T pastPacket, T futurePacket) {
			this.pastPacket = pastPacket;
			this.futurePacket = futurePacket;
		}
	}
	
}
