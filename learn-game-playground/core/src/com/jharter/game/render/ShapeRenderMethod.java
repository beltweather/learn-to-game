package com.jharter.game.render;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.jharter.game.ecs.entities.EntityHandler;
import com.jharter.game.ecs.entities.IEntityHandler;

public abstract class ShapeRenderMethod extends EntityHandler {
	
	public ShapeRenderMethod(IEntityHandler handler) {
		super(handler);
	}
	
	public abstract void render(ShapeRenderer shapeRenderer, Entity entity, float deltaTime);
	
}
