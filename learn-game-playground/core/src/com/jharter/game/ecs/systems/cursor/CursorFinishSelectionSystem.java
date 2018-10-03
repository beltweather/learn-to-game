package com.jharter.game.ecs.systems.cursor;

import com.badlogic.ashley.core.Entity;
import com.jharter.game.ecs.components.Components.ActionQueueableComp;
import com.jharter.game.ecs.components.Components.ActivePlayerComp;
import com.jharter.game.ecs.components.Components.ChangeZoneComp;
import com.jharter.game.ecs.components.Components.CursorComp;
import com.jharter.game.ecs.components.Components.PendingTurnActionComp;
import com.jharter.game.ecs.components.Components.ZoneComp;
import com.jharter.game.ecs.components.subcomponents.TurnAction;
import com.jharter.game.util.id.ID;

import uk.co.carelesslabs.Enums.ZoneType;

public class CursorFinishSelectionSystem extends CursorSystem {

	@SuppressWarnings("unchecked")
	public CursorFinishSelectionSystem() {
		super();
	}

	@Override
	public void processEntity(Entity cursor, CursorComp c, float deltaTime) {
		TurnAction t = getTurnAction(c);
		if(t == null || !t.hasAllTargets()) {
			return;
		}
		
		ActivePlayerComp a = getActivePlayer();
		a.spentPlayers.add(a.activePlayerID);
		nextPlayer();
		
		Comp.swap(PendingTurnActionComp.class, ActionQueueableComp.class, c.turnActionID);
		c.reset();
	}
	
}
