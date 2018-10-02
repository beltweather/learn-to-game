package com.jharter.game.render;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.jharter.game.ashley.entities.EntityFactory;
import com.jharter.game.ashley.entities.IEntityFactory;

public abstract class ShapeRenderMethod extends EntityFactory {
	
	public ShapeRenderMethod(IEntityFactory factory) {
		super(factory);
	}
	
	public abstract void render(ShapeRenderer shapeRenderer, Entity entity, float deltaTime);
	
}
