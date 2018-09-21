package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
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
import com.jharter.game.util.id.ID;

import uk.co.carelesslabs.Enums.ZoneType;
import uk.co.carelesslabs.Media;

public class CursorSelectSystem extends IteratingSystem {

	@SuppressWarnings("unchecked")
	public CursorSelectSystem() {
		super(Family.all(CursorComp.class,
				 CursorInputComp.class,
				 ZonePositionComp.class).get());
	}

	@Override
	public void processEntity(Entity cursor, float deltaTime) {
		CursorComp c = Comp.CursorComp.get(cursor);
		CursorInputComp ci = Comp.CursorInputComp.get(cursor);
		ZonePositionComp zp = Comp.ZonePositionComp.get(cursor);
		ZoneComp z = Comp.ZonePositionComp(zp).getZoneComp();
		ID playerID = Comp.Entity.Cursor(cursor).getPlayerID();
		
		// A little failsafe here for when zones are empty
		if(!Comp.ZoneComp(z).hasIndex(zp.index)) {
			ci.reset();
			return;
		}
		
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
			int index = zp.index;
			
			// Make sure we're accepting a valid target
			if(isTargetable(z, index)) {
				ID targetEntityID = z.objectIDs.get(index);
				Entity targetEntity = Comp.Entity.get(targetEntityID);
				
				// If this is our first target, make them the turn action entity
				if(t == null) {
					c.turnActionEntityID = targetEntityID;
					t = Comp.TurnActionComp.get(targetEntity).turnAction;
					Comp.getOrAdd(getEngine(), PendingTurnActionComp.class, targetEntity);
				} else {
					// Always add every object we select as we go to our turn action entity
					t.addTarget(targetEntity);
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
					checkpoint = false;
				}
				
				// Update our current cursor position based on our next object to select or
				// wether we should go back to the hand. We don't need to do an extra check
				// for validity here because we covered that at the top of this menu.
				ZoneComp targetZone = t.hasAllTargets() ? Comp.Find.ZoneComp.findZone(playerID, ZoneType.HAND) : Comp.Find.ZoneComp.findZone(playerID, t.getTargetZoneType());
				int targetIndex = 0; // Comp.Entity.Cursor(cursor).findFirstValidTargetInZone(playerID, targetZone.zoneType, t);
				
				ChangeZoneComp cz = Comp.create(getEngine(), ChangeZoneComp.class);
				cz.oldZoneID = z.zoneID;
				cz.newZoneID = targetZone.zoneID;
				cz.newIndex = targetIndex;
				cz.checkpoint = checkpoint;
				cursor.add(cz);
			
			}
			
		} else if(ci.cancel) {

			TurnAction t = Comp.CursorComp(c).turnAction();
			if(Comp.ZonePositionComp(zp).tryRevertToLastCheckpoint()) {
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
	
	private boolean isTargetable(ZoneComp zone, int index) {
		return index >= 0 && index < zone.objectIDs.size() && !Comp.UntargetableComp.has(zone.objectIDs.get(index));
	}
	
}
