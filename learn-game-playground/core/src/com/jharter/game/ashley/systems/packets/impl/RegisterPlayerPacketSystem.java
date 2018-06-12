package com.jharter.game.ashley.systems.packets.impl;

import com.jharter.game.ashley.components.Components.PositionComp;
import com.jharter.game.ashley.components.EntityBuilder;
import com.jharter.game.ashley.entities.EntityUtil;
import com.jharter.game.ashley.systems.packets.ConsumingPacketSystem;
import com.jharter.game.ashley.systems.packets.impl.Packets.AddPlayersPacket;
import com.jharter.game.ashley.systems.packets.impl.Packets.RegisterPlayerPacket;
import com.jharter.game.network.GameClient;
import com.jharter.game.network.GameEndPoint;
import com.jharter.game.network.GameNetwork.AddPlayer;
import com.jharter.game.network.GameServer;
import com.jharter.game.stages.GameStage;
import com.jharter.game.util.id.ID;

public class RegisterPlayerPacketSystem extends ConsumingPacketSystem<RegisterPlayerPacket> {
	
	public RegisterPlayerPacketSystem(GameStage stage, GameEndPoint endPoint) {
		super(stage, endPoint);
	}

	private boolean hasFocus = false;
	
	@Override
	public void update(GameServer server, GameStage stage, float deltaTime, RegisterPlayerPacket request) {
		ID id = request.id;
		if(EntityUtil.findEntity(id) != null) {
			System.err.println("Requested new player with id " + id + " but they already exist.");
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
			PositionComp p = b.PositionComp(); //Mapper.PositionComp.get(player);
			addPlayer.id = b.IDComp().id; //Mapper.IDComp.get(player).id;
			addPlayer.x = p.position.x;
			addPlayer.y = p.position.y;
			addPlayer.z = p.position.z;
			addPlayers.players.add(addPlayer);
		//}
		System.out.println("Server sending " + addPlayers.players.size + " players to all clients.");
		server.sendToAllTCP(addPlayers);
	}

	@Override
	public void update(GameClient client, GameStage stage, float deltaTime, RegisterPlayerPacket request) {
		
	}

	@Override
	public Class<RegisterPlayerPacket> getPacketClass() {
		return RegisterPlayerPacket.class;
	}

}
