package com.jharter.game.ashley.systems.packets.impl;

import com.badlogic.ashley.core.Entity;
import com.jharter.game.ashley.components.Components.PlayerComp;
import com.jharter.game.ashley.components.Components.PositionComp;
import com.jharter.game.ashley.components.Mapper;
import com.jharter.game.ashley.entities.EntityUtil;
import com.jharter.game.ashley.systems.packets.ConsumingPacketSystem;
import com.jharter.game.ashley.systems.packets.impl.Packets.AddPlayersPacket;
import com.jharter.game.ashley.systems.packets.impl.Packets.RequestEntityPacket;
import com.jharter.game.network.GameClient;
import com.jharter.game.network.GameEndPoint;
import com.jharter.game.network.GameNetwork.AddPlayer;
import com.jharter.game.network.GameServer;
import com.jharter.game.stages.GameStage;
import com.jharter.game.util.id.ID;

public class RequestEntityPacketSystem extends ConsumingPacketSystem<RequestEntityPacket> {

	public RequestEntityPacketSystem(GameStage stage, GameEndPoint endPoint) {
		super(stage, endPoint);
	}

	@Override
	public void update(GameServer server, GameStage stage, float deltaTime, RequestEntityPacket request) {
		ID id = request.id;
		Entity entity = EntityUtil.findEntity(id); 
		if(entity == null) {
			System.err.println("Requested entity with id " + id + " but none exists!");
			return;
		}
		
		// XXX Right now just add players
		PlayerComp playerComp = Mapper.PlayerComp.get(entity);
		if(playerComp != null) {
			AddPlayersPacket addPlayers = AddPlayersPacket.newInstance();
			AddPlayer addPlayer = new AddPlayer();
			PositionComp p = Mapper.PositionComp.get(entity);
			addPlayer.id = Mapper.IDComp.get(entity).id;
			addPlayer.x = p.position.x;
			addPlayer.y = p.position.y;
			addPlayer.z = p.position.z;
			addPlayers.players.add(addPlayer);
			System.out.println("Server sending " + addPlayers.players.size + " players to all clients.");
			server.sendToTCP(request.connectionId, addPlayers);
		}
	}

	@Override
	public void update(GameClient client, GameStage stage, float deltaTime, RequestEntityPacket request) {
		
	}

	@Override
	public Class<RequestEntityPacket> getPacketClass() {
		return RequestEntityPacket.class;
	}

}
