package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.jharter.game.ashley.components.Components.ActionQueueableComp;
import com.jharter.game.ashley.components.Components.ActiveCardComp;
import com.jharter.game.ashley.components.Components.CursorComp;
import com.jharter.game.ashley.components.Components.CursorInputComp;
import com.jharter.game.ashley.components.Components.TurnActionComp;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.Components.ZonePositionComp;
import com.jharter.game.ashley.components.Mapper;
import com.jharter.game.util.id.ID;

import uk.co.carelesslabs.Enums.ZoneType;

public class CursorSelectSystem extends AbstractCursorOperationSystem {

	@SuppressWarnings("unchecked")
	public CursorSelectSystem() {
		super();
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		CursorComp c = Mapper.CursorComp.get(entity);
		CursorInputComp ci = Mapper.CursorInputComp.get(entity);
		ZonePositionComp zp = Mapper.ZonePositionComp.get(entity);
		ZoneComp z = zp.getZoneComp();
		
		// A little failsafe here for when zones are empty
		if(!z.hasIndex(zp)) {
			ci.reset();
			return;
		}

		if(ci.accept) {
			
			TurnActionComp t = c.getTurnActionComp();
			int index = zp.index();
			
			// Make sure we're accepting a valid target
			if(hasValidTarget(z.zoneType(), t, index, 0)) {
				ID targetEntityID = z.getID(zp);
				Entity targetEntity = Mapper.Entity.get(targetEntityID);
				
				// If this is our first target, make them the turn action entity
				if(t == null) {
					c.turnActionEntityID = targetEntityID;
					t = Mapper.TurnActionComp.get(targetEntity);
				}
				
				// Always add every object we select as we go to our turn action entity
				t.addTarget(targetEntity);
				
				// Update our current cursor position based on our next object to select or
				// wether we should go back to the hand
				ZoneType targetZoneType = t.hasAllTargets() ? ZoneType.HAND : t.getTargetZoneType();
				int targetIndex = findNextValidTarget(targetZoneType, t, -1, 1);
				
				zp.checkpoint();
				zp.index(targetIndex);
				zp.zoneType(targetZoneType);
				
				// If we're done selecting objects, do some clean up and pass our turn action
				// entity on to the next step
				if(t.hasAllTargets()) {
					Entity turnActionEntity = Mapper.Entity.get(c.turnActionEntityID);
					turnActionEntity.add(Mapper.Comp.get(ActionQueueableComp.class));
					c.turnActionEntityID = null;
					zp.clearHistory();
				}
			}
			
		} else if(ci.cancel) {

			TurnActionComp t = c.getTurnActionComp();
			if(zp.tryRevertToLastCheckpoint()) {
				entity.remove(ActiveCardComp.class);
				if(t != null) {
					t.targetIDs.pop();
					if(t.targetIDs.size == 0) {
						c.turnActionEntityID = null;
					}
				}
			}			
		}
		
		ci.reset();
	}
	
}
