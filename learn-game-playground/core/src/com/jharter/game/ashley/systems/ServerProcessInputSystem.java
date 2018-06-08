package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.jharter.game.network.GameServer;

public class ServerProcessInputSystem extends EntitySystem {
	
	private GameServer server;
	
	public ServerProcessInputSystem(GameServer server) {
		this.server = server;
	}
	
	@Override
	public void update(float deltaTime) {
		
	}
	
}
