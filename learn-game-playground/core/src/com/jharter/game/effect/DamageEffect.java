package com.jharter.game.effect;

import com.badlogic.ashley.core.Entity;

public class DamageEffect extends HealthEffect {

	private int baseDamage;
	
	public DamageEffect(int baseDamage) {
		this.baseDamage = baseDamage;
	}
	
	public int getBaseDamage() {
		return baseDamage;
	}
	
	public int getDamage(Entity attacker, Entity defender) {
		return getCombatHelper().getDamage(attacker, defender, baseDamage);
	}
	
	@Override
	public Integer perform(Entity attacker, Entity defender) {
		return getDamage(attacker, defender);
	}
	
}
