package com.jharter.game.ecs.components.subcomponents;

import com.jharter.game.primitives.Array_;

import uk.co.carelesslabs.Enums.StatusEffectType;

public class StatusEffects implements Pendable<StatusEffects> {

	public Array_<StatusEffectType> types = new Array_<>();
	public Array_<StatusEffectType> weakToTypes = new Array_<>();
	public Array_<StatusEffectType> resistantToTypes = new Array_<>();
	public Array_<StatusEffectType> immuneToTypes = new Array_<>();

	public int maxEffects = 1;

	public StatusEffects() {

	}

	@Override
	public void setToDefault() {
		types.setToDefault();
		weakToTypes.setToDefault();
		resistantToTypes.setToDefault();
		immuneToTypes.setToDefault();
	}

	@Override
	public void resetPending() {
		types.resetPending();
		weakToTypes.resetPending();
		resistantToTypes.resetPending();
		immuneToTypes.resetPending();
	}

	@Override
	public void clear() {
		types.clear();
		weakToTypes.clear();
		resistantToTypes.clear();
		immuneToTypes.clear();
		maxEffects = 1;
	}

	public boolean isAtCapacity() {
		return types.v().size >= maxEffects;
	}
}
