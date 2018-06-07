package com.jharter.game.game;

import com.badlogic.gdx.Game;
import com.jharter.game.screens.TestStageScreen;

import uk.co.carelesslabs.Media;

public class SimpleGame extends Game {
	
    @Override
    public void create() {
    	Media.load_assets();
    	setScreen(new TestStageScreen());
    }
    
    @Override
    public void render() {
    	super.render();
    }
	
}
