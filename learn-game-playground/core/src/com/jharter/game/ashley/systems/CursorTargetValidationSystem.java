package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.jharter.game.ashley.components.Comp;
import com.jharter.game.ashley.components.Components.ActivePlayerComp;
import com.jharter.game.ashley.components.Components.CursorComp;
import com.jharter.game.ashley.components.Components.InvisibleComp;
import com.jharter.game.ashley.components.Components.PendingTurnActionComp;
import com.jharter.game.ashley.components.Components.TargetableComp;
import com.jharter.game.ashley.components.Components.TurnActionComp;
import com.jharter.game.ashley.components.Components.UntargetableComp;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.Components.ZonePositionComp;
import com.jharter.game.ashley.components.subcomponents.TurnAction;
import com.jharter.game.ashley.systems.boilerplate.CustomIteratingSystem;
import com.jharter.game.util.ArrayUtil;
import com.jharter.game.util.id.ID;

import uk.co.carelesslabs.Enums.ZoneType;

public class CursorTargetValidationSystem extends CustomIteratingSystem {
	
	@SuppressWarnings("unchecked")
	public CursorTargetValidationSystem() {
		super(Family.all(ZoneComp.class).exclude(InvisibleComp.class).get());
		add(TurnActionComp.class, Family.all(TurnActionComp.class, PendingTurnActionComp.class).get());
		add(ActivePlayerComp.class, Family.all(ActivePlayerComp.class).get());
		add(CursorComp.class, Family.all(CursorComp.class).get());
	}
	
	@Override
	public void processEntity(Entity zone, float deltaTime) {
		ZoneComp z = Comp.ZoneComp.get(zone);
		TurnAction t = getTurnAction();
		boolean isTargetZone = z.zoneType == (t == null ? ZoneType.HAND : t.getTargetZoneType());
		
		// If there's no cursor we don't want to reason on targets
		if(isCursorDisabled()) {
			for(ID id : z.objectIDs) { clearTargeting(id); }
			
		// If we're not in the target zone, see if we're even using a turn action
		} else if(!isTargetZone) {
			
			// If we're not targeting with any turn action, don't reason on targets
			if(t == null) {
				for(ID id : z.objectIDs) { clearTargeting(id); }
			
			// Otherwise, make these untargetable because they're not in the target zone
			} else {
				for(ID id : z.objectIDs) { makeUntargetable(id); }
			}
			
		} else {
			
			// If we're in the target zone and the cursor is enabled, then make these
			// targetable if they're actually valid, otherwise, untargetable
			int targetable = 0;
			for(ID id : z.objectIDs) { 
				if(isValid(id, t)) { 
					makeTargetable(id); 
					targetable++;
				} else { 
					makeUntargetable(id); 
				} 
			}
			//Sys.out.println("Made " + targetable + " entities targetable.");
			
		}
	}
	
	private void makeTargetable(ID id) {
		Entity zoneObject = Comp.Entity.get(id);
		Comp.add(getEngine(), TargetableComp.class, zoneObject);
		Comp.remove(UntargetableComp.class, zoneObject);
	}
	
	private void clearTargeting(ID id) {
		Entity zoneObject = Comp.Entity.get(id);
		Comp.remove(TargetableComp.class, zoneObject);
		Comp.remove(UntargetableComp.class, zoneObject);
	}
	
	private void makeUntargetable(ID id) {
		Entity zoneObject = Comp.Entity.get(id);
		Comp.remove(TargetableComp.class, zoneObject);
		Comp.add(getEngine(), UntargetableComp.class, zoneObject);
	}
	
	private boolean isCursorDisabled() {
		return Comp.DisabledComp.has(getFirstEntity(CursorComp.class));
	}
	
	private TurnAction getTurnAction() {
		TurnActionComp t = getFirstComponent(TurnActionComp.class);
		return t == null ? null : t.turnAction;
	}
	
	private ID getActivePlayerID() {
		ActivePlayerComp a = getFirstComponent(ActivePlayerComp.class);
		return a == null ? null : a.activePlayerID;
	}
	
	private ZoneComp getZone(ZoneType zoneType) {
		return Comp.Find.ZoneComp.findZone(getActivePlayerID(), zoneType);
	}
	
	private boolean isValid(ID targetID, TurnAction t) {
		Entity target = Comp.Entity.get(targetID);
		if(Comp.InvisibleComp.has(target)) {
			return false;
		}
		
		// Check that the this entity's turn action has a valid next target
		if(t == null) {
			t = Comp.TurnActionComp.get(target).turnAction;
			return isValid(t.getTargetZoneType(), t, 0, 1, 0);
		}
		// Otherwise, check if this entity is a valid target for the current turn action
		ZonePositionComp zp = Comp.ZonePositionComp.get(target);
		return isValid(t.getTargetZoneType(), t, zp.index, 0, 0);
	}
	
	private boolean isValid(ZoneType zoneType, TurnAction turnAction, int index, int direction, int depth) {
		ZoneComp z = getZone(zoneType);
		return ArrayUtil.findNextIndex(z.objectIDs, index, direction, (id, args) -> { TurnAction _turnAction = (TurnAction) args[0]; int _depth = (int) args[1];
			
			// If we can't find the initial entity, it's an invalid target
			Entity entity = Comp.Entity.get(id);
			if(entity == null) {
				return false;
			}

			// If we have a turn action, use its validation
			if(_turnAction != null) {
				return _turnAction.isValidTarget(entity);
			}
		
			// Otherwise, try to get a turn action from our target.
			// If our target doesn't have a turn action, there's no validation we
			// can do, consider ourselves invalid.
			TurnActionComp t = Comp.TurnActionComp.get(entity);
			if(t == null) {
				return false;
			}
			
			// Otherwise, use the turn action's validation
			ZoneType nextZoneType = t.turnAction.getNextTargetZoneType(_depth);
			return nextZoneType == ZoneType.NONE || isValid(nextZoneType, t.turnAction, 0, 1, _depth+1);
		
		}, turnAction, depth) >= 0;
	}
	
}
