package com.jharter.game.stages;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.jharter.game.ashley.components.Components.AnimationComp;
import com.jharter.game.ashley.components.Components.InteractComp;
import com.jharter.game.ashley.components.Components.RemoveComp;
import com.jharter.game.ashley.components.Components.SensorComp;
import com.jharter.game.ashley.components.EntityBuilder;
import com.jharter.game.ashley.components.Mapper;
import com.jharter.game.ashley.entities.EntityUtil;
import com.jharter.game.ashley.interactions.Interaction;
import com.jharter.game.game.GameDescription;
import com.jharter.game.util.id.ID;
import com.jharter.game.util.id.IDUtil;

import uk.co.carelesslabs.Enums.EntityType;
import uk.co.carelesslabs.Media;
import uk.co.carelesslabs.Rumble;
import uk.co.carelesslabs.box2d.Box2DHelper;
import uk.co.carelesslabs.map.Island;

public class TestStageA extends GameStage {
	
	private Island island;
	
	public TestStageA(GameDescription gameDescription) {
		super(gameDescription);
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
		addBird(engine, center.cpy());
	}
	
	private void addBird(PooledEngine engine, Vector3 position) {
		float width = 8;
		float height = 8;
		TextureRegion texture = new TextureRegion(Media.birdWalk, Media.birdWalk.getWidth()/3, Media.birdWalk.getHeight());
		float speed = 5;
		ID id = IDUtil.newID();
		Entity birdEntity = EntityUtil.buildDynamicSprite(engine, id, EntityType.HERO, position, width, height, texture, box2D, BodyType.StaticBody, speed).Entity();
		
		Body sensor = Box2DHelper.createSensor(getBox2DWorld().world, width, height*.85f, width/2, height/3, position, BodyDef.BodyType.DynamicBody);     
	    SensorComp sensorComp = engine.createComponent(SensorComp.class);
	    sensorComp.sensor = sensor;
	    sensor.setUserData(id);
	    birdEntity.add(sensorComp);
	    
	    InteractComp interactComp = engine.createComponent(InteractComp.class);
	    interactComp.interaction = new Interaction() {
	    	
	    	public void interact(Entity interactor, Entity target) {
	    		RemoveComp removeComp = Mapper.RemoveComp.get(target);
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
	public EntityBuilder addPlayerEntity(ID id, Vector3 position, boolean focus) {
		float width = 8;
		float height = 8;
		TextureRegion texture = new TextureRegion(Media.hero);
		float speed = 90;
		System.out.println("Hero id: " + id);
		EntityBuilder b = EntityUtil.buildPlayerSprite(engine, id, EntityType.HERO, position, width, height, texture, box2D, speed, buildInput(focus));
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
	
}
