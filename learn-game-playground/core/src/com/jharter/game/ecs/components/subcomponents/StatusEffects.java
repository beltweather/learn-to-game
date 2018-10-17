package com.jharter.game.ecs.components.subcomponents;

import com.badlogic.gdx.utils.Array;

import uk.co.carelesslabs.Enums.StatusEffectType;

public class StatusEffects implements Pendable<StatusEffects> {

	public int maxEffects = 1;
	public Array<StatusEffectType> effects = new Array<>();

	public StatusEffects() {

	}

	public boolean isAtCapacity() {
		return effects.size >= maxEffects;
	}

	@Override
	public StatusEffects setFrom(StatusEffects other) {
		maxEffects = other.maxEffects;
		effects.clear();
		effects.addAll(other.effects);
		return this;
	}

	@Override
	public StatusEffects clear() {
		effects.clear();
		maxEffects = 1;
		return this;
	}

}
