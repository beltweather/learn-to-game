package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jharter.game.ashley.components.Components.InvisibleComp;
import com.jharter.game.ashley.components.Components.PositionComp;
import com.jharter.game.ashley.components.Components.TextureComp;
import com.jharter.game.ashley.components.Components.TileComp;
import com.jharter.game.ashley.components.Mapper;

public class RenderTilesSystem extends IteratingSystem {
	
	private SpriteBatch batch;
	private OrthographicCamera camera;

	@SuppressWarnings("unchecked")
	public RenderTilesSystem (OrthographicCamera camera) {
		super(Family.all(TileComp.class, PositionComp.class, TextureComp.class).exclude(InvisibleComp.class).get());
		this.camera = camera;
		this.batch = new SpriteBatch();
	}

	@Override
	public void update (float deltaTime) {
		batch.begin();
		batch.setProjectionMatrix(camera.combined);
		super.update(deltaTime);
		batch.end();
	}
	
	@Override
	public void processEntity(Entity entity, float deltaTime) {
		PositionComp p = Mapper.PositionComp.get(entity);
		TextureComp v = Mapper.TextureComp.get(entity);
		TileComp t = Mapper.TileComp.get(entity);
		
		if(v.region != null) {
			batch.draw(v.region, p.position.x, p.position.y);
		} else if(v.defaultRegion != null) {
			batch.draw(v.defaultRegion, p.position.x, p.position.y);
		}
		if(t.secondaryTexture != null) {
			batch.draw(t.secondaryTexture, p.position.x, p.position.y);
		}
	}
			
}