package com.jharter.game.ecs.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

public class RenderInitSystem extends EntitySystem {

	public RenderInitSystem() {}
	
	@Override
	public void update (float deltaTime) {
		Gdx.gl.glClearColor(0, 0, 0, 0);
        //Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT |
						(Gdx.graphics.getBufferFormat().coverageSampling?GL20.GL_COVERAGE_BUFFER_BIT_NV:0));
	}
	
}
