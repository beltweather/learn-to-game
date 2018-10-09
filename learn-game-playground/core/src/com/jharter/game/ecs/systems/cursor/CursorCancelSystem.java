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
			if(tryRevertToLastTarget(c)) {
				Media.cancelBeep.play();	
				cursor.remove(ActiveTurnActionComp.class); // XXX Highly suspect line of code that probably doesn't do anything
				
				TurnAction t = getCursorManager().getTurnAction(c);
				if(t != null) {
					if(t.targetIDs.size == 0) {
						c.turnActionID = null;
					} else if(t.targetIDs.size > 0) {
						t.targetIDs.pop();
					}
				}
			}
		}
		
		ci.cancel = false;
	}
	
	private boolean tryRevertToLastTarget(CursorComp c) {
		if(c.history.size == 0) {
			return false;
		}
		c.targetID = c.history.pop();
		return true;
	}
}
