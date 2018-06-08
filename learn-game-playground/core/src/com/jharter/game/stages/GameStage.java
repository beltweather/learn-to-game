package com.jharter.game.stages;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.jharter.game.ashley.components.EntityBuilder;
import com.jharter.game.ashley.entities.EntityUtil;
import com.jharter.game.ashley.systems.AddEntitiesSystem;
import com.jharter.game.ashley.systems.AnimationSystem;
import com.jharter.game.ashley.systems.ApproachTargetSystem;
import com.jharter.game.ashley.systems.CameraSystem;
import com.jharter.game.ashley.systems.CleanupInputSystem;
import com.jharter.game.ashley.systems.ClientSendInputSystem;
import com.jharter.game.ashley.systems.CollisionSystem;
import com.jharter.game.ashley.systems.InputMovementSystem;
import com.jharter.game.ashley.systems.InteractSystem;
import com.jharter.game.ashley.systems.RemoveEntitiesSystem;
import com.jharter.game.ashley.systems.RenderEntitiesSystem;
import com.jharter.game.ashley.systems.RenderInitSystem;
import com.jharter.game.ashley.systems.RenderTilesSystem;
import com.jharter.game.ashley.systems.SelectInputSystem;
import com.jharter.game.ashley.systems.ServerProcessInputSystem;
import com.jharter.game.ashley.systems.ServerSendSnapshotSystem;
import com.jharter.game.ashley.systems.UpdatePhysicsSystem;
import com.jharter.game.ashley.systems.VelocityMovementSystem;
import com.jharter.game.control.Input;
import com.jharter.game.game.GameDescription;
import com.jharter.game.network.GameClient;
import com.jharter.game.network.GameServer;
import com.jharter.game.util.IDUtil;

import uk.co.carelesslabs.box2d.Box2DWorld;

public abstract class GameStage {

	private String id;
	protected OrthographicCamera camera;
	protected PooledEngine engine;
    protected Box2DWorld box2D;
    protected Input stageInput;
    protected GameDescription gameDescription;
    
	public GameStage(GameDescription gameDescription) {
		this(IDUtil.newID(), gameDescription);
	}
	
	public GameStage(String id, GameDescription gameDescription) {
		this.id = id;
		this.gameDescription = gameDescription;
		create();
	}
    
	public String getId() {
		return id;
	}
	
	protected void setId(String id) {
		this.id = id;
	}
	
	public GameDescription getGameDescription() {
		return gameDescription;
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
	
	public Input buildInput(boolean active) {
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
		
		if(gameDescription.isOffline()) {
			engine.addSystem(new SelectInputSystem());
		}
		
		if(gameDescription.isServer()) {
			
			GameServer server = gameDescription.getServer();
			engine.addSystem(new ServerSendSnapshotSystem(server));
			engine.addSystem(new ServerProcessInputSystem(server));
			
			//server.addPacketSystemsToEngine(engine);
			
		} else if(gameDescription.isClient()){

			GameClient client = gameDescription.getClient();
			engine.addSystem(new ClientSendInputSystem(client));
			client.addPacketSystemsToEngine(engine);
			
		}
		
		engine.addSystem(new UpdatePhysicsSystem(this));
		engine.addSystem(new CollisionSystem());
		engine.addSystem(new InputMovementSystem());
		engine.addSystem(new VelocityMovementSystem());
		engine.addSystem(new ApproachTargetSystem());
		engine.addSystem(new InteractSystem());
		
		if(!gameDescription.isHeadless()) {
			engine.addSystem(new AnimationSystem());
			engine.addSystem(new CameraSystem(getCamera()));
			engine.addSystem(new RenderInitSystem());
			engine.addSystem(new RenderTilesSystem(getCamera()));
			engine.addSystem(new RenderEntitiesSystem(getCamera()));
		}
		
		engine.addSystem(new RemoveEntitiesSystem(engine, getBox2DWorld()));
		
		if(gameDescription.isClient()) {
			engine.addSystem(new AddEntitiesSystem(this, gameDescription.getClient()));
		}
		
		engine.addSystem(new CleanupInputSystem(this));
		
		return engine;
    }
    
    public abstract void addEntities(PooledEngine engine);
    
    public abstract EntityBuilder addPlayerEntity(String id, Vector3 position, boolean focus);
    
    public abstract Vector3 getEntryPoint();
    
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
