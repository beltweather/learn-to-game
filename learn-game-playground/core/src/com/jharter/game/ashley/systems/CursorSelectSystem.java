package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.jharter.game.ashley.components.Comp;
import com.jharter.game.ashley.components.Components.ActionQueueableComp;
import com.jharter.game.ashley.components.Components.ActivePlayerComp;
import com.jharter.game.ashley.components.Components.ActiveTurnActionComp;
import com.jharter.game.ashley.components.Components.ChangeZoneComp;
import com.jharter.game.ashley.components.Components.CursorComp;
import com.jharter.game.ashley.components.Components.CursorInputComp;
import com.jharter.game.ashley.components.Components.PendingTurnActionComp;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.Components.ZonePositionComp;
import com.jharter.game.ashley.components.subcomponents.TurnAction;
import com.jharter.game.ashley.systems.boilerplate.FirstSystem;
import com.jharter.game.util.id.ID;

import uk.co.carelesslabs.Enums.ZoneType;
import uk.co.carelesslabs.Media;

public class CursorSelectSystem extends FirstSystem {

	@SuppressWarnings("unchecked")
	public CursorSelectSystem() {
		super(Family.all(CursorComp.class, CursorInputComp.class).get());
	}

	@Override
	public void processEntity(Entity cursor, float deltaTime) {
		CursorComp c = Comp.CursorComp.get(cursor);
		if(c.targetID == null) {
			return;
		}
		CursorInputComp ci = Comp.CursorInputComp.get(cursor);
		
		Entity target = Comp.Entity.get(c.targetID);
		ZonePositionComp zp = Comp.ZonePositionComp.get(target);
		if(zp == null) {
			return;
		}
		ZoneComp z = Comp.ZonePositionComp(zp).getZoneComp();
		ID playerID = Comp.Entity.Cursor(cursor).getPlayerID();
		
		ActivePlayerComp a = Comp.Entity.DefaultTurn().ActivePlayerComp();
		
		if(ci.prev) {
			
			if(Comp.ActivePlayerComp(a).prevPlayer()) {
				Comp.Entity.Cursor(cursor).toHand(getEngine());
			}
			
		} else if(ci.next) {

			if(Comp.ActivePlayerComp(a).nextPlayer()) {
				Comp.Entity.Cursor(cursor).toHand(getEngine());
			}
			
		} else if(ci.accept) {
			Media.acceptBeep.play();
			
			TurnAction t = Comp.CursorComp(c).turnAction();
			ID targetEntityID = c.targetID;
				
			// If this is our first target, make them the turn action entity
			if(t == null) {
				c.turnActionEntityID = targetEntityID;
				t = Comp.TurnActionComp.get(target).turnAction;
				Comp.getOrAdd(getEngine(), PendingTurnActionComp.class, target);
			} else {
				// Always add every object we select as we go to our turn action entity
				t.addTarget(target);
			}
			
			// If we're done selecting objects, do some clean up and pass our turn action
			// entity on to the next step
			boolean checkpoint = true;
			if(t.hasAllTargets()) {
				// Handle logic for next active player given cursor selection
				Comp.ActivePlayerComp(a).nextPlayer();
				a.spentPlayers.add(playerID);
				playerID = Comp.Entity.Cursor(cursor).getPlayerID();
				
				Entity turnActionEntity = Comp.Entity.get(c.turnActionEntityID);
				Comp.remove(PendingTurnActionComp.class, turnActionEntity);
				turnActionEntity.add(Comp.create(getEngine(), ActionQueueableComp.class));
				c.turnActionEntityID = null;
				Comp.ZonePositionComp(zp).clearHistory();
				c.history.clear();
				checkpoint = false;
			}
			
			ZoneComp targetZone = t.hasAllTargets() ? Comp.Find.ZoneComp.findZone(playerID, ZoneType.HAND) : Comp.Find.ZoneComp.findZone(playerID, t.getTargetZoneType());
			if(!t.hasAllTargets() && c.targetID != null) {
				c.history.add(c.targetID);
			}
			c.targetID = targetZone.objectIDs.first();
			
		} else if(ci.cancel) {

			TurnAction t = Comp.CursorComp(c).turnAction();
			if(tryRevertToLastTarget(c)) {
				Media.cancelBeep.play();	
				cursor.remove(ActiveTurnActionComp.class);
				if(t != null) {
					if(t.targetIDs.size == 0) {
						Comp.CursorComp(c).cancelTurnAction(getEngine());
					} else if(t.targetIDs.size > 0) {
						t.targetIDs.pop();
					}
				}
				Comp.add(getEngine(), ChangeZoneComp.class, cursor);
			}
		}
		
		ci.reset();
	}
	
	private boolean tryRevertToLastTarget(CursorComp c) {
		if(c.history.size == 0) {
			return false;
		}
		c.targetID = c.history.pop();
		return true;
	}
}
