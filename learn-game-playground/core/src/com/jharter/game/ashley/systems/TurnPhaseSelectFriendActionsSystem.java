package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.jharter.game.ashley.components.Comp;
import com.jharter.game.ashley.components.Components.ActionQueuedComp;
import com.jharter.game.ashley.components.Components.CleanupTurnActionComp;
import com.jharter.game.ashley.components.Components.CursorComp;
import com.jharter.game.ashley.components.Components.TurnPhasePerformFriendActionsComp;
import com.jharter.game.ashley.components.Components.TurnPhaseSelectFriendActionsComp;
import com.jharter.game.ashley.components.subcomponents.TurnTimer;
import com.jharter.game.util.id.IDUtil;

import uk.co.carelesslabs.Media;

public class TurnPhaseSelectFriendActionsSystem extends TurnPhaseSystem {
	
	public TurnPhaseSelectFriendActionsSystem() {
		super(TurnPhaseSelectFriendActionsComp.class, TurnPhasePerformFriendActionsComp.class);
	}

	@Override
	protected boolean processEntityPhaseStart(Entity turnEntity, float deltaTime) {
		if(!isDoneAnimating()) {
			return false;
		}
		enableCursor();
		resetCursor();
		Comp.Entity.DefaultTurn().TurnTimerComp().turnTimer.start();
		Media.startTurnBeep.play();
		ActionQueuedComp.QUEUE_INDEX = 0;
		return true;
	}

	@Override
	protected boolean processEntityPhaseMiddle(Entity entity, float deltaTime) {
		TurnTimer t = Comp.TurnTimerComp.get(entity).turnTimer;
		t.increment(deltaTime);
		if(t.isOvertime()) {
			return true;
		}
		
		// XXX This assumption will change, but the intent is to check if all characters
		// have made a card selection. Currently, one cursor controls all actions so 
		// we'll leave this hack in for testing.
		return count(ActionQueuedComp.class) >= IDUtil.getPlayerIDs().size();
	}

	@Override
	protected void processEntityPhaseEnd(Entity turnEntity, float deltaTime) {
		disableCursor();
		Comp.Entity.DefaultTurn().TurnTimerComp().turnTimer.stop();
		
		// Cancel the current turn action if there is one
		CursorComp c = Comp.CursorComp.get(Comp.Entity.DefaultCursor().Entity());
		if(c.turnActionEntityID != null) {
			Entity entity = Comp.Entity.get(c.turnActionEntityID);
			if(!Comp.ActionSpentComp.has(entity)) {
				entity.add(Comp.create(getEngine(), CleanupTurnActionComp.class));
			}
		}
	}
	
}	