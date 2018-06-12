package com.jharter.game.network.endpoints;

import com.badlogic.gdx.utils.ObjectMap;
import com.esotericsoftware.kryonet.Connection;
import com.jharter.game.network.packets.Packet;
import com.jharter.game.network.packets.PacketManager;
import com.jharter.game.util.id.ID;
import com.jharter.game.util.id.IDGenerator;

public abstract class GameEndPoint {

	protected ID id;
	protected ObjectMap<Class<? extends Packet>, PacketManager<?>> packetManagers = new ObjectMap();
	
	public GameEndPoint() {
		id = IDGenerator.newID();
	}
	
	public ID getId() {
		return id;
	}
	
	@SuppressWarnings("unchecked")
	public void received(GameEndPoint endPoint, Connection connection, Object object) {
		if(object instanceof Packet) {
			Packet<?> packet = (Packet<?>) object;
			if(hasPacketManager(packet.getClass())) {
				packet.connectionId = connection.getID();
				getPacketManager(packet.getClass()).received(endPoint, connection, packet);
			}
		}
	}
	
	public <T extends Packet<T>> void addPacketManager(Class<T> packetClass, PacketManager<?> packetManager) {
		packetManagers.put(packetClass, packetManager);
	}
	
	public <T extends Packet<T>> boolean hasPacketManager(Class<T> packetClass) {
		return packetManagers.containsKey(packetClass);
	}
	
	public <T extends Packet<T>> PacketManager<T> getPacketManager(Class<T> packetClass) {
		if(packetManagers.containsKey(packetClass)) {
			return (PacketManager<T>) packetManagers.get(packetClass);
		}
		return null;
	}
	
	public void maybeFree(Object object) {
		if(object instanceof Packet) {
			((Packet) object).free();
		}
	}
	
}
