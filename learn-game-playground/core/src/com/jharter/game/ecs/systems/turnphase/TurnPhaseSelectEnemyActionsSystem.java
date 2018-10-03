package com.jharter.game.ecs.systems.turnphase;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;
import com.jharter.game.ecs.components.Components.ActionQueueableComp;
import com.jharter.game.ecs.components.Components.TurnPhaseSelectEnemyActionsComp;
import com.jharter.game.ecs.components.Components.TurnPhaseSelectFriendActionsComp;
import com.jharter.game.ecs.components.Components.ZoneComp;
import com.jharter.game.ecs.components.subcomponents.TurnAction;
import com.jharter.game.util.id.ID;

import uk.co.carelesslabs.Enums.ZoneType;

public class TurnPhaseSelectEnemyActionsSystem extends TurnPhaseSystem {
	
	private float wait;
	private float beginWait;
	
	public TurnPhaseSelectEnemyActionsSystem() {
		super(TurnPhaseSelectEnemyActionsComp.class, TurnPhaseSelectFriendActionsComp.class);
	}

	@Override
	protected boolean processEntityPhaseStart(Entity entity, float deltaTime) {
		wait = 0;
		beginWait = deltaTime;
		return isDoneAnimating();
	}

	@Override
	protected boolean processEntityPhaseMiddle(Entity entity, float deltaTime) {
		beginWait += deltaTime;
		if(beginWait > wait) {
			selectEnemyTurnActions();
			return true;
		}
		return false;
	}

	@Override
	protected void processEntityPhaseEnd(Entity entity, float deltaTime) {
		
	}
	
	private void selectEnemyTurnActions() {
		ZoneComp enemyZone = getZone(null, ZoneType.ENEMY);
		for(ID enemyID : enemyZone.objectIDs) {
			selectEnemyTurnAction(enemyID);
		}
	}
	
	private void selectEnemyTurnAction(ID enemyID) {
		ZoneComp deckZone = getZone(enemyID, ZoneType.DECK);
		ID cardID = deckZone.objectIDs.first();
		Entity card = Comp.Entity.get(cardID);
		if(cardID != null) {
			TurnAction t = Comp.TurnActionComp.get(card).turnAction;
			for(ZoneType type : t.targetZoneTypes) {
				ZoneComp zTarget = getZone(enemyID, type);
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
					t.cleanUp();
					break;
				}
			}
			if(t.hasAllTargets()) {
				Comp.add(ActionQueueableComp.class, card);
			}
		}
	}
	
}
