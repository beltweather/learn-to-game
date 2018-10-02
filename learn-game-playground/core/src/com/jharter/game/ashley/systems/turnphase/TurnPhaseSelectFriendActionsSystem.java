package com.jharter.game.ashley.systems.turnphase;

import com.badlogic.ashley.core.Entity;
import com.jharter.game.ashley.components.Comp;
import com.jharter.game.ashley.components.Components.ActionQueuedComp;
import com.jharter.game.ashley.components.Components.CleanupTurnActionComp;
import com.jharter.game.ashley.components.Components.CursorComp;
import com.jharter.game.ashley.components.Components.PlayerComp;
import com.jharter.game.ashley.components.Components.TurnPhasePerformFriendActionsComp;
import com.jharter.game.ashley.components.Components.TurnPhaseSelectFriendActionsComp;
import com.jharter.game.ashley.components.subcomponents.TurnTimer;

import uk.co.carelesslabs.Media;

public class TurnPhaseSelectFriendActionsSystem extends TurnPhaseSystem {
	
	public TurnPhaseSelectFriendActionsSystem() {
		super(TurnPhaseSelectFriendActionsComp.class, TurnPhasePerformFriendActionsComp.class);
		add(ActionQueuedComp.class);
		add(PlayerComp.class);
	}

	@Override
	protected boolean processEntityPhaseStart(Entity turnEntity, float deltaTime) {
		if(!isDoneAnimating()) {
			return false;
		}
		enableCursor();
		resetCursor();
		getTurnTimer().start();
		Media.startTurnBeep.play();
		ActionQueuedComp.QUEUE_INDEX = 0;
		return true;
	}

	@Override
	protected boolean processEntityPhaseMiddle(Entity entity, float deltaTime) {
		TurnTimer t = getTurnTimer();
		t.increment(deltaTime);
		if(t.isOvertime()) {
			return true;
		}
		
		// XXX This assumption will change, but the intent is to check if all characters
		// have made a card selection. Currently, one cursor controls all actions so 
		// we'll leave this hack in for testing.
		return countEntities(ActionQueuedComp.class) >= countEntities(PlayerComp.class);
	}

	@Override
	protected void processEntityPhaseEnd(Entity turnEntity, float deltaTime) {
		disableCursor();
		getTurnTimer().stop();
		
		// Cancel the current turn action if there is one
		CursorComp c = getCursorComp();
		if(c.turnActionID != null) {
			Entity entity = Comp.Entity.get(c.turnActionID);
			if(!Comp.ActionSpentComp.has(entity)) {
				entity.add(Comp.create(getEngine(), CleanupTurnActionComp.class));
			}
		}
	}
	
}	