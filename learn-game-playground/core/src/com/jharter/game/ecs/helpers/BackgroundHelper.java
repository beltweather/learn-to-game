package com.jharter.game.ecs.helpers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;
import com.jharter.game.ecs.entities.EntityBuildUtil;
import com.jharter.game.ecs.entities.EntityBuilder;
import com.jharter.game.ecs.entities.EntityHandler;
import com.jharter.game.ecs.entities.IEntityHandler;
import com.jharter.game.util.U;

import uk.co.carelesslabs.Enums.EntityType;

public class BackgroundHelper extends EntityHandler {

	public BackgroundHelper(IEntityHandler handler) {
		super(handler);
	}

	public void addBackground(Texture texture) {
		addBackground(texture, -1);
	}
		
	public void addBackground(Texture texture, float z) {
		EntityBuilder b = EntityBuildUtil.buildBasicEntity(getEngine(), 
				  EntityType.BACKGROUND, 
				  new Vector3(U.u12(-80),U.u12(-45),z), // XXX
					 //new Vector3(-1920/2,-1080/2,-1), 
				  texture);
		b.SpriteComp().alpha = 1f;
		getEngine().addEntity(b.Entity());
		b.free();
	}
	
}
