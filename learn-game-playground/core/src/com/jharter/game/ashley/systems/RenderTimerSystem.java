package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.jharter.game.ashley.components.Components.PositionComp;
import com.jharter.game.ashley.components.Components.SizeComp;
import com.jharter.game.ashley.components.Components.TurnTimerComp;
import com.jharter.game.ashley.components.Mapper;

public class RenderTimerSystem extends IteratingSystem {
	
	private ShapeRenderer shapeRenderer;
	private OrthographicCamera camera;

	@SuppressWarnings("unchecked")
	public RenderTimerSystem (OrthographicCamera camera) {
		super(Family.all(PositionComp.class, SizeComp.class, TurnTimerComp.class).get());
		this.camera = camera;
		this.shapeRenderer = new ShapeRenderer();
	}

	@Override
	public void update (float deltaTime) {
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		shapeRenderer.setProjectionMatrix(camera.combined);
		super.update(deltaTime);
		Gdx.gl.glDisable(GL20.GL_BLEND);
	}
	
	@Override
	public void processEntity(Entity entity, float deltaTime) {
		TurnTimerComp t = Mapper.TurnTimerComp.get(entity);
		
		if(t.isStopped()) {
			return;
		}
		
		int degrees = Math.round(360 * t.accumulator / t.turnTime);
		if(degrees < 200) {
			//return;
		}
		
		PositionComp p = Mapper.PositionComp.get(entity);
		SizeComp s = Mapper.SizeComp.get(entity);
		
		//shapeRenderer.begin(ShapeType.Line);
		//shapeRenderer.circle(p.position.x, p.position.y, s.width / 2);
		//shapeRenderer.end();
		
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(0, 0, 0, (float) Math.pow(degrees / 360f, 2));
		shapeRenderer.arc(p.position.x, p.position.y, s.width / 2, 90, degrees, 2*degrees + 1);
		shapeRenderer.end();
		
		//batch.draw(v.region, p.position.x, p.position.y, 0, 0, s.width, s.height, s.scale.x, s.scale.y, p.angleDegrees);
	}
	
}