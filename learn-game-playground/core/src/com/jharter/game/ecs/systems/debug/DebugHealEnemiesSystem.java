package com.jharter.game.ecs.systems.debug;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.jharter.game.ecs.components.Components.EnemyTag;
import com.jharter.game.ecs.components.Components.TurnPhaseStartTurnTag;
import com.jharter.game.ecs.components.Components.VitalsComp;
import com.jharter.game.ecs.systems.boilerplate.GameIteratingSystem;

public class DebugHealEnemiesSystem extends GameIteratingSystem {
	
	public DebugHealEnemiesSystem() {
		super(Family.all(EnemyTag.class, VitalsComp.class).get());
		require(TurnPhaseStartTurnTag.class);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		VitalsComp v = Comp.VitalsComp.get(entity);
		v.vitals.health = v.vitals.maxHealth;
	}

}
