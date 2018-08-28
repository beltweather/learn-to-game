package com.jharter.game.ashley.systems.network.server;

import com.badlogic.ashley.core.Entity;
import com.jharter.game.ashley.components.Components.BattleAvatarComp;
import com.jharter.game.ashley.components.Components.SpriteComp;
import com.jharter.game.ashley.components.Mapper;
import com.jharter.game.ashley.systems.network.ConsumingPacketSystem;
import com.jharter.game.network.endpoints.GameNetwork.AddPlayer;
import com.jharter.game.network.endpoints.GameServer;
import com.jharter.game.network.packets.Packets.AddPlayersPacket;
import com.jharter.game.network.packets.Packets.RequestEntityPacket;
import com.jharter.game.stages.GameStage;
import com.jharter.game.util.Sys;
import com.jharter.game.util.id.ID;

public class ServerRequestEntityPacketSystem extends ConsumingPacketSystem<GameServer, RequestEntityPacket> {

	public ServerRequestEntityPacketSystem(GameStage stage, GameServer server) {
		super(stage, server);
	}

	@Override
	public void update(GameServer server, GameStage stage, float deltaTime, RequestEntityPacket request) {
		ID id = request.id;
		Entity entity = Mapper.Entity.get(id); 
		if(entity == null) {
			Sys.err.println("Requested entity with id " + id + " but none exists!");
			return;
		}
		
		// XXX Right now just add players
		BattleAvatarComp playerComp = Mapper.BattleAvatarComp.get(entity);
		if(playerComp != null) {
			AddPlayersPacket addPlayers = AddPlayersPacket.newInstance();
			AddPlayer addPlayer = new AddPlayer();
			SpriteComp s = Mapper.SpriteComp.get(entity);
			addPlayer.id = Mapper.IDComp.get(entity).id;
			addPlayer.x = s.position.x;
			addPlayer.y = s.position.y;
			addPlayer.z = s.position.z;
			addPlayers.players.add(addPlayer);
			Sys.out.println("Server sending " + addPlayers.players.size + " players to all clients.");
			server.sendTo(request.connectionId, addPlayers);
		}
	}

	@Override
	public Class<RequestEntityPacket> getPacketClass() {
		return RequestEntityPacket.class;
	}

}
