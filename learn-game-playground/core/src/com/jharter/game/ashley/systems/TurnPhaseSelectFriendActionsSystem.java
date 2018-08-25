package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.jharter.game.ashley.components.Components.ActionQueuedComp;
import com.jharter.game.ashley.components.Components.TurnPhasePerformFriendActionsComp;
import com.jharter.game.ashley.components.Components.TurnPhaseSelectFriendActionsComp;
import com.jharter.game.ashley.components.Mapper;

public class TurnPhaseSelectFriendActionsSystem extends TurnPhaseSystem {
	
	public TurnPhaseSelectFriendActionsSystem() {
		super(TurnPhaseSelectFriendActionsComp.class, TurnPhasePerformFriendActionsComp.class);
	}

	@Override
	protected boolean processEntityPhaseStart(Entity turnEntity, float deltaTime) {
		Mapper.TurnEntity.TurnTimerComp().start();
		enableCursor();
		return true;
	}

	@Override
	protected boolean processEntityPhaseMiddle(Entity entity, float deltaTime) {
		Mapper.TurnEntity.TurnTimerComp().increment(deltaTime);
		if(Mapper.TurnEntity.TurnTimerComp().isOvertime()) {
			return true;
		}
		
		// XXX This assumption will change, but the intent is to check if all characters
		// have made a card selection. Currently, one cursor controls all actions so 
		// we'll leave this hack in for testing.
		return count(ActionQueuedComp.class) >= 4;
	}

	@Override
	protected void processEntityPhaseEnd(Entity turnEntity, float deltaTime) {
		Mapper.TurnEntity.TurnTimerComp().stop();
		disableCursor();
	}
	
}