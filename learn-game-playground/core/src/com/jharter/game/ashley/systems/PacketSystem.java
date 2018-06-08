package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.jharter.game.game.GameType;
import com.jharter.game.network.GameClient;
import com.jharter.game.network.GameServer;
import com.jharter.game.network.packets.Packet;
import com.jharter.game.network.packets.PacketManager;

public class PacketSystem<T extends Packet<T>> extends EntitySystem {

	private PacketManager<T> packetManager;
	private GameServer server;
	private GameClient client;
	
	public PacketSystem(PacketManager<T> packetManager, GameServer server, GameClient client) {
		this.packetManager = packetManager;
		this.server = server;
		this.client = client;
	}
	
	@Override
	public void update(float deltaTime) {
		if(server != null) {
			packetManager.update(server, deltaTime);
		} else if(client != null) {
			packetManager.update(client, deltaTime);
		}
	}
	
}
