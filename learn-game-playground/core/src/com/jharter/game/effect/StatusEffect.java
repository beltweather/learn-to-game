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

	public StatusEffect fire() {
		types.add(StatusEffectType.FIRE);
		return this;
	}

	public StatusEffect ice() {
		types.add(StatusEffectType.ICE);
		return this;
	}

	public StatusEffect lightning() {
		types.add(StatusEffectType.LIGHTNING);
		return this;
	}

	public StatusEffect poison() {
		types.add(StatusEffectType.POISON);
		return this;
	}

	@Override
	public Array<StatusEffectType> getResult(Entity performer, Entity target) {
		return types;
	}

	@Override
	public void handleAudioVisual(Entity performer, Entity target) {

	}


}
