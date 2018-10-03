package com.jharter.game.ecs.systems.cursor;

import com.badlogic.ashley.core.Entity;
import com.jharter.game.ecs.components.Components.CursorComp;
import com.jharter.game.ecs.components.Components.CursorInputComp;
import com.jharter.game.ecs.components.Components.PendingTurnActionComp;
import com.jharter.game.ecs.components.subcomponents.TurnAction;
import com.jharter.game.util.id.ID;

import uk.co.carelesslabs.Media;

public class CursorAcceptSystem extends CursorSystem {

	@SuppressWarnings("unchecked")
	public CursorAcceptSystem() {
		super(CursorInputComp.class);
	}

	@Override
	public void processEntity(Entity cursor, CursorComp c, float deltaTime) {
		if(c.targetID == null) {
			return;
		}
		
		Entity target = Comp.Entity.get(c.targetID);
		if(target == null) {
			return;
		}

		CursorInputComp ci = Comp.CursorInputComp.get(cursor);
		if(ci.accept) {
			Media.acceptBeep.play();
			
			TurnAction t = getTurnAction(c);
			ID targetEntityID = c.targetID;
				
			// If this is our first target, make them the turn action entity
			if(t == null) {
				c.turnActionID = targetEntityID;
				Comp.add(PendingTurnActionComp.class, target);
			
			// Otherwise, add them to the turn entity target list
			} else {
				t.addTarget(target);
			}
			
			// When we select a new target, record it for our "undo" history
			if(c.targetID != null) {
				c.history.add(c.targetID);
			}
		}
		
		ci.accept = false;
	}
	
}
