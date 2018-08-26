package com.jharter.game.ashley.systems;

import java.util.Comparator;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.jharter.game.ashley.components.Components.DisabledComp;
import com.jharter.game.ashley.components.Components.InvisibleComp;
import com.jharter.game.ashley.components.Components.SpriteComp;
import com.jharter.game.ashley.components.Components.TextureComp;
import com.jharter.game.ashley.components.Components.TileComp;
import com.jharter.game.ashley.components.Mapper;

public class RenderEntitiesSystem extends SortedIteratingSystem {
	
	private SpriteBatch batch;
	private OrthographicCamera camera;

	@SuppressWarnings("unchecked")
	public RenderEntitiesSystem (OrthographicCamera camera) {
		super(Family.all(SpriteComp.class, TextureComp.class).exclude(InvisibleComp.class, TileComp.class, DisabledComp.class).get(), new PositionSort());
		this.camera = camera;
		this.batch = new SpriteBatch();
	}

	@Override
	public void update (float deltaTime) {
		batch.begin();
		batch.setProjectionMatrix(camera.combined);
		forceSort();
		super.update(deltaTime);
		batch.end();
	}
	
	@Override
	public void processEntity(Entity entity, float deltaTime) {
		TextureComp t = Mapper.TextureComp.get(entity);
		SpriteComp s = Mapper.SpriteComp.get(entity);
		boolean hasAlpha = s.alpha != 1f;
		
		if(t.region == null) {
			t.region = t.defaultRegion;
		}
		if(t.region != null) {
			
			Color c = null;
			if(hasAlpha) {
				c = batch.getColor();
				batch.setColor(c.r, c.g, c.b, s.alpha);
			}
			
			if(Mapper.MultiPositionComp.has(entity)) {
				for(Vector3 position : Mapper.MultiPositionComp.get(entity).positions) {
					batch.draw(t.region, position.x, position.y, 0, 0, s.width, s.height, s.scale.x, s.scale.y, s.angleDegrees);
				}
			} else {
				batch.draw(t.region, s.position.x, s.position.y, 0, 0, s.width, s.height, s.scale.x, s.scale.y, s.angleDegrees);
			}
			
			if(hasAlpha) {
				batch.setColor(c);
			}
		}
	}
	
	private static class PositionSort implements Comparator<Entity> {
		private ComponentMapper<SpriteComp> sm = ComponentMapper.getFor(SpriteComp.class);
		
		@Override
		public int compare(Entity entityA, Entity entityB) {
			Vector3 posA = sm.get(entityA).position;
			Vector3 posB = sm.get(entityB).position;
			if(posA.z == posB.z) {
				return (int) (posB.y - posA.y);
			}
			return (int) (posA.z - posB.z);
		}
	}
	
}