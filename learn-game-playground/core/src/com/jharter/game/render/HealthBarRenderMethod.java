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
		Entity owner = Comp.Entity.get(s.relativePositionRules.getRelativeToID()); 
		VitalsComp v = Comp.VitalsComp.get(owner);
		
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
	    float healthW = w*(v.vitals.health/(float)v.vitals.maxHealth);
		shapeRenderer.rect(x, y, healthW, h);
		int incoming = getIncomingHealthChange(v);
		if(incoming != 0) {
			if(incoming > 0) {
				float incomingW = w*(Math.min(v.vitals.maxHealth - v.vitals.health, incoming)/(float)v.vitals.maxHealth);
				shapeRenderer.setColor(0, 0.8f, 0, 1f);
				shapeRenderer.rect(x + healthW, y, incomingW, h);
			} else {
				float incomingW = w*(Math.min(v.vitals.health, -incoming)/(float)v.vitals.maxHealth);
				shapeRenderer.setColor(0.8f, 0.8f, 0, 1f);
				shapeRenderer.rect(x + healthW - incomingW, y, incomingW, h);
			}
		}
		shapeRenderer.end();
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.setColor(0, 0, 0, 1f);
		shapeRenderer.rect(x, y, w, h);
		shapeRenderer.end();
		shapeRenderer.setColor(color);
	}
	
	private int getIncomingHealthChange(VitalsComp v) {
		return v.pendingVitals.health - v.vitals.health;
	}
	
}
