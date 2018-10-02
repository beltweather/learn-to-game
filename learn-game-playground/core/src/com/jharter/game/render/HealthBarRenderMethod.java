package com.jharter.game.render;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.jharter.game.ecs.components.Components.SpriteComp;
import com.jharter.game.ecs.components.Components.VitalsComp;
import com.jharter.game.ecs.entities.IEntityHandler;

public class HealthBarRenderMethod extends ShapeRenderMethod {

	public HealthBarRenderMethod(IEntityHandler handler) {
		super(handler);
	}

	@Override
	public void render(ShapeRenderer shapeRenderer, Entity entity, float deltaTime) {
		SpriteComp s = Comp.SpriteComp.get(entity);
		VitalsComp v = Comp.VitalsComp.get(Comp.Entity.get(s.relativePositionRules.getRelativeToID()));
		
		if(s == null || v == null) {
			return;
		}
		
		float x = s.position.x;
	    float y = s.position.y;
		float w = Comp.util(s).scaledWidth();
		float h = Comp.util(s).scaledHeight();
	    
		Color color = shapeRenderer.getColor();
		shapeRenderer.begin(ShapeType.Filled);
	    shapeRenderer.setColor(1f, 1f, 1f, 1f);
		shapeRenderer.rect(x, y, w, h);
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
