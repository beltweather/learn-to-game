package com.jharter.game.ashley.systems;

import java.util.Comparator;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.jharter.game.ashley.components.Components.AlphaComp;
import com.jharter.game.ashley.components.Components.InvisibleComp;
import com.jharter.game.ashley.components.Components.PositionComp;
import com.jharter.game.ashley.components.Components.SizeComp;
import com.jharter.game.ashley.components.Components.TextureComp;
import com.jharter.game.ashley.components.Components.TileComp;
import com.jharter.game.ashley.components.Mapper;

public class RenderEntitiesSystem extends SortedIteratingSystem {
	
	private SpriteBatch batch;
	private OrthographicCamera camera;

	@SuppressWarnings("unchecked")
	public RenderEntitiesSystem (OrthographicCamera camera) {
		super(Family.all(PositionComp.class, TextureComp.class, SizeComp.class).exclude(InvisibleComp.class, TileComp.class).get(), new PositionSort());
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
		PositionComp p = Mapper.PositionComp.get(entity);
		TextureComp v = Mapper.TextureComp.get(entity);
		SizeComp s = Mapper.SizeComp.get(entity);
		AlphaComp a = Mapper.AlphaComp.get(entity);
		boolean hasAlpha = a != null && a.alpha != 1f;
		
		if(v.region == null) {
			v.region = v.defaultRegion;
		}
		if(v.region != null) {
			
			Color c = null;
			if(hasAlpha) {
				c = batch.getColor();
				batch.setColor(c.r, c.g, c.b, a.alpha);
			}
			
			if(Mapper.MultiPositionComp.has(entity)) {
				for(Vector3 position : Mapper.MultiPositionComp.get(entity).positions) {
					batch.draw(v.region, position.x, position.y, 0, 0, s.width, s.height, s.scale.x, s.scale.y, p.angleDegrees);
				}
			} else {
				batch.draw(v.region, p.position.x, p.position.y, 0, 0, s.width, s.height, s.scale.x, s.scale.y, p.angleDegrees);
			}
			
			if(hasAlpha) {
				batch.setColor(c);
			}
		}
	}
	
	private static class PositionSort implements Comparator<Entity> {
		private ComponentMapper<PositionComp> pm = ComponentMapper.getFor(PositionComp.class);
		
		@Override
		public int compare(Entity entityA, Entity entityB) {
			Vector3 posA = pm.get(entityA).position;
			Vector3 posB = pm.get(entityB).position;
			if(posA.z == posB.z) {
				return (int) (posB.y - posA.y);
			}
			return (int) (posA.z - posB.z);
		}
	}
	
}