package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.jharter.game.ashley.components.Comp;
import com.jharter.game.ashley.components.Components.ActivePlayerComp;
import com.jharter.game.ashley.components.Components.CursorComp;
import com.jharter.game.ashley.components.Components.TargetableComp;
import com.jharter.game.ashley.components.Components.TurnActionComp;
import com.jharter.game.ashley.components.Components.PendingTurnActionComp;
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
		super(Family.all(ZoneComp.class).get());
		add(TurnActionComp.class, Family.all(TurnActionComp.class, PendingTurnActionComp.class).get());
		add(ActivePlayerComp.class, Family.all(ActivePlayerComp.class).get());
		add(ZonePositionComp.class, Family.all(CursorComp.class, ZonePositionComp.class).get());
	}
	
	@Override
	public void processEntity(Entity zone, float deltaTime) {
		TurnAction t = getTurnAction();
		ZoneType cursorZoneType = getCursorZoneType();
		ZoneComp z = Comp.ZoneComp.get(zone);
		boolean isCursorZone = z.zoneType == cursorZoneType;
		
		if(!isCursorEnabled()) {
			for(ID id : z.objectIDs) {
				clearTargeting(id);
			}
		} else if(!isCursorZone) {
			if(t == null) {
				for(ID id : z.objectIDs) {
					clearTargeting(id);
				}
			} else {
				for(ID id : z.objectIDs) {
					makeUntargetable(id);
				}
			}
		} else {
			for(ID id : z.objectIDs) {
				if(isValid(id, t)) {
					makeTargetable(id);
				} else {
					makeUntargetable(id);
				}
			}
		}
	}
	
	protected boolean isCursorEnabled() {
		return !Comp.DisabledComp.has(getFirstEntity(ZonePositionComp.class));
	}
	
	protected TurnAction getTurnAction() {
		TurnActionComp t = getFirstComponent(TurnActionComp.class);
		return t == null ? null : t.turnAction;
	}
	
	protected ID getActivePlayerID() {
		ActivePlayerComp a = getFirstComponent(ActivePlayerComp.class);
		return a == null ? null : a.activePlayerID;
	}
	
	protected ZoneType getCursorZoneType() {
		ZonePositionComp zp = getFirstComponent(ZonePositionComp.class);
		return zp == null ? null : Comp.ZoneComp.get(zp.zoneID).zoneType;
	}
	
	protected ZoneComp getZone(ZoneType zoneType) {
		return Comp.Find.ZoneComp.findZone(getActivePlayerID(), zoneType);
	}
	
	protected void makeTargetable(ID id) {
		Entity zoneObject = Comp.Entity.get(id);
		Comp.add(getEngine(), TargetableComp.class, zoneObject);
		Comp.remove(UntargetableComp.class, zoneObject);
	}
	
	protected void clearTargeting(ID id) {
		Entity zoneObject = Comp.Entity.get(id);
		Comp.remove(TargetableComp.class, zoneObject);
		Comp.remove(UntargetableComp.class, zoneObject);
	}
	
	protected void makeUntargetable(ID id) {
		Entity zoneObject = Comp.Entity.get(id);
		Comp.remove(TargetableComp.class, zoneObject);
		Comp.add(getEngine(), UntargetableComp.class, zoneObject);
	}
	
	protected boolean isValid(ID id) {
		return isValid(id, null);
	}
	
	protected boolean isValid(ID id, TurnAction t) {
		// Check that the this entity's turn action has a valid next target
		if(t == null) {
			t = Comp.TurnActionComp.get(Comp.Entity.get(id)).turnAction;
			return hasValidTarget(getActivePlayerID(), t.getTargetZoneType(), t, 0, 1, 0);
		}
		// Otherwise, check if this entity is a valid target for the current turn action
		ZonePositionComp zp = Comp.ZonePositionComp.get(Comp.Entity.get(id));
		return hasValidTarget(getActivePlayerID(), t.getTargetZoneType(), t, zp.index, 0, 0);
	}
	
	private boolean hasValidTarget(ID ownerID, ZoneType zoneType, TurnAction t, int index, int direction, int depth) {
		return findNextValidTarget(ownerID, zoneType, t, index, direction, depth) >= 0;
	}
	
	private int findNextValidTarget(ID ownerID, ZoneType zoneType, TurnAction turnAction, int index, int direction, int depth) {
		ZoneComp z = Comp.Find.ZoneComp.findZone(ownerID, zoneType);
		if(z == null) {
			int j = 0;
		}
		return ArrayUtil.findNextIndex(z.objectIDs, index, direction, (id, args) -> {
			
			ID ownerIDArg = (ID) args[0];
			TurnAction tArg = (TurnAction) args[1];
			int depthArg = (int) args[2];
			
			Entity entity = Comp.Entity.get(id);
			if(entity != null && (tArg == null || tArg.isValidTarget(entity))) {
				if(tArg == null) {
					TurnActionComp taComp = Comp.TurnActionComp.get(entity);
					if(taComp != null) {
						TurnAction ta = taComp.turnAction;
						ZoneType nextZoneType = ta.getNextTargetZoneType(depthArg);
						if(nextZoneType == ZoneType.NONE || hasValidTarget(ownerIDArg, nextZoneType, ta, 0, 1, depthArg+1)) {
							return true;
						}
					}
				} else {
					return true;
				}
			}
			return false;
		
		}, ownerID, turnAction, depth);
	}
	
}
