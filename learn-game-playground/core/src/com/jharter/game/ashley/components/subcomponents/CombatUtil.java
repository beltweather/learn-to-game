package com.jharter.game.ashley.components.subcomponents;

import com.jharter.game.ashley.components.Components.StatsComp;

public class CombatUtil {

	private CombatUtil() {}
	
	public static int getDamage(StatsComp sAttacker, StatsComp sDefender, int baseDamage) {
		return baseDamage * sAttacker.power - sDefender.defense;
	}
	
}
