package com.jharter.game.render;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public abstract class ShapeRenderMethod {
	
	public ShapeRenderMethod() {}
	
	public abstract void render(ShapeRenderer shapeRenderer, Entity entity, float deltaTime);
	
}
