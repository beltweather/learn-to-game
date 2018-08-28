package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.jharter.game.ashley.components.Components.ActionQueuedComp;
import com.jharter.game.ashley.components.Components.ActionSpentComp;
import com.jharter.game.ashley.components.Components.CursorComp;
import com.jharter.game.ashley.components.Components.TurnActionComp;
import com.jharter.game.ashley.components.Components.TurnPhasePerformFriendActionsComp;
import com.jharter.game.ashley.components.Components.TurnPhaseSelectFriendActionsComp;
import com.jharter.game.ashley.components.Mapper;
import com.jharter.game.util.Sys;

public class TurnPhaseSelectFriendActionsSystem extends TurnPhaseSystem {
	
	public TurnPhaseSelectFriendActionsSystem() {
		super(TurnPhaseSelectFriendActionsComp.class, TurnPhasePerformFriendActionsComp.class);
	}

	@Override
	protected boolean processEntityPhaseStart(Entity turnEntity, float deltaTime) {
		if(!isDoneAnimating()) {
			return false;
		}
		Sys.out.println("------------------------------------------Starting turn");
		Mapper.TurnEntity.TurnTimerComp().start();
		enableCursor();
		resetCursor();
		return true;
	}

	@Override
	protected boolean processEntityPhaseMiddle(Entity entity, float deltaTime) {
		Mapper.TurnEntity.TurnTimerComp().increment(deltaTime);
		if(Mapper.TurnEntity.TurnTimerComp().isOvertime()) {
			return true;
		}
		
		int actionsQueued = count(ActionQueuedComp.class);
		
		// Debug
		if(actionsQueued > 0) {
			//disableCursor();
		}
		
		// XXX This assumption will change, but the intent is to check if all characters
		// have made a card selection. Currently, one cursor controls all actions so 
		// we'll leave this hack in for testing.
		return actionsQueued >= 4;
	}

	@Override
	protected void processEntityPhaseEnd(Entity turnEntity, float deltaTime) {
		disableCursor();
		Mapper.TurnEntity.TurnTimerComp().stop();
		
		// Cancel the current turn action if there is one
		CursorComp c = Mapper.CursorEntity.CursorComp();
		if(c.turnActionEntityID != null) {
			Entity entity = Mapper.Entity.get(c.turnActionEntityID);
			if(!Mapper.ActionSpentComp.has(entity)) {
				entity.add(Mapper.Comp.get(ActionSpentComp.class));
			}
		}
	}
	
}	