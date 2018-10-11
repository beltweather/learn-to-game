package com.jharter.game.effect;

import com.badlogic.ashley.core.Entity;

import uk.co.carelesslabs.Media;

public class DamageEffect extends Effect<Integer> {

	private int baseDamage;
	
	public DamageEffect(int baseDamage) {
		super(EffectProp.DAMAGE);
		this.baseDamage = baseDamage;
	}
	
	public int getBaseDamage() {
		return baseDamage;
	}
	
	public int getDamage(Entity attacker, Entity defender) {
		return getCombatHelper().getDamage(attacker, defender, baseDamage);
	}
	
	@Override
	public Integer getResult(Entity attacker, Entity defender) {
		return getDamage(attacker, defender);
	}

	@Override
	public void handleAudioVisual(Entity attacker, Entity defender) {
		Media.weaponSwing.play();
	}
	
}
