package com.jharter.game.effect;

import com.badlogic.ashley.core.Entity;

public class HealFromDamageEffect extends HealEffect {

	public HealFromDamageEffect(int baseHealing) {
		super(baseHealing);
	}

	@Override
	protected int getHealing(Entity healer, Entity patient) {
		return getCombatHelper().getDamage(healer, patient, baseHealing);
	}

}
