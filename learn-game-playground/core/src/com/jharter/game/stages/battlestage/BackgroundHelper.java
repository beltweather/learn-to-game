package com.jharter.game.stages.battlestage;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;
import com.jharter.game.ashley.components.EntityBuilder;
import com.jharter.game.ashley.entities.EntityFactory;
import com.jharter.game.ashley.entities.EntityUtil;
import com.jharter.game.ashley.entities.IEntityFactory;
import com.jharter.game.util.U;

import uk.co.carelesslabs.Enums.EntityType;

public class BackgroundHelper extends EntityFactory {

	public BackgroundHelper(IEntityFactory factory) {
		super(factory);
	}

	public void addBackground(Texture texture) {
		addBackground(texture, -1);
	}
		
	public void addBackground(Texture texture, float z) {
		EntityBuilder b = EntityUtil.buildBasicEntity(getEngine(), 
				  EntityType.BACKGROUND, 
				  new Vector3(U.u12(-80),U.u12(-45),z), // XXX
					 //new Vector3(-1920/2,-1080/2,-1), 
				  texture);
		b.SpriteComp().alpha = 1f;
		getEngine().addEntity(b.Entity());
		b.free();
	}
	
}
