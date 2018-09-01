package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.jharter.game.ashley.components.Components.SpriteComp;
import com.jharter.game.ashley.components.Components.TurnTimerComp;
import com.jharter.game.ashley.components.M;
import com.jharter.game.ashley.components.subcomponents.TurnTimer;

@Deprecated
public class RenderTimerSystem extends IteratingSystem {
	
	private ShapeRenderer shapeRenderer;
	private OrthographicCamera camera;

	@SuppressWarnings("unchecked")
	public RenderTimerSystem (OrthographicCamera camera) {
		super(Family.all(SpriteComp.class, TurnTimerComp.class).get());
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
		TurnTimer t = M.TurnTimerComp.get(entity).turnTimer;
		
		if(t.isStopped()) {
			return;
		}
		
		int degrees = Math.round(360 * t.accumulator / t.maxTurnTimeSec);
		if(degrees < 200) {
			//return;
		}
		
		SpriteComp s = M.SpriteComp.get(entity);
		
		//shapeRenderer.begin(ShapeType.Line);
		//shapeRenderer.circle(p.position.x, p.position.y, s.width / 2);
		//shapeRenderer.end();
		
		Color color = shapeRenderer.getColor();
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(0, 0, 0, (float) Math.pow(degrees / 360f, 2));
		shapeRenderer.arc(s.position.x, s.position.y, s.width / 2, 90, degrees, 2*degrees + 1);
		shapeRenderer.end();
		shapeRenderer.setColor(color);
		
		//batch.draw(v.region, p.position.x, p.position.y, 0, 0, s.width, s.height, s.scale.x, s.scale.y, p.angleDegrees);
	}
	
}