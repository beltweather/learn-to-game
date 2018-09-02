package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.jharter.game.ashley.components.Comp;
import com.jharter.game.ashley.components.Components.ActionQueuedComp;
import com.jharter.game.ashley.components.Components.ActionSpentComp;
import com.jharter.game.ashley.components.Components.ChangeZoneComp;
import com.jharter.game.ashley.components.Components.CursorComp;
import com.jharter.game.ashley.components.Components.TurnPhasePerformFriendActionsComp;
import com.jharter.game.ashley.components.Components.TurnPhaseSelectFriendActionsComp;
import com.jharter.game.ashley.components.Components.ZonePositionComp;
import com.jharter.game.ashley.components.Ent;
import com.jharter.game.ashley.components.Link;
import com.jharter.game.ashley.components.subcomponents.TurnTimer;
import com.jharter.game.util.Sys;

import uk.co.carelesslabs.Enums.ZoneType;

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
		/*CursorComp c = Ent.CursorEntity.CursorComp();
		ChangeZoneComp cz = Comp.getOrAdd(getEngine(), ChangeZoneComp.class, Ent.CursorEntity.Entity());
		cz.newZoneID = Link.ZoneComp.getID(Ent.TurnEntity.ActivePlayerComp().activePlayerID, ZoneType.HAND);
		cz.newIndex = 0;
		ZonePositionComp zp = Comp.ZonePositionComp.get(Ent.CursorEntity.Entity());
		zp.zoneID = cz.newZoneID;
		zp.index = 0;*/
		
		enableCursor();
		resetCursor();
		Ent.TurnEntity.TurnTimerComp().turnTimer.start();
		return true;
	}

	@Override
	protected boolean processEntityPhaseMiddle(Entity entity, float deltaTime) {
		TurnTimer t = Comp.TurnTimerComp.get(entity).turnTimer;
		t.increment(deltaTime);
		if(t.isOvertime()) {
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
		Ent.TurnEntity.TurnTimerComp().turnTimer.stop();
		
		// Cancel the current turn action if there is one
		CursorComp c = Ent.CursorEntity.CursorComp();
		if(c.turnActionEntityID != null) {
			Entity entity = Ent.Entity.get(c.turnActionEntityID);
			if(!Comp.ActionSpentComp.has(entity)) {
				entity.add(Comp.create(getEngine(), ActionSpentComp.class));
			}
		}
	}
	
}	