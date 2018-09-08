package com.jharter.game;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.jharter.game.game.GameType;
import com.jharter.game.game.OnlineGame;

public class OfflineLauncher {
	
	private static final boolean FULLSCREEN = false; 
	
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		if(FULLSCREEN) {
			config.fullscreen = true;
			config.width = 1920;
			config.height = 1080;
		} else {
			config.fullscreen = false;
			config.width = 1280;
			config.height = 720; 
		}
		config.samples = 4;
		new LwjglApplication(new OnlineGame(GameType.OFFLINE, false), config);
	}
}
