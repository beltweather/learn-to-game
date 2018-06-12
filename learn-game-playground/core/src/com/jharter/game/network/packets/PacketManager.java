package com.jharter.game.network.packets;

import com.badlogic.gdx.ai.msg.PriorityQueue;
import com.esotericsoftware.kryonet.Connection;
import com.jharter.game.collections.SynchronizedPriorityQueue;
import com.jharter.game.network.GameEndPoint;

public class PacketManager<T extends Packet<T>> {

	private SynchronizedPriorityQueue<T> packets;
	private PacketPair<T> pair = new PacketPair<T>();
	
	public PacketManager() {
		this.packets = new SynchronizedPriorityQueue<T>();
	}
	
	public SynchronizedPriorityQueue<T> getPackets() {
		return packets;
	}
	
	public T nextPacket() {
		return packets.poll();
	}
	
	public PriorityQueue<T> consumePackets() {
		return packets.consume();
	}
	
	public boolean hasPackets() {
		return packets.size() > 0;
	}
	
	public PacketPair<T> getPacketsBeforeAndAfter(long currentTime) {
		if(packets.size() < 2) {
			pair.pastPacket = null;
			pair.futurePacket = null;
    		return null;
    	}
    	
    	T pastPacket = null;
    	T futurePacket = null;
    	T packet = packets.peek();
		while(packet != null && futurePacket == null) {
			if(packet.sendTime <= currentTime) {
				pastPacket = packets.poll();
				packet = packets.peek();
			} else {
				futurePacket = packet;
			}
		}
		if(pastPacket == null || futurePacket == null || (futurePacket.sendTime - pastPacket.sendTime) <= 0) {
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
		pair.currentTime = currentTime;
		return pair;
	}
	
	public void received(GameEndPoint server, Connection connection, T packet) {
		packets.add(packet);
	}
	
	public static class PacketPair<T> {
		public T pastPacket, futurePacket;
		public long currentTime;
		
		public PacketPair() {
			this(null, null, -1);
		}
		
		public PacketPair(T pastPacket, T futurePacket, long currentTime) {
			this.pastPacket = pastPacket;
			this.futurePacket = futurePacket;
			this.currentTime = currentTime;
		}
	}
	
}
