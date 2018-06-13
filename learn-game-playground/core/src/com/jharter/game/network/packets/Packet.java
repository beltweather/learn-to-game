package com.jharter.game.network.packets;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.Pools;

public abstract class Packet<T extends Packet<T>> implements Comparable<T>, Poolable  {
	public long sendTime;
	public int sendTick;
	public int connectionId;
	public boolean useTCP = false;
	
	public void free() {
		Pools.get((Class<T>) getClass()).free((T) this);
	}
	
	@Override
	public int compareTo(T packet) {
		return (int) (packet.sendTime % Integer.MAX_VALUE);
	}
}