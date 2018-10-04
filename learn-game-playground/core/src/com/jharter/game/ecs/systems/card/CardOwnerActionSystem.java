package com.jharter.game.ecs.systems.card;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.utils.Array;
import com.jharter.game.ecs.components.Components.CardOwnerComp;
import com.jharter.game.ecs.components.Components.ChangeZoneComp;
import com.jharter.game.ecs.components.Components.IDComp;
import com.jharter.game.ecs.components.Components.ZoneComp;
import com.jharter.game.ecs.systems.boilerplate.GameIteratingSystem;
import com.jharter.game.util.id.ID;

import uk.co.carelesslabs.Enums.CardOwnerAction;
import uk.co.carelesslabs.Enums.ZoneType;

public class CardOwnerActionSystem extends GameIteratingSystem {

	public CardOwnerActionSystem() {
		super(Family.all(CardOwnerComp.class, IDComp.class).get());
	}

	@Override
	protected void processEntity(Entity cardOwner, float deltaTime) {
		ID id = Comp.IDComp.get(cardOwner).id;
		CardOwnerComp c = Comp.CardOwnerComp.get(cardOwner);
		
		setup(id, c);
		discard(id, c);
		draw(id, c);
	}
	
	protected boolean has(CardOwnerComp c, CardOwnerAction a) {
		if(c.actions.contains(a)) {
			c.actions.remove(a);
			return true;
		}
		return false;
	}
	
	protected void setup(ID id, CardOwnerComp c) {
		if(has(c, CardOwnerAction.RESET_CARDS)) {
			sendCardsToDeck(id, c);
		}
	}
	
	protected void sendCardsToDeck(ID id, CardOwnerComp c) {
		ZoneComp zDeck = getZone(id, ZoneType.DECK);
		for(ID cardID : c.cardIDs) {
			ZoneComp zCard = Comp.ZoneComp.get(Comp.ZonePositionComp.get(cardID).zoneID);
			if(zCard == null) {
				Comp.util(zDeck).add(cardID);
			} else if(zCard.zoneType != ZoneType.DECK) {
				Comp.util(zCard).remove(cardID);
				Comp.util(zDeck).add(cardID);
			}
		}
		zDeck.objectIDs.shuffle();
	}
	
	protected void discard(ID id, CardOwnerComp c) {
		if(has(c, CardOwnerAction.DISCARD_HAND)) {
			discardHand(id, c);
		}
	}
	
	protected void discardHand(ID id, CardOwnerComp c) {
		ZoneComp zHand = getZone(id, ZoneType.HAND);
		ZoneComp zDiscard = getZone(id, ZoneType.DISCARD);
		for(ID cardID : zHand.objectIDs) {
			changeZone(cardID, zHand, zDiscard);
		}
	}
	
	protected void draw(ID id, CardOwnerComp c) {
		
		if(has(c, CardOwnerAction.FILL_HAND)) {
			setDrawToFillHand(id, c);
		}
		
		if(c.draw < 0) {
			c.draw = 0;
			return;
		}
		
		if(c.draw == 0) {
			return;
		}
		
		ZoneComp zDeck = getZone(id, ZoneType.DECK);
		ZoneComp zHand = getZone(id, ZoneType.HAND);
		ZoneComp zDiscard = getZone(id, ZoneType.DISCARD);
		boolean shuffledDiscard = false;
		for(int i = 0; i < c.draw; i++) {
			
			// Shuffle our discard pile into our deck
			if(!shuffledDiscard && i >= zDeck.objectIDs.size) {
				zDiscard.objectIDs.shuffle();
				moveAll(zDiscard, zDeck);
				shuffledDiscard = true;
			}
			
			// If we've already shuffled in our discard and we still
			// don't have enough cards to draw, don't draw
			if(shuffledDiscard && i >= zDeck.objectIDs.size) {
				break;
			}
			
			// Draw a card
			changeZone(zDeck.objectIDs.get(i), zDeck, zHand);
		}
		
		c.draw = 0;
	}
	
	protected void setDrawToFillHand(ID id, CardOwnerComp c) {
		int draw = c.handSize - getZone(id, ZoneType.HAND).objectIDs.size;
		if(c.draw < draw) {
			c.draw = draw;
		}
	}
	
	protected void changeZone(ID id, ZoneComp zFrom, ZoneComp zTo) {
		Comp.util(Comp.add(ChangeZoneComp.class, id)).change(zFrom.zoneID, zTo.zoneID);
	}
	
	protected void move(ID id, ZoneComp zFrom, ZoneComp zTo) {
		Comp.util(zFrom).remove(id);
		Comp.util(zTo).add(id);
	}
	
	protected void moveAll(ZoneComp zFrom, ZoneComp zTo) {
		for(ID id : new Array<ID>(zFrom.objectIDs)) {
			Comp.util(zFrom).remove(id);
			Comp.util(zTo).add(id);
		}
	}

}
