package com.jharter.game.ecs.systems.cursor;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.TimeUtils;
import com.jharter.game.ecs.components.Components.ActivePlayerComp;
import com.jharter.game.ecs.components.Components.CursorComp;
import com.jharter.game.ecs.components.Components.CursorInputComp;
import com.jharter.game.ecs.components.Components.TurnActionSelectedEvent;
import com.jharter.game.ecs.components.subcomponents.TurnAction;

import uk.co.carelesslabs.Media;

public class CursorAcceptSystem extends CursorSystem {

	@SuppressWarnings("unchecked")
	public CursorAcceptSystem() {
		super(CursorInputComp.class);
		event(TurnActionSelectedEvent.class);
	}

	@Override
	public void processEntity(Entity cursor, CursorComp c, float deltaTime) {
		CursorInputComp ci = Comp.CursorInputComp.get(cursor);
		if(!ci.accept) {
			return;
		}

		if(c.targetID == null) {
			return;
		}

		Entity target = Comp.Entity.get(c.targetID);
		if(target == null) {
			return;
		}

		// If this is our first target, make them the turn action entity
		TurnAction t = getCursorManager().getTurnAction(c);
		if(t == null) {
			c.turnActionID = c.targetID;
			Comp.PendingTurnActionTag.add(target);

		// Otherwise, check if we've gotten all our targets and are now accepting
		} else {
			t.selectedCount++;
		}

		if(t != null && t.hasAllTargets()) {
			selectTurnAction(c, t);
		}

		Media.acceptBeep.play();
		ci.accept = false;
	}

	private void selectTurnAction(CursorComp c, TurnAction t) {
		ActivePlayerComp a = getActivePlayer();
		a.spentPlayers.add(a.activePlayerID);
		nextPlayer();
		Comp.TurnActionSelectedEvent.add(c.turnActionID).timestamp = TimeUtils.millis();
		c.reset();
	}

}
