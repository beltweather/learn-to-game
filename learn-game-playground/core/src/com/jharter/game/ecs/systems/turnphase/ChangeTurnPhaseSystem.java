package com.jharter.game.ecs.systems.turnphase;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.jharter.game.ecs.components.Components.NextTurnPhaseComp;
import com.jharter.game.ecs.components.Components.TurnPhaseTag;
import com.jharter.game.ecs.systems.boilerplate.FirstSystem;

public class ChangeTurnPhaseSystem extends FirstSystem {

	public ChangeTurnPhaseSystem() {
		super(Family.all(TurnPhaseTag.class, NextTurnPhaseComp.class).get());
	}
	
	@Override
	protected void processEntity(Entity turnPhase, float deltaTime) {
		NextTurnPhaseComp n = Comp.NextTurnPhaseComp.get(turnPhase);
		Comp.swap(NextTurnPhaseComp.class, n.next, turnPhase);
	}

}
