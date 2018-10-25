package com.jharter.game.effect;

import com.badlogic.ashley.core.Entity;

public class SkipEffect extends Effect {

	private int turns;

	public SkipEffect() {
		this(1);
	}

	public SkipEffect(int turns) {
		this.turns = turns;
	}

	@Override
	protected void apply(Entity performer, Entity target, boolean pending) {
		getStatusEffects(target).skip.beginPending(pending).incr(turns).endPending();
	}

	@Override
	protected void handleAudioVisual(Entity performer, Entity target) {

	}

}
