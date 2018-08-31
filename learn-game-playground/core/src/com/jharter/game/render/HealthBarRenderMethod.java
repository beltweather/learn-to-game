package com.jharter.game.render;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector3;
import com.jharter.game.ashley.components.Components.RelativePositionComp;
import com.jharter.game.ashley.components.Components.SpriteComp;
import com.jharter.game.ashley.components.Components.VitalsComp;
import com.jharter.game.ashley.components.Mapper;

public class HealthBarRenderMethod extends ShapeRenderMethod {

	public HealthBarRenderMethod() {
		
	}

	@Override
	public void render(ShapeRenderer shapeRenderer, Entity entity, float deltaTime) {
		SpriteComp s = Mapper.SpriteComp.get(entity);
		RelativePositionComp r = Mapper.RelativePositionComp.get(entity);
		Entity baselineEntity = Mapper.Entity.get(r.baselineID);
		VitalsComp v = Mapper.VitalsComp.get(baselineEntity);
		
		if(s == null || v == null) {
			return;
		}
		
		Vector3 position = r != null ? r.toPosition(s) : s.position;
		
		float x = position.x;
	    float y = position.y;
		float w = s.scaledWidth();
		float h = s.scaledHeight();
	    
		Color color = shapeRenderer.getColor();
		shapeRenderer.begin(ShapeType.Filled);
	    shapeRenderer.setColor(0.8f, 0, 0, 1f);
		shapeRenderer.rect(x, y, w*(v.health/(float)v.maxHealth), h);
		shapeRenderer.end();
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.setColor(0, 0, 0, 1f);
		shapeRenderer.rect(x, y, w, h);
		shapeRenderer.end();
		shapeRenderer.setColor(color);
	}
	
}
