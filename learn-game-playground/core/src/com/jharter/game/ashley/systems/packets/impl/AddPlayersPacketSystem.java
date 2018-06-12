package com.jharter.game.ashley.systems.packets.impl;

import com.badlogic.gdx.math.Vector3;
import com.jharter.game.ashley.entities.EntityUtil;
import com.jharter.game.ashley.systems.packets.ConsumingPacketSystem;
import com.jharter.game.ashley.systems.packets.impl.Packets.AddPlayersPacket;
import com.jharter.game.network.GameClient;
import com.jharter.game.network.GameEndPoint;
import com.jharter.game.network.GameNetwork.AddPlayer;
import com.jharter.game.network.GameServer;
import com.jharter.game.stages.GameStage;

public class AddPlayersPacketSystem extends ConsumingPacketSystem<AddPlayersPacket> {
	
	public AddPlayersPacketSystem(GameStage stage, GameEndPoint endPoint) {
		super(stage, endPoint);
	}

	@Override
	public void update(GameServer server, GameStage stage, float deltaTime, AddPlayersPacket addPlayers) {
		
	}

	@Override
	public void update(GameClient client, GameStage stage, float deltaTime, AddPlayersPacket addPlayers) {
		boolean addedFocus = false;
		for(AddPlayer addPlayer : addPlayers.players) {
			if(EntityUtil.findEntity(addPlayer.id) != null) {
				System.out.println("Client " + client.getId() + " skipping player " + addPlayer.id);
				continue;
			}
			System.out.println("Client " + client.getId() + " adding new player " + addPlayer.id);
			Vector3 pos = new Vector3(addPlayer.x, addPlayer.y, addPlayer.z);
			boolean isFocus = addPlayer.id.equals(client.getPlayerId());
			stage.addPlayerEntity(addPlayer.id, pos, isFocus);
			if(isFocus) {
				addedFocus = true;
			}
		}
		if(addedFocus) {
			stage.activate();
		}
	}

	@Override
	public Class<AddPlayersPacket> getPacketClass() {
		return AddPlayersPacket.class;
	}

}
