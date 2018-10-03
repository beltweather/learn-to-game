package com.jharter.game.ecs.systems.turnphase;

import com.badlogic.ashley.core.Entity;
import com.jharter.game.ecs.components.Components.CardComp;
import com.jharter.game.ecs.components.Components.ChangeZoneComp;
import com.jharter.game.ecs.components.Components.TurnPhaseStartBattleComp;
import com.jharter.game.ecs.components.Components.TurnPhaseStartTurnComp;
import com.jharter.game.ecs.components.Components.ZoneComp;
import com.jharter.game.util.id.ID;

import uk.co.carelesslabs.Enums.ZoneType;

public class TurnPhaseStartBattleSystem extends TurnPhaseSystem {
	
	public TurnPhaseStartBattleSystem() {
		super(TurnPhaseStartBattleComp.class, TurnPhaseStartTurnComp.class);
		add(CardComp.class);
	}

	@Override
	protected boolean processEntityPhaseStart(Entity turnPhase, float deltaTime) {
		return true;
	}

	@Override
	protected boolean processEntityPhaseMiddle(Entity turnPhase, float deltaTime) {
		addCardsToDecks();
		return true;
	}

	@Override
	protected void processEntityPhaseEnd(Entity turnPhase, float deltaTime) {
		
	}
	
	private void addCardsToDecks() {
		// Move all cards in every zone to their decks
		for(Entity card : getEntities(CardComp.class)) {
			ID cardID = Comp.IDComp.get(card).id;
			ID ownerID = Comp.CardComp.get(card).ownerID;
			ZoneComp zCard = Comp.ZoneComp.get(Comp.ZonePositionComp.get(card).zoneID);
			ZoneComp zDeck = getZone(ownerID, ZoneType.DECK);
			if(zCard == null) {
				Comp.util(zDeck).add(cardID);
			} else if(zCard.zoneType != ZoneType.DECK) {
				Comp.util(zCard).remove(cardID);
				Comp.util(zDeck).add(cardID);
			}
		}
		
		// Shuffle each player's deck
		for(ID playerID : getPlayerIDs()) {
			getZone(playerID, ZoneType.DECK).objectIDs.shuffle();
		}
	}
	
}
