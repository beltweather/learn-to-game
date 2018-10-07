package com.jharter.game.ecs.systems.turnphase;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.jharter.game.ecs.components.Components.ActionQueueableComp;
import com.jharter.game.ecs.components.Components.ActionQueuedComp;
import com.jharter.game.ecs.components.Components.ActivePlayerComp;
import com.jharter.game.ecs.components.Components.AutoSelectTurnActionComp;
import com.jharter.game.ecs.components.Components.CardOwnerComp;
import com.jharter.game.ecs.components.Components.CleanupTurnActionComp;
import com.jharter.game.ecs.components.Components.CursorComp;
import com.jharter.game.ecs.components.Components.TurnPhasePerformActionsComp;
import com.jharter.game.ecs.components.Components.TurnPhaseSelectActionsComp;
import com.jharter.game.ecs.components.Components.ZoneComp;
import com.jharter.game.ecs.components.subcomponents.TurnAction;
import com.jharter.game.ecs.components.subcomponents.TurnTimer;
import com.jharter.game.util.id.ID;

import uk.co.carelesslabs.Enums.CardOwnerAction;
import uk.co.carelesslabs.Enums.ZoneType;
import uk.co.carelesslabs.Media;

public class TurnPhaseSelectActionsSystem extends TurnPhaseSystem {
	
	public TurnPhaseSelectActionsSystem() {
		super(TurnPhaseSelectActionsComp.class, TurnPhasePerformActionsComp.class);
		add(CardOwnerComp.class);
		add(ActionQueuedComp.class);
		add(AutoSelectTurnActionComp.class);
		add(ActivePlayerComp.class);
	}

	@Override
	protected boolean processEntityPhaseStart(Entity turnEntity, float deltaTime) {
		if(!isDoneAnimating()) {
			return false;
		}
		computeWaitTimes();
		enableCursor();
		resetCursor();
		getTurnTimer().start();
		Media.startTurnBeep.play();
		return true;
	}

	@Override
	protected boolean processEntityPhaseMiddle(Entity turnEntity, float deltaTime) {
		TurnTimer t = getTurnTimer();
		t.increment(deltaTime);
		if(t.isOvertime()) {
			updateWaiting(deltaTime, true);
			return true;
		}
		
		boolean playersDone = comp(ActivePlayerComp.class).spentPlayers.size == getPlayerIDs().size();
		updateWaiting(deltaTime, playersDone);
		
		// XXX This assumption will change, but the intent is to check if all characters
		// have made a card selection. Currently, one cursor controls all actions so 
		// we'll leave this hack in for testing.
		return countEntities(ActionQueuedComp.class) == countEntities(CardOwnerComp.class);
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
	
	private void computeWaitTimes() {
		for(AutoSelectTurnActionComp a : comps(AutoSelectTurnActionComp.class)) {
			a.waitTime = MathUtils.random(a.minWaitTime, a.maxWaitTime);
			a.waited = 0;
			a.waiting = true;
		}
	}
	
	private void updateWaiting(float deltaTime, boolean force) {
		for(Entity entity : entities(AutoSelectTurnActionComp.class)) {
			AutoSelectTurnActionComp a = Comp.AutoSelectTurnActionComp.get(entity);
			if(!a.waiting) {
				continue;
			}
			
			a.waited += deltaTime;
			if(a.waited >= a.waitTime || force) {
				autoSelectTurnAction(Comp.IDComp.get(entity).id);
				a.waiting = false;
			}
		}
	}
	
	private void discardCards() {
		for(CardOwnerComp c : comps(CardOwnerComp.class)) {
			c.actions.add(CardOwnerAction.DISCARD_HAND);
		}
	}
	
	private void autoSelectTurnAction(ID ownerID) {
		ZoneComp handZone = getZone(ownerID, ZoneType.HAND);
		if(handZone.objectIDs.size == 0) {
			return;
		}
		ID cardID = handZone.objectIDs.first();
		Entity card = Comp.Entity.get(cardID);
		if(cardID != null) {
			TurnAction t = Comp.TurnActionComp.get(card).turnAction;
			for(ZoneType type : t.targetZoneTypes) {
				ZoneComp zTarget = getZone(ownerID, type);
				Array<ID> ids = new Array<ID>(zTarget.objectIDs);
				ids.shuffle();
				boolean success = false;
				for(ID targetID : ids) {
					Entity target = Comp.Entity.get(targetID);
					if(t.isValidTarget(target)) {
						t.addTarget(target);
						success = true;
						break;
					}
				}
				if(!success) {
					Comp.add(CleanupTurnActionComp.class, card);
					break;
				}
			}
			if(t.hasAllTargets()) {
				Comp.add(ActionQueueableComp.class, card).timestamp = TimeUtils.millis();
			}
		}
	}
	
}	