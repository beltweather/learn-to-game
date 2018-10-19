package com.jharter.game.render;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.jharter.game.ecs.entities.EntityHandler;
import com.jharter.game.ecs.entities.IEntityHandler;

public abstract class ShapeRenderMethod extends EntityHandler {

	public ShapeRenderMethod(IEntityHandler handler) {
		super(handler);
	}

	public abstract void render(ShapeRenderer shapeRenderer, Entity entity, float deltaTime);

	public void enableOpacity() {
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
	}

	public void disableOpacity() {
		Gdx.gl.glDisable(GL20.GL_BLEND);
	}

	public void setLineWidth(int width) {
		Gdx.gl20.glLineWidth(width);
	}

	public void resetLineWidth() {
		setLineWidth(1);
	}

}
