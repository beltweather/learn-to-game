package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

public class RenderInitSystem extends EntitySystem {

	public RenderInitSystem() {}
	
	@Override
	public void update (float deltaTime) {
		Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}
	
}
