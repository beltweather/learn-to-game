package com.jharter.game.ecs.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.jharter.game.util.U;

public class RenderWorldGridSystem extends EntitySystem {
	
	private ShapeRenderer shapeRenderer;
	private OrthographicCamera camera;

	@SuppressWarnings("unchecked")
	public RenderWorldGridSystem (OrthographicCamera camera) {
		super();
		this.camera = camera;
		this.shapeRenderer = new ShapeRenderer();
	}

	@Override
	public void update (float deltaTime) {
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		shapeRenderer.setProjectionMatrix(camera.combined);
		float w = camera.viewportWidth;
		float h = camera.viewportHeight;
		shapeRenderer.setColor(0,0,0, 0.25f);
		shapeRenderer.translate(-w/2, -h/2, 0);
		drawGrid(1, 1, w, h, U.u1(1), 0.1f);
		drawGrid(w/2, h/2, w, h, U.u1(3), 0.1f);
		drawGrid(w/8, h/6, w, h, U.u1(3), 0.1f);
		shapeRenderer.translate(w/2, h/2, 0);
		Gdx.gl.glDisable(GL20.GL_BLEND);
	}
	
	private void drawGrid(float colWidth, float colHeight, float w, float h, float thickness, float alpha) {
		shapeRenderer.setColor(0,0,0, alpha);
		shapeRenderer.begin(ShapeType.Filled);
		for(float i = 0; i < w; i += colWidth) {
			shapeRenderer.rect(i, 0, thickness, h);
		}
		for(float i = 0; i < h; i += colHeight) {
			shapeRenderer.rect(0, i, w, thickness);
		}
		shapeRenderer.end();
	}
	
}