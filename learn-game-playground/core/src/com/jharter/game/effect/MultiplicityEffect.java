package com.jharter.game.effect;

import com.badlogic.ashley.core.Entity;

public class MultiplicityEffect extends Effect<Integer> {

	protected int multiplicity;

	public MultiplicityEffect(int multiplicity) {
		super(EffectProp.MULTIPLICITY);
		this.multiplicity = multiplicity;
	}

	@Override
	public Integer getResult(Entity performer, Entity target) {
		return multiplicity;
	}

	@Override
	public void handleAudioVisual(Entity performer, Entity target) {

	}

}
