package com.jharter.game.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.ObjectMap;
import com.jharter.game.stages.GameStage;

public abstract class StageScreen implements Screen {
	
	private ObjectMap<String, GameStage> stages = new ObjectMap<String, GameStage>();
    private GameStage currentStage = null;
    
    public StageScreen() {
    	setStage(create());
    }
    
    public void addStage(GameStage stage) {
    	stages.put(stage.getId(), stage);
    }
    
    public void setStage(String stageId) {
    	setStage(stages.get(stageId));
    }
    
    public void setStage(GameStage stage) {
    	if(currentStage != null) {
    		currentStage.deactivate();
    	}
    	currentStage = stage;
    	currentStage.activate();
    }
    
    /**
     * Implement this to build and add stages to this screen.
     * 
     * @return The default stage that should be used for this screen initially.
     */
    protected abstract GameStage create();

	@Override
	public void show() {
		
	}

	@Override
	public void render(float deltaTime) {
		//System.out.println("fps: " + Gdx.graphics.getFramesPerSecond());
		currentStage.tick(deltaTime);
	}

	@Override
	public void resize(int width, int height) {
		
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
