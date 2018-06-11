package com.jharter.game.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.ObjectMap;
import com.jharter.game.game.GameDescription;
import com.jharter.game.stages.GameStage;
import com.jharter.game.util.id.ID;

public abstract class StageScreen implements Screen {
	
	private ObjectMap<ID, GameStage> stages = new ObjectMap<ID, GameStage>();
    private GameStage currentStage = null;
    private GameDescription gameDescription;
    
    public StageScreen(GameDescription gameDescription) {
    	this.gameDescription = gameDescription;
    	setStage(create(gameDescription));
    }

    public GameDescription getGameDescription() {
    	return gameDescription;
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
    protected abstract GameStage create(GameDescription gameDescription);

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
