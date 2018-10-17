package com.jharter.game.effect;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;

import uk.co.carelesslabs.Enums.StatusEffectType;

public class StatusEffect extends Effect<Array<StatusEffectType>>{

	private Array<StatusEffectType> types;

	public StatusEffect(StatusEffectType...types) {
		super(EffectProp.STATUS);
		this.types = new Array<>(types);
	}

	@Override
	public Array<StatusEffectType> getResult(Entity performer, Entity target) {
		return types;
	}

	@Override
	public void handleAudioVisual(Entity performer, Entity target) {

	}


}
