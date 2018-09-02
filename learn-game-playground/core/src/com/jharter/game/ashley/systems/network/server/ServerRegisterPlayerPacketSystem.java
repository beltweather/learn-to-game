package com.jharter.game.ashley.systems.network.server;

import com.jharter.game.ashley.components.Components.SpriteComp;
import com.jharter.game.ashley.components.Ent;
import com.jharter.game.ashley.components.EntityBuilder;
import com.jharter.game.ashley.systems.network.ConsumingPacketSystem;
import com.jharter.game.network.endpoints.GameNetwork.AddPlayer;
import com.jharter.game.network.endpoints.GameServer;
import com.jharter.game.network.packets.Packets.AddPlayersPacket;
import com.jharter.game.network.packets.Packets.RegisterPlayerPacket;
import com.jharter.game.stages.GameStage;
import com.jharter.game.util.Sys;
import com.jharter.game.util.id.ID;

public class ServerRegisterPlayerPacketSystem extends ConsumingPacketSystem<GameServer, RegisterPlayerPacket> {
	
	public ServerRegisterPlayerPacketSystem(GameStage stage, GameServer server) {
		super(stage, server);
	}

	private boolean hasFocus = false;
	
	@Override
	public void update(GameServer server, GameStage stage, float deltaTime, RegisterPlayerPacket request) {
		ID id = request.id;
		if(Ent.Entity.get(id) != null) {
			Sys.err.println("Requested new player with id " + id + " but they already exist.");
			return;
		}
		
		EntityBuilder b = stage.addPlayerEntity(id, stage.getEntryPoint(), !hasFocus);
		if(!hasFocus) {
			stage.activate();
		}
		hasFocus = true;
		
		AddPlayersPacket addPlayers = AddPlayersPacket.newInstance();
		//for(Entity player : stage.getEngine().getEntitiesFor(Family.all(PlayerComp.class, IDComp.class, PositionComp.class).get())) {
			AddPlayer addPlayer = new AddPlayer();
			SpriteComp s = b.SpriteComp(); //Mapper.PositionComp.get(player);
			addPlayer.id = b.IDComp().id; //Mapper.IDComp.get(player).id;
			addPlayer.x = s.position.x;
			addPlayer.y = s.position.y;
			addPlayer.z = s.position.z;
			addPlayers.players.add(addPlayer);
		//}
		Sys.out.println("Server sending " + addPlayers.players.size + " players to all clients.");
		server.sendToAll(addPlayers);
		
		b.free();
	}

	@Override
	public Class<RegisterPlayerPacket> getPacketClass() {
		return RegisterPlayerPacket.class;
	}

}
