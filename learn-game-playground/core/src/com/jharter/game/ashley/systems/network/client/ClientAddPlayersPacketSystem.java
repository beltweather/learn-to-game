package com.jharter.game.ashley.systems.network.client;

import com.badlogic.gdx.math.Vector3;
import com.jharter.game.ashley.entities.EntityUtil;
import com.jharter.game.ashley.systems.network.ConsumingPacketSystem;
import com.jharter.game.network.endpoints.GameClient;
import com.jharter.game.network.endpoints.GameNetwork.AddPlayer;
import com.jharter.game.network.packets.Packets;
import com.jharter.game.network.packets.Packets.AddPlayersPacket;
import com.jharter.game.stages.GameStage;

public class ClientAddPlayersPacketSystem extends ConsumingPacketSystem<GameClient, AddPlayersPacket> {
	
	public ClientAddPlayersPacketSystem(GameStage stage, GameClient client) {
		super(stage, client);
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
			stage.addPlayerEntity(addPlayer.id, pos, isFocus).free();
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
