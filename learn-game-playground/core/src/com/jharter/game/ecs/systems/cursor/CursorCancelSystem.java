package com.jharter.game.ecs.systems.cursor;

import com.badlogic.ashley.core.Entity;
import com.jharter.game.ecs.components.Components.ActiveTurnActionComp;
import com.jharter.game.ecs.components.Components.CursorComp;
import com.jharter.game.ecs.components.Components.CursorInputComp;
import com.jharter.game.ecs.components.subcomponents.TurnAction;

import uk.co.carelesslabs.Media;

public class CursorCancelSystem extends CursorSystem {

	@SuppressWarnings("unchecked")
	public CursorCancelSystem() {
		super(CursorInputComp.class);
	}

	@Override
	public void processEntity(Entity cursor, CursorComp c, float deltaTime) {
		CursorInputComp ci = Comp.CursorInputComp.get(cursor);

		if(ci.cancel) {
			if(tryRevertToCancelTurnActionTarget(c)) {
				Media.cancelBeep.play();
				cursor.remove(ActiveTurnActionComp.class); // XXX Highly suspect line of code that probably doesn't do anything
			}
		}

		ci.cancel = false;
	}

	private boolean tryRevertToCancelTurnActionTarget(CursorComp c) {
		// If no turn action, nothing to cancel
		if(c.turnActionID == null) {
			return false;
		}

		TurnAction t = Comp.TurnActionComp.get(c.turnActionID).turnAction;

		// As part of the cancel, try to pop the most
		// recent target.
		if(t.targetIDs.size > 0) {
			t.targetIDs.pop();
			t.selectedCount--;
		}

		// If we still have targets, set our cursor to
		// our most recent one.
		if(t.targetIDs.size > 0) {
			c.targetID = t.targetIDs.peek();
			return true;

		// If we don't have targets, cancel the entire
		// turn action, moving the cursor to the turn
		// action's location.
		} else {
			c.targetID = c.turnActionID;
			c.turnActionID = null;
			return true;
		}
	}
}
