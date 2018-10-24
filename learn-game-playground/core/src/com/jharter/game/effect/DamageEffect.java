package com.jharter.game.effect;

import com.badlogic.ashley.core.Entity;
import com.jharter.game.primitives.int_;

import uk.co.carelesslabs.Media;

public class DamageEffect extends Effect {

	private int baseDamage;

	public DamageEffect(int baseDamage) {
		super();
		this.baseDamage = baseDamage;
	}

	@Override
	protected void apply(Entity performer, Entity target, boolean pending) {
		int_ health = getVitals(target).health.beginPending(pending);
		health.decr(getDamage(performer, target));
		if(health.v() < 0) {
			health.v(0);
		}
		health.endPending();
	}

	protected int getDamage(Entity attacker, Entity defender) {
		return getCombatHelper().getDamage(attacker, defender, baseDamage);
	}

	@Override
	protected void handleAudioVisual(Entity attacker, Entity defender) {
		Media.weaponSwing.play();
	}

}
