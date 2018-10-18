package com.jharter.game.ecs.components.subcomponents;

import com.badlogic.gdx.utils.Array;

import uk.co.carelesslabs.Enums.StatusEffectType;

public class StatusEffects implements Pendable<StatusEffects> {

	public int maxEffects = 1;
	public Array<StatusEffectType> types = new Array<>();

	public StatusEffects() {

	}

	public boolean isAtCapacity() {
		return types.size >= maxEffects;
	}

	@Override
	public StatusEffects setFrom(StatusEffects other) {
		maxEffects = other.maxEffects;
		types.clear();
		types.addAll(other.types);
		return this;
	}

	@Override
	public StatusEffects clear() {
		types.clear();
		maxEffects = 1;
		return this;
	}

}
