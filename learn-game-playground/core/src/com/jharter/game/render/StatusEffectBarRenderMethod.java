package com.jharter.game.render;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.jharter.game.ecs.components.Components.SpriteComp;
import com.jharter.game.ecs.components.subcomponents.StatusEffects;
import com.jharter.game.ecs.entities.IEntityHandler;
import com.jharter.game.util.graphics.GraphicsUtil;

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

		if(s == null || effects == null || effects.types.size == 0) {
			return;
		}

		int size = Math.min(effects.types.size, effects.maxEffects);
		float x = s.position.x;
	    float y = s.position.y;
		float w = Comp.util(s).scaledWidth();
		float h = Comp.util(s).scaledHeight();
		float r = Math.min(w, h)/2f;
		float degrees = 360f / effects.maxEffects;
		int segments = (int) (2*degrees + 1);
		float start = 90;

		Color color = shapeRenderer.getColor();
		shapeRenderer.begin(ShapeType.Filled);
		for(int i = 0; i < size; i++) {
			StatusEffectType type = effects.types.get(i);
			shapeRenderer.setColor(type.getColor());
			shapeRenderer.arc(x, y, r, start + degrees*i, degrees, segments);
		}
		shapeRenderer.end();

		Gdx.gl20.glLineWidth(3);
		shapeRenderer.begin(ShapeType.Line);
		for(int i = 0; i < size; i++) {
			shapeRenderer.setColor(Color.WHITE);
			shapeRenderer.arc(x, y, r, start + degrees*i, degrees, segments);
		}
		shapeRenderer.end();
		Gdx.gl20.glLineWidth(1);

		shapeRenderer.setColor(color);
	}

	private StatusEffects getStatusEffects(Entity entity) {
		if(Comp.PendingStatusEffectsComp.has(entity)) {
			return Comp.PendingStatusEffectsComp.get(entity).effects;
		}
		if(Comp.StatusEffectsComp.has(entity)) {
			return Comp.StatusEffectsComp.get(entity).effects;
		}
		return null;
	}

}
