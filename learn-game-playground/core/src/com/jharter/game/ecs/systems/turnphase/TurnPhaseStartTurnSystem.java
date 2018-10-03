package com.jharter.game.ecs.systems.turnphase;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;
import com.jharter.game.ecs.components.Components.ActivePlayerComp;
import com.jharter.game.ecs.components.Components.ChangeZoneComp;
import com.jharter.game.ecs.components.Components.TurnPhaseSelectEnemyActionsComp;
import com.jharter.game.ecs.components.Components.TurnPhaseStartTurnComp;
import com.jharter.game.ecs.components.Components.ZoneComp;
import com.jharter.game.util.ArrayUtil;
import com.jharter.game.util.id.ID;

import uk.co.carelesslabs.Enums.ZoneType;

public class TurnPhaseStartTurnSystem extends TurnPhaseSystem {

	public TurnPhaseStartTurnSystem() {
		super(TurnPhaseStartTurnComp.class, TurnPhaseSelectEnemyActionsComp.class);
		add(ActivePlayerComp.class);
	}

	@Override
	protected boolean processEntityPhaseStart(Entity entity, float deltaTime) {
		return true;
	}

	@Override
	protected boolean processEntityPhaseMiddle(Entity entity, float deltaTime) {
		resetPlayers();
		resetEnemyCards();
		drawCards();
		return true;
	}

	@Override
	protected void processEntityPhaseEnd(Entity entity, float deltaTime) {
		
	}
	
	private void resetPlayers() {
		ActivePlayerComp a = getFirstComponent(ActivePlayerComp.class);
		setPlayer(a, 0);
		a.spentPlayers.clear();
	}
	
	private void resetEnemyCards() {
		ZoneComp z = getZone(null, ZoneType.ENEMY);
		for(ID enemyID: z.objectIDs) {
			ZoneComp zDiscard = getZone(enemyID, ZoneType.DISCARD);
			ZoneComp zDeck = getZone(enemyID, ZoneType.DECK);
			for(ID cardID : new Array<ID>(zDiscard.objectIDs)) {
				Comp.util(zDiscard).remove(cardID);
				Comp.util(zDeck).add(cardID);
			}
		}
	}
	
	private void drawCards() {
		int handSize = 3;
		for(ID playerID : getPlayerIDs()) {
			ZoneComp zDeck = getZone(playerID, ZoneType.DECK);
			ZoneComp zHand = getZone(playerID, ZoneType.HAND);
			int draw = handSize - zHand.objectIDs.size;
			ZoneComp zDiscard = getZone(playerID, ZoneType.DISCARD);
			boolean shuffledDiscard = false;
			for(int i = 0; i < draw; i++) {
				
				// Shuffle our discard pile into our deck
				if(!shuffledDiscard && i >= zDeck.objectIDs.size) {
					zDiscard.objectIDs.shuffle();
					for(ID discardID : new Array<ID>(zDiscard.objectIDs)) {
						Comp.util(zDiscard).remove(discardID);
						Comp.util(zDeck).add(discardID);
					}
					shuffledDiscard = true;
				}
				
				// If we've already shuffled in our discard and we still
				// don't have enough cards to draw, don't draw
				if(shuffledDiscard && i >= zDeck.objectIDs.size) {
					break;
				}
				
				// Draw a card
				Comp.util(Comp.add(ChangeZoneComp.class, zDeck.objectIDs.get(i))).change(zDeck.zoneID, zHand.zoneID);
			}
		}
	}
	
	private void setPlayer(ActivePlayerComp a, int index) {
		if(!ArrayUtil.has(getPlayerIDs(), index)) {
			index = 0;
		}
		a.activePlayerID = getPlayerIDs().get(index);
	}
	
}
