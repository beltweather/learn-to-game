package com.jharter.game.effect;

import com.badlogic.ashley.core.Entity;

public class HealEffect extends Effect<Integer> {

	protected int baseHealing;
	
	public HealEffect(int baseHealing) {
		super(EffectProp.HEAL);
		this.baseHealing = baseHealing;
	}
	
	public int getBaseHealing() {
		return baseHealing;
	}
	
	public int getHealing(Entity healer, Entity patient) {
		return getCombatHelper().getHealing(healer, patient, baseHealing);
	}
	
	@Override
	public Integer getResult(Entity healer, Entity patient) {
		return getHealing(healer, patient);
	}

	@Override
	public void handleAudioVisual(Entity attacker, Entity defender) {
		
	}
	
}
