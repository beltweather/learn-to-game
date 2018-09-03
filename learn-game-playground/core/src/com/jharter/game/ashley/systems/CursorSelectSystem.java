package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.jharter.game.ashley.components.Comp;
import com.jharter.game.ashley.components.Components.ActionQueueableComp;
import com.jharter.game.ashley.components.Components.ActiveCardComp;
import com.jharter.game.ashley.components.Components.ChangeZoneComp;
import com.jharter.game.ashley.components.Components.CursorComp;
import com.jharter.game.ashley.components.Components.CursorInputComp;
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
		ZoneComp z = zp.getZoneComp();
		ID playerID = Comp.Method.CursorComp.getPlayerID(c);
		
		// A little failsafe here for when zones are empty
		if(!z.hasIndex(zp.index)) {
			ci.reset();
			return;
		}

		if(ci.accept) {
			Media.acceptBeep.play();
			
			TurnAction t = Comp.Method.CursorComp.getTurnAction(c);
			int index = zp.index;
			
			// Make sure we're accepting a valid target
			if(Comp.Method.CursorComp.isValidTarget(cursor)) {
				ID targetEntityID = z.objectIDs.get(index);
				Entity targetEntity = Comp.Entity.get(targetEntityID);
				
				// If this is our first target, make them the turn action entity
				if(t == null) {
					c.turnActionEntityID = targetEntityID;
					t = Comp.TurnActionComp.get(targetEntity).turnAction;
				} else {
					// Always add every object we select as we go to our turn action entity
					t.addTarget(targetEntity);
				}
				
				// If we're done selecting objects, do some clean up and pass our turn action
				// entity on to the next step
				boolean checkpoint = true;
				if(t.hasAllTargets()) {
					// Handle logic for next active player given cursor selection
					Comp.Method.ActivePlayerComp.nextPlayer(Comp.Entity.TurnEntity.ActivePlayerComp());
					playerID = Comp.Method.CursorComp.getPlayerID(c);
					
					Entity turnActionEntity = Comp.Entity.get(c.turnActionEntityID);
					turnActionEntity.add(Comp.create(getEngine(), ActionQueueableComp.class));
					c.turnActionEntityID = null;
					zp.clearHistory();
					checkpoint = false;
				}
				
				// Update our current cursor position based on our next object to select or
				// wether we should go back to the hand. We don't need to do an extra check
				// for validity here because we covered that at the top of this menu.
				ZoneComp targetZone = t.hasAllTargets() ? Comp.Method.ZoneComp.get(playerID, ZoneType.HAND) : Comp.Method.ZoneComp.get(playerID, t.getTargetZoneType());
				int targetIndex = Comp.Method.CursorComp.findFirstValidTargetInZone(playerID, targetZone.zoneType, t);
				
				ChangeZoneComp cz = Comp.create(getEngine(), ChangeZoneComp.class);
				cz.oldZoneID = z.zoneID;
				cz.newZoneID = targetZone.zoneID;
				cz.newIndex = targetIndex;
				cz.checkpoint = checkpoint;
				cursor.add(cz);
			
			}
			
		} else if(ci.cancel) {
			

			TurnAction t = Comp.Method.CursorComp.getTurnAction(c);
			if(zp.tryRevertToLastCheckpoint()) {
				Media.cancelBeep.play();	
				cursor.remove(ActiveCardComp.class);
				if(t != null) {
					if(t.targetIDs.size > 0) {
						t.targetIDs.pop();
					}
					if(t.targetIDs.size == 0) {
						c.turnActionEntityID = null;
					}
				}
				ChangeZoneComp cz = Comp.create(getEngine(), ChangeZoneComp.class);
				cursor.add(cz);
			}			
		}
		
		ci.reset();
	}
	
}
