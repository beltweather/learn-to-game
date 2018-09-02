package com.jharter.game.render;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.jharter.game.ashley.components.Comp;
import com.jharter.game.ashley.components.Components.SpriteComp;
import com.jharter.game.ashley.components.subcomponents.TurnTimer;

public class TurnTimerRenderMethod extends ShapeRenderMethod {
	
	public TurnTimerRenderMethod() {}
	
	@Override
	public void render(ShapeRenderer shapeRenderer, Entity entity, float deltaTime) {
		TurnTimer t = Comp.TurnTimerComp.get(entity).turnTimer;
		if(t.isStopped()) {
			return;
		}
		SpriteComp s = Comp.SpriteComp.get(entity);
		
		int degrees = Math.round(360 * t.accumulator / t.maxTurnTimeSec);
		
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(0, 0, 0, (float) Math.pow(degrees / 360f, 2));
		shapeRenderer.arc(s.position.x, s.position.y, s.width / 2, 90, degrees, 2*degrees + 1);
		shapeRenderer.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);
	}

}
