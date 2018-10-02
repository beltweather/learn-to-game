package com.jharter.game.stages;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.jharter.game.ashley.components.CompManager;
import com.jharter.game.ashley.components.EntityBuilder;
import com.jharter.game.ashley.entities.IEntityFactory;
import com.jharter.game.ashley.systems.boilerplate.CustomEntitySystem;
import com.jharter.game.ashley.util.ToolBox;
import com.jharter.game.control.GameInput;
import com.jharter.game.network.endpoints.EndPointHelper;
import com.jharter.game.util.U;
import com.jharter.game.util.id.ID;
import com.jharter.game.util.id.IDManager;
import com.jharter.game.util.id.IDUtil;

import uk.co.carelesslabs.box2d.Box2DWorld;

public abstract class GameStage implements IEntityFactory {

	private ID id;
	protected OrthographicCamera camera;
	protected Viewport viewport;
	protected PooledEngine engine;
    protected Box2DWorld box2D;
    protected GameInput stageInput;
    protected EndPointHelper endPointHelper;
    protected ToolBox toolBox;
    
	public GameStage(EndPointHelper endPointHelper) {
		this(IDUtil.newID(), endPointHelper);
	}
	
	public GameStage(ID id, EndPointHelper endPointHelper) {
		this.id = id;
		this.endPointHelper = endPointHelper;
		create();
	}
    
	public ID getId() {
		return id;
	}
	
	protected void setId(ID id) {
		this.id = id;
	}
	
	public ToolBox getToolBox() {
		return toolBox;
	}
	
	public IDManager getIDManager() {
		return toolBox.getIDManager();
	}
	
	public CompManager getCompManager() {
		return toolBox.getCompManager();
	}
	
	public EndPointHelper getEndPointHelper() {
		return endPointHelper;
	}
	
	public OrthographicCamera getCamera() {
		return camera;
	}
	
	public Box2DWorld getBox2DWorld() {
		return box2D;
	}
	
	public GameInput getInput() {
		return stageInput;
	}
	
	public PooledEngine getEngine() {
		return engine;
	}
	
    protected void create() {
    	camera = buildCamera();
    	viewport = buildViewport(camera);
    	viewport.apply();
    	camera.position.set(0, 0, 0);
    	
    	box2D = buildBox2DWorld();
        engine = buildEngine();
        toolBox = new ToolBox(engine);
        toolBox.getCompManager().Entity.addIdListener(engine, getBox2DWorld());
        addManagers(engine);
        addEntities(engine);
    }
    
    private void addManagers(PooledEngine engine) {
		for(EntitySystem system : engine.getSystems()) {
			if(system instanceof CustomEntitySystem) {
				((CustomEntitySystem) system).setToolBox(toolBox);
			}
		}
	}
    
	public GameInput buildInput(boolean active) {
		int displayW = Gdx.graphics.getWidth();
    	int displayH = Gdx.graphics.getHeight();
    	GameInput input = new GameInput(displayW, displayH, camera);
    	if(active) {
    		stageInput = input;
    	}
    	return input;
	}
	
    protected OrthographicCamera buildCamera() {
    	OrthographicCamera camera = new OrthographicCamera();//getHorizontalPixels(), getVerticalPixels());
        camera.zoom = 1f;
        return camera;
    }
    
    protected Viewport buildViewport(OrthographicCamera camera) {
    	int displayW = Gdx.graphics.getWidth();
        int displayH = Gdx.graphics.getHeight();
    	//int verticalPixels = getVerticalPixels();
        
        //int h = 1080; //(int) (displayH/Math.floor(displayH/verticalPixels));
        //int w = 1920; //(int) (displayW/(displayH/ (displayH/Math.floor(displayH/verticalPixels))));
    	
    	float worldH = U.WORLD_HEIGHT_IN_UNITS;
    	float ratio = displayW / (float) displayH;
    	float worldW = ratio * worldH;
    	
    	return new FillViewport(worldW, worldH, camera);
    }
    
    public void resize(int width, int height) {
    	viewport.update(width, height);
    	camera.position.set(0, 0, 0);
    }
    
    protected Box2DWorld buildBox2DWorld() {
    	return new Box2DWorld(this);
    }
    
    protected abstract PooledEngine buildEngine();
    
    public abstract void addEntities(PooledEngine engine);
    
    public abstract EntityBuilder addPlayerEntity(ID id, Vector3 position, boolean focus);
    
    public abstract Vector3 getEntryPoint();
    
    public void tick(float deltaTime) {
        engine.update(deltaTime);
    }
    
    public void activate() {
    	if(stageInput != null) {
    		Gdx.input.setInputProcessor(stageInput);
    		Controllers.addListener(stageInput);
    		stageInput.reset();
    	}
    }
    
    public void deactivate() {
    	stageInput.reset();
    }
    
    public void dispose() {
    	box2D.dispose();
    	engine.removeAllEntities();
    }

}
