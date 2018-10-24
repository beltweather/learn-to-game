package com.jharter.game.effect;

import com.badlogic.ashley.core.Entity;
import com.jharter.game.primitives.int_;

public class MultiplicityEffect extends Effect {

	protected int mult;

	public MultiplicityEffect(int mult) {
		super();
		this.mult = mult;
	}

	@Override
	protected void apply(Entity performer, Entity target, boolean pending) {
		int_ multiplicity = getMods(target).multiplicity.beginPending(pending);
		multiplicity.mult(mult);
		multiplicity.endPending();
	}

	@Override
	protected void handleAudioVisual(Entity performer, Entity target) {

	}

}