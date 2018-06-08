package com.jharter.game.network.packets;

public class Packet<T extends Packet<T>> implements Comparable<T>  {
	public long time;
	public int tick;
	
	@Override
	public int compareTo(T packet) {
		return (int) (packet.time % Integer.MAX_VALUE);
	}
}