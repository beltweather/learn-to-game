package com.jharter.game.effect;

import com.badlogic.ashley.core.Entity;

public class HealEffect extends HealthEffect {

	private int baseHealing;
	
	public HealEffect(int baseHealing) {
		this.baseHealing = baseHealing;
	}
	
	public int getBaseHealing() {
		return baseHealing;
	}
	
	public int getHealing(Entity healer, Entity patient) {
		return getCombatHelper().getHealing(healer, patient, baseHealing);
	}
	
	@Override
	public Integer perform(Entity healer, Entity patient) {
		return getHealing(healer, patient);
	}
	
}
