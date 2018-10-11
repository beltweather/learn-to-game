package com.jharter.game.ecs.systems;

import com.badlogic.gdx.math.Vector3;
import com.jharter.game.ecs.systems.boilerplate.GameEntitySystem;
import com.jharter.game.network.endpoints.GameClient;
import com.jharter.game.network.endpoints.GameNetwork.AddPlayer;
import com.jharter.game.network.packets.Packets.AddPlayersPacket;
import com.jharter.game.stages.GameStage;
import com.jharter.game.util.Sys;

@Deprecated
public class AddEntitiesSystem extends GameEntitySystem {

	private GameStage stage;
	private GameClient client;
	
	public AddEntitiesSystem(GameStage stage, GameClient client) {
		this.stage = stage;
		this.client = client;
	}
	
	@SuppressWarnings("unused")
	@Override
	public void performUpdate(float deltaTime) {
		AddPlayersPacket addPlayers = null; // client.getAddPlayers();
		if(addPlayers == null) {
			return;
		}
		//client.clearAddPlayers();
		
		boolean addedFocus = false;
		for(AddPlayer addPlayer : addPlayers.players) {
			if(Comp.Entity.get(addPlayer.id) != null) {
				Sys.out.println("Client " + client.getId() + " skipping player " + addPlayer.id);
				continue;
			}
			Sys.out.println("Client " + client.getId() + " adding new player " + addPlayer.id);
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
	
}
