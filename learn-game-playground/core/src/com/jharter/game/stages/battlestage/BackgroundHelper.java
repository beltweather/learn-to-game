package com.jharter.game.stages.battlestage;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;
import com.jharter.game.ashley.components.EntityBuilder;
import com.jharter.game.ashley.entities.EntityUtil;

import uk.co.carelesslabs.Enums.EntityType;

public class BackgroundHelper {

	private BackgroundHelper() {}
	
	public static void addBackground(PooledEngine engine, Texture texture) {
		EntityBuilder b = EntityUtil.buildBasicEntity(engine, 
				  EntityType.BACKGROUND, 
				  new Vector3(-1920/2,-1080/2,-1), 
				  texture);
		engine.addEntity(b.Entity());
		b.free();
	}
	
}
