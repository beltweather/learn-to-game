package com.jharter.game.effect;

import com.badlogic.ashley.core.Entity;
import com.jharter.game.primitives.int_;

public class HealEffect extends Effect {

	protected int baseHealing;

	public HealEffect(int baseHealing) {
		super();
		this.baseHealing = baseHealing;
	}

	@Override
	protected void apply(Entity performer, Entity target, boolean pending) {
		int_ maxHealth = getVitals(target).maxHealth;
		int_ health = getVitals(target).health.beginPending(pending);
		health.incr(getHealing(performer, target));
		if(health.v() > maxHealth.v()) {
			health.v(maxHealth.v());
		}
		health.endPending();
	}

	protected int getHealing(Entity healer, Entity patient) {
		return getCombatHelper().getHealing(healer, patient, baseHealing);
	}

	@Override
	protected void handleAudioVisual(Entity attacker, Entity defender) {

	}
}
