package com.jharter.game.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.ObjectMap;
import com.jharter.game.network.endpoints.EndPointHelper;
import com.jharter.game.stages.GameStage;
import com.jharter.game.util.id.ID;

public abstract class GameScreen implements Screen {
	
	private ObjectMap<ID, GameStage> stages = new ObjectMap<ID, GameStage>();
    private GameStage currentStage = null;
    private EndPointHelper endPointHelper;
    
    public GameScreen(EndPointHelper endPointHelper) {
    	this.endPointHelper = endPointHelper;
    	setStage(create(endPointHelper));
    }

    public EndPointHelper getEndPointHelper() {
    	return endPointHelper;
    }
    
    public void addStage(GameStage stage) {
    	stages.put(stage.getId(), stage);
    }
    
    public void setStage(ID stageId) {
    	setStage(stages.get(stageId));
    }
    
    public void setStage(GameStage stage) {
    	if(currentStage != null) {
    		currentStage.deactivate();
    	}
    	currentStage = stage;
    	currentStage.activate();
    }
    
    public GameStage getStage() {
    	return currentStage;
    }
    
    /**
     * Implement this to build and add stages to this screen.
     * 
     * @return The default stage that should be used for this screen initially.
     */
    protected abstract GameStage create(EndPointHelper endPointHelper);

	@Override
	public void show() {
		
	}

	@Override
	public void render(float deltaTime) {
		//Sys.out.println("fps: " + Gdx.graphics.getFramesPerSecond());
		currentStage.tick(deltaTime);
	}

	@Override
	public void resize(int width, int height) {
		if(currentStage != null) {
			currentStage.resize(width, height);
		}
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void hide() {
		
	}

	@Override
	public void dispose() {
		for(GameStage stage : stages.values()) {
    		stage.dispose();
    	}
    	stages.clear();
	}

}
