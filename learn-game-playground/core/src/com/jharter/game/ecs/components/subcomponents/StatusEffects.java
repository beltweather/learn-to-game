package com.jharter.game.ecs.components.subcomponents;

import com.badlogic.gdx.utils.Array;

import uk.co.carelesslabs.Enums.StatusEffectType;

public class StatusEffects implements Pendable<StatusEffects> {

	public int maxEffects = 1;
	public Array<StatusEffectType> types = new Array<>();
	public Array<StatusEffectType> weakToTypes = new Array<>();
	public Array<StatusEffectType> resistantToTypes = new Array<>();
	public Array<StatusEffectType> immuneToTypes = new Array<>();
	public StatusEffects defaultEffects = null;

	public StatusEffects() {

	}

	public boolean isAtCapacity() {
		return types.size >= maxEffects;
	}

	public StatusEffects setFromDefaults() {
		if(defaultEffects == null) {
			return clear();
		}
		return setFrom(defaultEffects);
	}

	public StatusEffects storeDefaults() {
		if(defaultEffects == null) {
			defaultEffects = new StatusEffects();
		}
		defaultEffects.setFrom(this);
		return this;
	}

	@Override
	public StatusEffects setFrom(StatusEffects other) {
		maxEffects = other.maxEffects;
		types.clear();
		types.addAll(other.types);
		weakToTypes.clear();
		weakToTypes.addAll(other.weakToTypes);
		resistantToTypes.clear();
		resistantToTypes.addAll(other.resistantToTypes);
		immuneToTypes.clear();
		immuneToTypes.addAll(other.immuneToTypes);
		return this;
	}

	@Override
	public StatusEffects clear() {
		types.clear();
		weakToTypes.clear();
		resistantToTypes.clear();
		immuneToTypes.clear();
		maxEffects = 1;
		defaultEffects = null;
		return this;
	}

}
