package com.jharter.game.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jharter.game.ecs.components.Components.InvisibleTag;
import com.jharter.game.ecs.components.Components.SpriteComp;
import com.jharter.game.ecs.components.Components.TextureComp;
import com.jharter.game.ecs.components.Components.TileComp;
import com.jharter.game.ecs.systems.boilerplate.GameIteratingSystem;

public class RenderTilesSystem extends GameIteratingSystem {
	
	private SpriteBatch batch;
	private OrthographicCamera camera;

	@SuppressWarnings("unchecked")
	public RenderTilesSystem (OrthographicCamera camera) {
		super(Family.all(TileComp.class, SpriteComp.class, TextureComp.class).exclude(InvisibleTag.class).get());
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
		SpriteComp s = Comp.SpriteComp.get(entity);
		TextureComp v = Comp.TextureComp.get(entity);
		TileComp t = Comp.TileComp.get(entity);
		
		if(v.region != null) {
			batch.draw(v.region, s.position.x, s.position.y);
		} else if(v.defaultRegion != null) {
			batch.draw(v.defaultRegion, s.position.x, s.position.y);
		}
		if(t.secondaryTexture != null) {
			batch.draw(t.secondaryTexture, s.position.x, s.position.y);
		}
	}
			
}