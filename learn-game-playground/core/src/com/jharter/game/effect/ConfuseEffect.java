package com.jharter.game.effect;

import com.badlogic.ashley.core.Entity;

public class ConfuseEffect extends Effect {

	private int turns;

	public ConfuseEffect() {
		this(3);
	}

	public ConfuseEffect(int turns) {
		this.turns = turns;
	}

	@Override
	protected void apply(Entity performer, Entity target, boolean pending) {
		getStatusEffects(target).confused.beginPending(pending).incr(turns).endPending();
	}

	@Override
	protected void handleAudioVisual(Entity performer, Entity target) {

	}

}
