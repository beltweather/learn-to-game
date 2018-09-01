package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.jharter.game.ashley.components.Components.ActionQueueableComp;
import com.jharter.game.ashley.components.Components.ActiveCardComp;
import com.jharter.game.ashley.components.Components.ChangeZoneComp;
import com.jharter.game.ashley.components.Components.CursorComp;
import com.jharter.game.ashley.components.Components.CursorInputComp;
import com.jharter.game.ashley.components.Components.DisabledComp;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.Components.ZonePositionComp;
import com.jharter.game.ashley.components.M;
import com.jharter.game.ashley.components.subcomponents.CompLinker;
import com.jharter.game.ashley.components.subcomponents.TurnAction;
import com.jharter.game.util.id.ID;

import uk.co.carelesslabs.Enums.ZoneType;

public class CursorSelectSystem extends AbstractCursorOperationSystem {

	@SuppressWarnings("unchecked")
	public CursorSelectSystem() {
		super(Family.all(CursorComp.class,
				 CursorInputComp.class,
				 ZonePositionComp.class).get());
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		CursorComp c = M.CursorComp.get(entity);
		CursorInputComp ci = M.CursorInputComp.get(entity);
		ZonePositionComp zp = M.ZonePositionComp.get(entity);
		ZoneComp z = zp.getZoneComp();
		
		// A little failsafe here for when zones are empty
		if(!z.hasIndex(zp.index)) {
			ci.reset();
			return;
		}

		if(ci.accept) {
			
			TurnAction t = CompLinker.CursorComp.getTurnAction(c);
			int index = zp.index;
			
			// Make sure we're accepting a valid target
			if(isValidTarget(c.playerID(), z.zoneType, t, index)) {
				ID targetEntityID = z.objectIDs.get(index);
				Entity targetEntity = M.Entity.get(targetEntityID);
				
				// If this is our first target, make them the turn action entity
				if(t == null) {
					c.turnActionEntityID = targetEntityID;
					t = M.TurnActionComp.get(targetEntity).turnAction;
				}
				
				// Always add every object we select as we go to our turn action entity
				t.addTarget(targetEntity);
				
				// If we're done selecting objects, do some clean up and pass our turn action
				// entity on to the next step
				boolean checkpoint = true;
				if(t.hasAllTargets()) {
					// Handle logic for next active player given cursor selection
					M.nextActivePlayerEntity();
					
					Entity turnActionEntity = M.Entity.get(c.turnActionEntityID);
					turnActionEntity.add(M.Comp.get(ActionQueueableComp.class));
					c.turnActionEntityID = null;
					zp.clearHistory();
					checkpoint = false;
				}
				
				// Update our current cursor position based on our next object to select or
				// wether we should go back to the hand. We don't need to do an extra check
				// for validity here because we covered that at the top of this menu.
				ZoneComp targetZone = t.hasAllTargets() ? M.ZoneComp.get(c.playerID(), ZoneType.HAND) : M.ZoneComp.get(c.playerID(), t.getTargetZoneType());
				int targetIndex = findFirstValidTargetInZone(c.playerID(), targetZone.zoneType, t);
				
				ChangeZoneComp cz = M.Comp.get(ChangeZoneComp.class);
				cz.oldZoneID = z.zoneID;
				cz.newZoneID = targetZone.zoneID;
				cz.newIndex = targetIndex;
				cz.checkpoint = checkpoint;
				entity.add(cz);
			}
			
		} else if(ci.cancel) {

			TurnAction t = CompLinker.CursorComp.getTurnAction(c);
			if(zp.tryRevertToLastCheckpoint()) {
				entity.remove(ActiveCardComp.class);
				if(t != null) {
					t.targetIDs.pop();
					if(t.targetIDs.size == 0) {
						c.turnActionEntityID = null;
					}
				}
				ChangeZoneComp cz = M.Comp.get(ChangeZoneComp.class);
				entity.add(cz);
			}			
		}
		
		ci.reset();
	}
	
}
