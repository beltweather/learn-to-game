package com.jharter.game.stages.impl;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.jharter.game.debug.Debug;
import com.jharter.game.ecs.components.Components.AnimationComp;
import com.jharter.game.ecs.components.Components.InteractComp;
import com.jharter.game.ecs.components.Components.RemoveComp;
import com.jharter.game.ecs.components.Components.SensorComp;
import com.jharter.game.ecs.components.subcomponents.Interaction;
import com.jharter.game.ecs.entities.EntityBuildUtil;
import com.jharter.game.ecs.entities.EntityBuilder;
import com.jharter.game.ecs.systems.AnimationSystem;
import com.jharter.game.ecs.systems.ApproachTargetSystem;
import com.jharter.game.ecs.systems.CleanupInputSystemOld;
import com.jharter.game.ecs.systems.FocusCameraSystem;
import com.jharter.game.ecs.systems.InputMovementSystem;
import com.jharter.game.ecs.systems.InteractSystem;
import com.jharter.game.ecs.systems.RemoveEntitiesSystem;
import com.jharter.game.ecs.systems.RenderEntitiesSystem;
import com.jharter.game.ecs.systems.RenderInitSystem;
import com.jharter.game.ecs.systems.RenderTilesSystem;
import com.jharter.game.ecs.systems.UpdatePhysicsSystem;
import com.jharter.game.ecs.systems.VelocityMovementSystem;
import com.jharter.game.ecs.systems.network.client.ClientAddPlayersPacketSystem;
import com.jharter.game.ecs.systems.network.client.ClientRandomMovementSystem;
import com.jharter.game.ecs.systems.network.client.ClientRemoveEntityPacketSystem;
import com.jharter.game.ecs.systems.network.client.ClientSendInputSystem;
import com.jharter.game.ecs.systems.network.client.ClientSnapshotPacketSystem;
import com.jharter.game.ecs.systems.network.offline.OfflineSelectInputSystem;
import com.jharter.game.ecs.systems.network.server.ServerInputPacketSystem;
import com.jharter.game.ecs.systems.network.server.ServerRegisterPlayerPacketSystem;
import com.jharter.game.ecs.systems.network.server.ServerRequestEntityPacketSystem;
import com.jharter.game.ecs.systems.network.server.ServerSendSnapshotSystem;
import com.jharter.game.network.endpoints.EndPointHelper;
import com.jharter.game.network.endpoints.GameClient;
import com.jharter.game.network.endpoints.GameServer;
import com.jharter.game.stages.GameStage;
import com.jharter.game.util.Sys;
import com.jharter.game.util.id.ID;
import com.jharter.game.util.id.IDUtil;

import uk.co.carelesslabs.Enums.EntityType;
import uk.co.carelesslabs.Media;
import uk.co.carelesslabs.Rumble;
import uk.co.carelesslabs.box2d.Box2DHelper;
import uk.co.carelesslabs.map.Island;

public class TestStageA extends GameStage {
	
	private Island island;
	
	public TestStageA(EndPointHelper endPointHelper) {
		super(endPointHelper);
	}
	
	@Override
	public void addEntities(PooledEngine engine) {
		island = new Island(getBox2DWorld());
		for(Entity e : island.getTileEntities(engine, getBox2DWorld())) {
			engine.addEntity(e);
		}
		
		//String focusId = IDUtil.newID();
		//addFocusEntity(focusId, center.cpy());
	    
		Vector3 center = getEntryPoint();
		center.x += 10;
		//addBird(engine, center.cpy());
	}
	
