package com.jharter.game.network.endpoints;

import com.jharter.game.game.GameType;

public class EndPointHelper {
	
	private GameType type;
	private boolean headless;
	private GameEndPoint endPoint;
	
	public EndPointHelper(GameType type, boolean headless) {
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
		if(isServer()) {
			return (GameServer) endPoint;
		}
		return null;
	}
	
	public GameClient getClient() {
		if(isClient()) {
			return (GameClient) endPoint;
		}
		return null;
	}
	
	public void setEndPoint(GameEndPoint endPoint) {
		this.endPoint = endPoint;
	}
	
}
