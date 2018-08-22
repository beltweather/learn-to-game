package com.jharter.game.ashley.components.subcomponents;

import com.badlogic.ashley.core.Entity;
import com.jharter.game.ashley.components.Components.StatsComp;
import com.jharter.game.ashley.components.Mapper;

public class CombatUtil {

	private CombatUtil() {}
	
	public static int getDamage(Entity attacker, Entity defender, int baseDamage) {
		StatsComp sAttacker = Mapper.StatsComp.get(attacker);
		StatsComp sDefender = Mapper.StatsComp.get(defender);
		return baseDamage * sAttacker.power - sDefender.defense;
	}
	
}
