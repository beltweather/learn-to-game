package com.jharter.game.ecs.systems.turnphase;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.jharter.game.ecs.components.Components.NextTurnPhaseComp;
import com.jharter.game.ecs.components.Components.TurnPhaseComp;
import com.jharter.game.ecs.systems.boilerplate.FirstSystem;

public class ChangeTurnPhaseSystem extends FirstSystem {

	//private Array<Class<? extends Component>> turnPhaseOrder;
	
	public ChangeTurnPhaseSystem() {
		super(Family.all(TurnPhaseComp.class, NextTurnPhaseComp.class).get());
		//initOrder();
	}
	
	/*private void initOrder() {
		turnPhaseOrder = new Array<Class<? extends Component>>();
		turnPhaseOrder.add(TurnPhaseStartBattleComp.class);
		turnPhaseOrder.add(TurnPhaseStartTurnComp.class);
		turnPhaseOrder.add(TurnPhaseSelectActionsComp.class);
		turnPhaseOrder.add(TurnPhasePerformActionsComp.class);
		turnPhaseOrder.add(TurnPhaseEndTurnComp.class);
		turnPhaseOrder.add(TurnPhaseEndBattleComp.class);
	}*/

	@Override
	protected void processEntity(Entity turnPhase, float deltaTime) {
		NextTurnPhaseComp n = Comp.NextTurnPhaseComp.get(turnPhase);
		Comp.swap(NextTurnPhaseComp.class, n.next, turnPhase);
	}

}
