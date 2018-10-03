package com.jharter.game.ecs.systems.turnphase;

import com.badlogic.ashley.core.Entity;
import com.jharter.game.ecs.components.Components.ActionQueuedComp;
import com.jharter.game.ecs.components.Components.ActivePlayerComp;
import com.jharter.game.ecs.components.Components.ChangeZoneComp;
import com.jharter.game.ecs.components.Components.CleanupTurnActionComp;
import com.jharter.game.ecs.components.Components.CursorComp;
import com.jharter.game.ecs.components.Components.PlayerComp;
import com.jharter.game.ecs.components.Components.TurnPhasePerformFriendActionsComp;
import com.jharter.game.ecs.components.Components.TurnPhaseSelectFriendActionsComp;
import com.jharter.game.ecs.components.Components.ZoneComp;
import com.jharter.game.ecs.components.subcomponents.TurnTimer;
import com.jharter.game.util.id.ID;

import uk.co.carelesslabs.Enums.ZoneType;
import uk.co.carelesslabs.Media;

public class TurnPhaseSelectFriendActionsSystem extends TurnPhaseSystem {
	
	public TurnPhaseSelectFriendActionsSystem() {
		super(TurnPhaseSelectFriendActionsComp.class, TurnPhasePerformFriendActionsComp.class);
		//add(ActionQueuedComp.class);
		//add(PlayerComp.class);
		add(ActivePlayerComp.class);
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
		return getPlayerIDs().size() == getFirstComponent(ActivePlayerComp.class).spentPlayers.size;
	}

	@Override
	protected void processEntityPhaseEnd(Entity turnEntity, float deltaTime) {
		disableCursor();
		getTurnTimer().stop();
		discardCards();
		
		// Cancel the current turn action if there is one
		CursorComp c = getCursorComp();
		if(c.turnActionID != null) {
			if(!Comp.ActionSpentComp.has(c.turnActionID)) {
				Comp.add(CleanupTurnActionComp.class, c.turnActionID);
			}
		}
	}
	
	private void discardCards() {
		for(ID playerID : getPlayerIDs()) {
			ZoneComp zHand = getZone(playerID, ZoneType.HAND);
			ZoneComp zDiscard = getZone(playerID, ZoneType.DISCARD);
			for(ID cardID : zHand.objectIDs) {
				Entity card = Comp.Entity.get(cardID);
				Comp.util(Comp.add(ChangeZoneComp.class, card)).change(zHand.zoneID, zDiscard.zoneID);
			}
		}
	}
	
}	