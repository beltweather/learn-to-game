package com.jharter.game.stages.impl;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.EntityBuilder;
import com.jharter.game.ashley.components.ZoneUtil;
import com.jharter.game.ashley.entities.EntityUtil;
import com.jharter.game.network.endpoints.EndPointHelper;
import com.jharter.game.stages.GameStage;
import com.jharter.game.util.id.ID;

import uk.co.carelesslabs.Enums.EntityType;
import uk.co.carelesslabs.Enums.ZoneType;
import uk.co.carelesslabs.Media;

public class BattleStage extends GameStage {
	
	public static final int CARD_WIDTH = 72;
	public static final int CARD_HEIGHT = 100;
	
	public BattleStage(EndPointHelper endPointHelper) {
		super(endPointHelper);
	}

	@Override
	public void addEntities(PooledEngine engine) {
		EntityBuilder b = EntityUtil.buildBasicEntity(engine, 
													  EntityType.BACKGROUND, 
													  new Vector3(-1920/2,-1080/2,-1), 
													  Media.background);
		engine.addEntity(b.Entity());
		b.free();
		
		// Zones
		b = EntityBuilder.create(engine);
		b.IDComp().id = ZoneUtil.getID(ZoneType.HAND);
		b.ZoneComp().zoneType = ZoneType.HAND;
		b.ZoneComp().rows = 1;
		b.ZoneComp().cols = 4;
		ZoneComp handZone = b.ZoneComp();
		engine.addEntity(b.Entity());
		b.free();
		

		b = EntityBuilder.create(engine);
		b.IDComp().id = ZoneUtil.getID(ZoneType.FRIEND);
		b.ZoneComp().zoneType = ZoneType.FRIEND;
		b.ZoneComp().rows = 4;
		b.ZoneComp().cols = 1;
		ZoneComp friendZone = b.ZoneComp();
		engine.addEntity(b.Entity());
		b.free();
		
		b = EntityBuilder.create(engine);
		b.IDComp().id = ZoneUtil.getID(ZoneType.ENEMY);
		b.ZoneComp().zoneType = ZoneType.ENEMY;
		b.ZoneComp().rows = 1;
		b.ZoneComp().cols = 1;
		ZoneComp enemyZone = b.ZoneComp();
		engine.addEntity(b.Entity());
		b.free();
		
		// Cards
		
		b = EntityUtil.buildBasicEntity(engine, 
										EntityType.CARD, 
										new Vector3(-700,-475,0), 
										Media.island);
		handZone.add(b);
		engine.addEntity(b.Entity());
		b.free();
		
		b = EntityUtil.buildBasicEntity(engine, 
				EntityType.CARD, 
				new Vector3(-450,-475,0), 
				Media.island);
		handZone.add(b);
		engine.addEntity(b.Entity());
		b.free();
		
		b = EntityUtil.buildBasicEntity(engine, 
				EntityType.CARD, 
				new Vector3(-200,-475,0), 
				Media.island);
		handZone.add(b);
		engine.addEntity(b.Entity());
		b.free();
		
		b = EntityUtil.buildBasicEntity(engine, 
				EntityType.CARD, 
				new Vector3(50,-475,0), 
				Media.island);
		handZone.add(b);
		engine.addEntity(b.Entity());
		b.free();
		
		// CHARACTERS
		
		b = EntityUtil.buildBasicEntity(engine, 
				  EntityType.CHARACTER, 
				  new Vector3(700,150,0), 
				  Media.ranger);
		friendZone.add(b);
		engine.addEntity(b.Entity());
		b.free();
		
		b = EntityUtil.buildBasicEntity(engine, 
				  EntityType.CHARACTER, 
				  new Vector3(750,15,0), 
				  Media.ranger);
		friendZone.add(b);
		engine.addEntity(b.Entity());
		b.free();
		
		b = EntityUtil.buildBasicEntity(engine, 
				  EntityType.CHARACTER, 
				  new Vector3(700,-120,0), 
				  Media.ranger);
		friendZone.add(b);
		engine.addEntity(b.Entity());
		b.free();
		
		b = EntityUtil.buildBasicEntity(engine, 
				  EntityType.CHARACTER, 
				  new Vector3(750,-255,0), 
				  Media.ranger);
		friendZone.add(b);
		engine.addEntity(b.Entity());
		b.free();
		
		// ENEMIES
		
		b = EntityUtil.buildBasicEntity(engine, 
				  EntityType.ENEMY, 
				  new Vector3(-750,-100,0), 
				  Media.atma);
		enemyZone.add(b);
		b.SizeComp().scale = new Vector2(2f,2f);
		engine.addEntity(b.Entity());
		b.free();
		
	}
	
	@Override
	protected OrthographicCamera buildCamera() {
		OrthographicCamera camera = super.buildCamera();
		camera.zoom = 1f;
		return camera;
	}
	
	@Override
	public EntityBuilder addPlayerEntity(ID id, Vector3 position, boolean focus) {
		EntityBuilder b = EntityUtil.buildBasicEntity(engine, 
				  EntityType.HAND, 
				  new Vector3(-550,-100,1), 
				  Media.handPointDown);
		b.CursorComp();
		b.CursorInputRegulatorComp();
		b.CursorInputComp();
		b.InputComp().input = buildInput(focus);
		b.ZonePositionComp().zoneType = ZoneType.HAND;
		b.ZonePositionComp().row = 0;
		b.ZonePositionComp().col = 0;
		if(focus) {
			b.FocusComp();
		}
		engine.addEntity(b.Entity());
		
		return b;
	}

	@Override
	public Vector3 getEntryPoint() {
		return new Vector3(0,0,0);
	}

}
