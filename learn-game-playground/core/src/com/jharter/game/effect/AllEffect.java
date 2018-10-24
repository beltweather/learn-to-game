package com.jharter.game.effect;

import com.badlogic.ashley.core.Entity;
import com.jharter.game.primitives.boolean_;

public class AllEffect extends Effect {

	public AllEffect() {
		super();
	}

	@Override
	protected void apply(Entity performer, Entity target, boolean pending) {
		boolean_ all = getMods(target).all.beginPending(pending);
		all.v(all.v() || true);
		all.endPending();
	}

	@Override
	protected void handleAudioVisual(Entity performer, Entity target) {

	}

}
