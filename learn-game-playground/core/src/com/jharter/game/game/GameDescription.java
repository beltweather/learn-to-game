package com.jharter.game.game;

import com.jharter.game.server.GameClient;
import com.jharter.game.server.GameServer;

public class GameDescription {
	
	private GameType type;
	private boolean headless;
	private GameServer server;
	private GameClient client;
	
	public GameDescription(GameType type, boolean headless) {
		this.type = type;
		this.headless = headless;
	}
	
	public boolean isHeadless() {
		return headless;
	}
	
	public GameType getType() {
		return type;
	}
	
	public boolean isServer() {
		return type == GameType.SERVER;
	}
	
	public boolean isClient() {
		return type == GameType.CLIENT;
	}
	
	public boolean isOffline() {
		return type == GameType.OFFLINE;
	}
	
	public GameServer getServer() {
		return server;
	}
	
	public GameClient getClient() {
		return client;
	}
	
	public void setServer(GameServer server) {
		this.server = server;
	}
	
	public void setClient(GameClient client) {
		this.client = client;
	}
	
}
