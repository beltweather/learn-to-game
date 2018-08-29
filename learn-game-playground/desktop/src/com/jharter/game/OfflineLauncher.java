package com.jharter.game;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.jharter.game.game.GameType;
import com.jharter.game.game.OnlineGame;

public class OfflineLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1280; //800;
		config.height = 720; //1080 + 200; //600;
		config.samples = 4;
		config.fullscreen = false;
		new LwjglApplication(new OnlineGame(GameType.OFFLINE, false), config);
	}
}
