package com.jharter.game.ecs.helpers;

import com.badlogic.ashley.core.Entity;
import com.jharter.game.ecs.components.Components.StatsComp;
import com.jharter.game.ecs.entities.EntityHandler;
import com.jharter.game.ecs.entities.IEntityHandler;

public class CombatHelper extends EntityHandler {

	public CombatHelper(IEntityHandler handler) {
		super(handler);
	}
	
	public int getDamage(Entity attacker, Entity defender, int baseDamage) {
		StatsComp sAttacker = Comp.StatsComp.get(attacker);
		StatsComp sDefender = Comp.StatsComp.get(defender);
		return baseDamage * sAttacker.power - sDefender.defense;
	}
	
}
