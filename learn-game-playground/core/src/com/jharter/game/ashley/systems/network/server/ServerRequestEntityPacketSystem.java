package com.jharter.game.ashley.systems.network.server;

import com.badlogic.ashley.core.Entity;
import com.jharter.game.ashley.components.Components.PlayerComp;
import com.jharter.game.ashley.components.Components.PositionComp;
import com.jharter.game.ashley.components.Mapper;
import com.jharter.game.ashley.entities.EntityUtil;
import com.jharter.game.ashley.systems.network.ConsumingPacketSystem;
import com.jharter.game.network.endpoints.GameNetwork.AddPlayer;
import com.jharter.game.network.endpoints.GameServer;
import com.jharter.game.network.packets.Packets.AddPlayersPacket;
import com.jharter.game.network.packets.Packets.RequestEntityPacket;
import com.jharter.game.stages.GameStage;
import com.jharter.game.util.id.ID;

public class ServerRequestEntityPacketSystem extends ConsumingPacketSystem<GameServer, RequestEntityPacket> {

	public ServerRequestEntityPacketSystem(GameStage stage, GameServer server) {
		super(stage, server);
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
			server.sendTo(request.connectionId, addPlayers);
		}
	}

	@Override
	public Class<RequestEntityPacket> getPacketClass() {
		return RequestEntityPacket.class;
	}

}
