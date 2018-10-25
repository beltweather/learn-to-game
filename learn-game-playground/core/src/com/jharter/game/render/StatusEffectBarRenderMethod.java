package com.jharter.game.render;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.jharter.game.ecs.components.Components.SpriteComp;
import com.jharter.game.ecs.components.subcomponents.StatusEffects;
import com.jharter.game.ecs.entities.IEntityHandler;

import uk.co.carelesslabs.Enums.StatusEffectType;

public class StatusEffectBarRenderMethod extends ShapeRenderMethod {

	public StatusEffectBarRenderMethod(IEntityHandler handler) {
		super(handler);
	}

	@Override
	public void render(ShapeRenderer shapeRenderer, Entity entity, float deltaTime) {
		SpriteComp s = Comp.SpriteComp.get(entity);
		Entity owner = Comp.Entity.get(s.relativePositionRules.getRelativeToID());
		StatusEffects effects = getStatusEffects(owner);

		if(s == null || effects == null || effects.types.p().size == 0) {
			return;
		}

		int size = Math.min(effects.types.hasPending() ? effects.types.p().size : effects.types.v().size, effects.maxEffects);
		float x = s.position.x;
	    float y = s.position.y;
		float w = Comp.util(s).scaledWidth();
		float h = Comp.util(s).scaledHeight();
		float r = Math.min(w, h)/2f;
		float degrees = 360f / effects.maxEffects;
		int segments = (int) (2*degrees + 1);
		float start = 90;
		int actualCount = effects.types.v().size;

		Color color = shapeRenderer.getColor();
		enableOpacity();
		shapeRenderer.begin(ShapeType.Filled);
		for(int i = 0; i < size; i++) {
			StatusEffectType type = effects.types.p().get(i);
			Color c = type.getColor();
			shapeRenderer.setColor(c.r, c.g, c.b, i < actualCount ? 1f : 0.25f);
			shapeRenderer.arc(x, y, r, start + degrees*i, degrees, segments);
		}
		shapeRenderer.end();

		setLineWidth(2);
		shapeRenderer.begin(ShapeType.Line);
		for(int i = 0; i < size; i++) {
			shapeRenderer.setColor(Color.WHITE);
			shapeRenderer.arc(x, y, r, start + degrees*i, degrees, segments);
		}
		shapeRenderer.end();
		resetLineWidth();
		shapeRenderer.setColor(color);
		shapeRenderer.end();
		disableOpacity();
	}

	private StatusEffects getStatusEffects(Entity entity) {
		return Comp.StatusEffectsComp.get(entity).effects;
	}

}
