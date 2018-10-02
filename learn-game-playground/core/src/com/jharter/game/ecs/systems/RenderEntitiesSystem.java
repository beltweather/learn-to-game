package com.jharter.game.ecs.systems;

import java.util.Comparator;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.jharter.game.ecs.components.Components.DisabledComp;
import com.jharter.game.ecs.components.Components.InvisibleComp;
import com.jharter.game.ecs.components.Components.MultiSpriteComp;
import com.jharter.game.ecs.components.Components.ShapeRenderComp;
import com.jharter.game.ecs.components.Components.SpriteComp;
import com.jharter.game.ecs.components.Components.TextureComp;
import com.jharter.game.ecs.components.Components.TileComp;
import com.jharter.game.ecs.systems.boilerplate.GameSortedIteratingSystem;
import com.jharter.game.render.ShapeRenderMethod;

public class RenderEntitiesSystem extends GameSortedIteratingSystem {
	
	private SpriteBatch batch;
	private ShapeRenderer shapeRenderer;
	private OrthographicCamera camera;

	@SuppressWarnings("unchecked")
	public RenderEntitiesSystem (OrthographicCamera camera) {
		super(Family.all(SpriteComp.class).one(TextureComp.class, ShapeRenderComp.class).exclude(InvisibleComp.class, TileComp.class, DisabledComp.class).get());
		setComparator(new PositionSort());
		this.camera = camera;
		this.batch = new SpriteBatch();
		this.shapeRenderer = new ShapeRenderer();
	}

	@Override
	public void update (float deltaTime) {
		Gdx.gl.glClearColor(0, 0, 0, 0);
        //Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT |
						(Gdx.graphics.getBufferFormat().coverageSampling?GL20.GL_COVERAGE_BUFFER_BIT_NV:0));
		shapeRenderer.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.setProjectionMatrix(camera.combined);
		forceSort();
		super.update(deltaTime);
		batch.end();
	}
	
	@Override
	public void processEntity(Entity entity, float deltaTime) {
		TextureComp t = Comp.TextureComp.get(entity);
		ShapeRenderComp r = Comp.ShapeRenderComp.get(entity);
		SpriteComp s = Comp.SpriteComp.get(entity);
		
		if(t != null && t.region == null) {
			t.region = t.defaultRegion;
		}
		
		boolean isTexture = t != null && t.region != null;
		boolean isShapeRender = r != null && r.renderMethod != null;
		if(!isTexture && !isShapeRender) {
			return;
		}
		
		Color c = batch.getColor();
		float offsetX = (Comp.util(s).scaledWidth() - s.width)/2;
		float offsetY = (Comp.util(s).scaledHeight() - s.height)/2;
		float originX = s.width/2;
		float originY = s.height/2;
		
		boolean drawSingle = true;
		if(Comp.MultiSpriteComp.has(entity)) {
			MultiSpriteComp ms = Comp.MultiSpriteComp.get(entity);
			for(int i = 0; i < ms.size; i++) {
				if(isTexture) {
					batchDraw(ms, i, s, t, offsetX, offsetY, originX, originY);
				}
				if(isShapeRender) {
					handleShapeRenderMethod(r.renderMethod, entity, deltaTime);
					//r.renderMethod.render(shapeRenderer, entity, deltaTime);
				}
			}
			drawSingle = ms.drawSingle;
		}
		if(drawSingle) {
			if(isTexture) {
				batchDraw(s, t, offsetX, offsetY, originX, originY);
			}
			if(isShapeRender) {
				handleShapeRenderMethod(r.renderMethod, entity, deltaTime);
				//r.renderMethod.render(shapeRenderer, entity, deltaTime);
			}
		}
		
		if(batch.getColor().a != c.a) {
			batch.setColor(c);
		}
	}
	
	private void handleShapeRenderMethod(ShapeRenderMethod r, Entity entity, float deltaTime) {
		batch.end();
		r.render(shapeRenderer, entity, deltaTime);
		batch.begin();
	}
	
	private void batchDraw(MultiSpriteComp ms, int index, SpriteComp s, TextureComp t, float offsetX, float offsetY, float originX, float originY) {
		Vector3 position = index >= ms.positions.size ? s.position : ms.positions.get(index);
		Vector2 scale = index >= ms.scales.size ? s.scale : ms.scales.get(index);
		float alpha = index >= ms.alphas.size ? s.alpha : ms.alphas.get(index);
		float angleDegrees = index >= ms.anglesDegrees.size ? s.angleDegrees : ms.anglesDegrees.get(index);
		if(ms.reflectAngle) {
			angleDegrees += 180;
		}
		batchDraw(position, scale, alpha, angleDegrees, s.width, s.height, t.region, offsetX, offsetY, originX, originY);
	}
	
	private void batchDraw(SpriteComp s, TextureComp t, float offsetX, float offsetY, float originX, float originY) {
		batchDraw(s.position, s.scale, s.alpha, s.angleDegrees, s.width, s.height, t.region, offsetX, offsetY, originX, originY);
	}
	
	private void batchDraw(Vector3 position, Vector2 scale, float alpha, float angleDegrees, float width, float height, TextureRegion t, float offsetX, float offsetY, float originX, float originY) {
		if(alpha != batch.getColor().a) {
			Color c = batch.getColor();
			batch.setColor(c.r, c.g, c.b, alpha);
		}
		batch.draw(t, round(position.x + offsetX), round(position.y + offsetY), round(originX), round(originY), width, height, scale.x, scale.y, angleDegrees);
	}
	
	private float round(float v) {
		return v; //Math.round(v);
	}
	
	private class PositionSort implements Comparator<Entity> {
		@Override
		public int compare(Entity entityA, Entity entityB) {
			Vector3 posA = Comp.SpriteComp.get(entityA).position;
			Vector3 posB = Comp.SpriteComp.get(entityB).position;
			if(posA.z == posB.z) {
				return (int) (posB.y - posA.y);
			}
			return (int) (posA.z - posB.z);
		}
	}
		
}