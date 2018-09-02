package com.jharter.game.ashley.components.subcomponents;

import com.badlogic.ashley.core.Entity;
import com.jharter.game.ashley.components.Comp;
import com.jharter.game.ashley.components.Components.StatsComp;

public class CombatUtil {

	private CombatUtil() {}
	
	public static int getDamage(Entity attacker, Entity defender, int baseDamage) {
		StatsComp sAttacker = Comp.StatsComp.get(attacker);
		StatsComp sDefender = Comp.StatsComp.get(defender);
		return baseDamage * sAttacker.power - sDefender.defense;
	}
	
}
