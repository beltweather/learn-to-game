package com.jharter.game.stages;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.jharter.game.ashley.entities.EntityUtil;
import com.jharter.game.ashley.systems.AnimationSystem;
import com.jharter.game.ashley.systems.CameraSystem;
import com.jharter.game.ashley.systems.CleanupInputSystem;
import com.jharter.game.ashley.systems.CollisionSystem;
import com.jharter.game.ashley.systems.ControlledMovementSystem;
import com.jharter.game.ashley.systems.InteractSystem;
import com.jharter.game.ashley.systems.MoveTowardTargetSystem;
import com.jharter.game.ashley.systems.RemoveEntitiesSystem;
import com.jharter.game.ashley.systems.RenderEntitiesSystem;
import com.jharter.game.ashley.systems.RenderInitSystem;
import com.jharter.game.ashley.systems.RenderTilesSystem;
import com.jharter.game.ashley.systems.SelectInputSystem;
import com.jharter.game.ashley.systems.UpdatePhysicsSystem;
import com.jharter.game.ashley.systems.VelocityMovementSystem;
import com.jharter.game.control.Input;
import com.jharter.game.util.IDUtil;

import uk.co.carelesslabs.box2d.Box2DWorld;

public abstract class GameStage {

	private String id;
	protected OrthographicCamera camera;
	protected PooledEngine engine;
    protected Box2DWorld box2D;
    protected Input stageInput;
    
	public GameStage() {
		this(IDUtil.newID());
	}
	
	public GameStage(String id) {
		this.id = id;
		create();
	}
    
	public String getId() {
		return id;
	}
	
	protected void setId(String id) {
		this.id = id;
	}
	
	public OrthographicCamera getCamera() {
		return camera;
	}
	
	public Box2DWorld getBox2DWorld() {
		return box2D;
	}
	
	public Input getInput() {
		return stageInput;
	}
	
	public PooledEngine getEngine() {
		return engine;
	}
	
    protected void create() {
    	camera = buildCamera();
        box2D = buildBox2DWorld();
        engine = buildEngine();
        addEntities(engine);
    }
	
	protected Input buildInput(boolean active) {
		int displayW = Gdx.graphics.getWidth();
    	int displayH = Gdx.graphics.getHeight();
    	Input input = new Input(displayW, displayH, camera);
    	if(active) {
    		stageInput = input;
    	}
    	return input;
	}
    
    protected OrthographicCamera buildCamera() {
        int displayW = Gdx.graphics.getWidth();
        int displayH = Gdx.graphics.getHeight();
        // For 800x600 we will get 266*200
        int h = (int) (displayH/Math.floor(displayH/160));
        int w = (int) (displayW/(displayH/ (displayH/Math.floor(displayH/160))));
        OrthographicCamera camera = new OrthographicCamera(w,h);
        camera.zoom = .6f;
        return camera;
    }
    
    protected Box2DWorld buildBox2DWorld() {
    	return new Box2DWorld();
    }
    
    protected PooledEngine buildEngine() {
    	PooledEngine engine = new PooledEngine();
		EntityUtil.addIdListener(engine);
		
		engine.addSystem(new SelectInputSystem(this));
		engine.addSystem(new UpdatePhysicsSystem(this));
		engine.addSystem(new CollisionSystem());
		engine.addSystem(new ControlledMovementSystem());
		engine.addSystem(new VelocityMovementSystem());
		engine.addSystem(new MoveTowardTargetSystem());
		engine.addSystem(new InteractSystem());
		engine.addSystem(new AnimationSystem());
		engine.addSystem(new CameraSystem(getCamera()));
		engine.addSystem(new RenderInitSystem());
		engine.addSystem(new RenderTilesSystem(getCamera()));
		engine.addSystem(new RenderEntitiesSystem(getCamera()));
		engine.addSystem(new RemoveEntitiesSystem(engine, getBox2DWorld()));
		engine.addSystem(new CleanupInputSystem(this));
		
		return engine;
    }
    
    public abstract void addEntities(PooledEngine engine);
    
    public void tick(float deltaTime) {
        engine.update(deltaTime);
    }
    
    public void activate() {
    	if(stageInput != null) {
    		Gdx.input.setInputProcessor(stageInput);
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
