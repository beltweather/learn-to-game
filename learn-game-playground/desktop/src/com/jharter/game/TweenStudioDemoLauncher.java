package com.jharter.game;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.jharter.game.tween.tweenstudio.demo.App;

public class TweenStudioDemoLauncher {
	
	 public static void main(String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.fullscreen = false;
		config.width = 320;
		config.height = 480; 
		new LwjglApplication(new App(), config);
	 }
	
}