	private void addBird(PooledEngine engine, Vector3 position) {
		float width = 8;
		float height = 8;
		TextureRegion texture = new TextureRegion(Media.birdWalk, Media.birdWalk.getWidth()/3, Media.birdWalk.getHeight());
		float speed = 5;
		ID id = IDUtil.newID();
		EntityBuilder b = EntityBuildUtil.buildDynamicSprite(engine, id, EntityType.HERO, position, width, height, texture, box2D, BodyType.StaticBody, speed);
		Entity birdEntity = b.Entity();
		b.free();
		
		Body sensor = Box2DHelper.createSensor(getBox2DWorld().world, width, height*.85f, width/2, height/3, position, BodyDef.BodyType.DynamicBody);     
	    SensorComp sensorComp = engine.createComponent(SensorComp.class);
	    sensorComp.sensor = sensor;
	    sensor.setUserData(id);
	    birdEntity.add(sensorComp);
	    
	    InteractComp interactComp = engine.createComponent(InteractComp.class);
	    interactComp.interaction = new Interaction() {
	    	
	    	public void interact(Entity interactor, Entity target) {
	    		RemoveComp removeComp = getCompManager().RemoveComp.get(target);
	    		removeComp.requestRemove = true;
	    		// XXX Debug for now
	    		removeComp.remove = true;
	    		Rumble.rumble(1, .2f);
	    	}
	    	
	    };
	    birdEntity.add(interactComp);
		
		AnimationComp animationComp = engine.createComponent(AnimationComp.class); 
		animationComp.animation = Media.birdWalkAnim;
		animationComp.looping = true;
		birdEntity.add(animationComp);
		engine.addEntity(birdEntity);
	}
	
	@Override
	protected Viewport buildViewport(OrthographicCamera camera) {
    	int displayW = Gdx.graphics.getWidth();
        int displayH = Gdx.graphics.getHeight();
	        
        int h = (int) (displayH/Math.floor(displayH/160));
        int w = (int) (displayW/(displayH/ (displayH/Math.floor(displayH/160))));
    	
    	return new FillViewport(w, h, camera);
    }
	
	@Override
	public EntityBuilder addPlayerEntity(ID id, Vector3 position, boolean focus) {
		float width = 8;
		float height = 8;
		TextureRegion texture = new TextureRegion(Media.hero);
		float speed = 90;
		Sys.out.println("Hero id: " + id);
		EntityBuilder b = EntityBuildUtil.buildPlayerSprite(engine, id, EntityType.HERO, position, width, height, texture, box2D, speed, buildInput(focus));
		if(focus) {
			b.FocusComp();
		}
		engine.addEntity(b.Entity());
		return b;
	}

	@Override
	public Vector3 getEntryPoint() {
		return island.getCentrePosition().cpy();
	}
	
	@Override
	protected PooledEngine buildEngine() {
    	PooledEngine engine = new PooledEngine();
		getCompManager().Entity.addIdListener(engine, getBox2DWorld());
		
		if(endPointHelper.isOffline()) {
			engine.addSystem(new OfflineSelectInputSystem());
		}
		
		if(endPointHelper.isServer()) {
			
			GameServer server = endPointHelper.getServer();
			engine.addSystem(new ServerSendSnapshotSystem(server));
			engine.addSystem(new ServerInputPacketSystem(this, server));
			engine.addSystem(new ServerRegisterPlayerPacketSystem(this, server));
			engine.addSystem(new ServerRequestEntityPacketSystem(this, server));
			
		} else if(endPointHelper.isClient()){

			GameClient client = endPointHelper.getClient();
			if(Debug.RANDOM_MOVEMENT) engine.addSystem(new ClientRandomMovementSystem());
			engine.addSystem(new ClientSendInputSystem(client));
			engine.addSystem(new ClientSnapshotPacketSystem(this, client));
			engine.addSystem(new ClientAddPlayersPacketSystem(this, client));
			engine.addSystem(new ClientRemoveEntityPacketSystem(this, client));
		
		}
		
		engine.addSystem(new UpdatePhysicsSystem(this));
		
		// Used in movement demo
		engine.addSystem(new InputMovementSystem());
		engine.addSystem(new VelocityMovementSystem());
		engine.addSystem(new ApproachTargetSystem());
		engine.addSystem(new InteractSystem());
		
		if(!endPointHelper.isHeadless()) {
			engine.addSystem(new AnimationSystem());
			
			// Used in movement demo
			engine.addSystem(new FocusCameraSystem(getCamera()));
			engine.addSystem(new RenderInitSystem());
			engine.addSystem(new RenderTilesSystem(getCamera()));
			engine.addSystem(new RenderEntitiesSystem(getCamera()));
		}
		
		engine.addSystem(new RemoveEntitiesSystem(endPointHelper.getClient()));
		
		/*if(endPointHelper.isClient()) {
			engine.addSystem(new AddEntitiesSystem(this, endPointHelper.getClient()));
		}*/
		
		engine.addSystem(new CleanupInputSystemOld(this));
		
		return engine;
    }
	
}
