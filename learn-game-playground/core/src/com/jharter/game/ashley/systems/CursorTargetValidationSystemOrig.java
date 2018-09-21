package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.jharter.game.ashley.components.Comp;
import com.jharter.game.ashley.components.Components.TurnActionComp;
import com.jharter.game.ashley.components.Components.UntargetableComp;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.subcomponents.TurnAction;
import com.jharter.game.util.ArrayUtil;
import com.jharter.game.util.id.ID;

import uk.co.carelesslabs.Enums.ZoneType;

public class CursorTargetValidationSystemOrig extends IteratingSystem {

	private ZoneComp zCursor;
	
	@SuppressWarnings("unchecked")
	public CursorTargetValidationSystemOrig() {
		super(Family.all(ZoneComp.class).get());
	}
	
	@Override
	public void update(float deltaTime) {
		zCursor = Comp.ZonePositionComp(Comp.ZonePositionComp.get(Comp.Entity.DefaultCursor().Entity())).getZoneComp();
		super.update(deltaTime);
	}

	@Override
	public void processEntity(Entity zone, float deltaTime) {
		ZoneComp z = Comp.ZoneComp.get(zone);
		if(z.zoneID == zCursor.zoneID) {
			handleZoneWithCursor(z);
		} else {
			handleZoneWithoutCursor(z);
		}
	}
	
	public void handleZoneWithCursor(ZoneComp z) {
		for(int i = 0; i < z.objectIDs.size(); i++) {
			Entity zoneItem = Comp.Entity.get(z.objectIDs.get(i));
			if(!isValidTarget(i)) {
				Comp.add(getEngine(), UntargetableComp.class, zoneItem);
			} else {
				Comp.remove(UntargetableComp.class, zoneItem);
			}
		}
	}
	
	public void handleZoneWithoutCursor(ZoneComp z) {
		switch(z.zoneType) {
			case HAND:
				handleHandZoneWithoutCursor(z);
				break;
			case ACTIVE_CARD:
				handleActiveCardZoneWithoutCursor(z);
				break;
			default:
				handleDefaultZonesWithoutCursor(z);
				break;
		}
	}
	
	public void handleDefaultZonesWithoutCursor(ZoneComp z) {
		for(int i = 0; i < z.objectIDs.size(); i++) {
			Comp.remove(UntargetableComp.class, Comp.Entity.get(z.objectIDs.get(i)));
		}		
	}
	
	public void handleHandZoneWithoutCursor(ZoneComp z) {
		for(int i = 0; i < z.objectIDs.size(); i++) {
			Entity card = Comp.Entity.get(z.objectIDs.get(i));
			TurnAction t = Comp.TurnActionComp.get(card).turnAction;
			if(t.targetIDs.size > 0) {
				Comp.remove(UntargetableComp.class, card);
			} else {
				Comp.add(getEngine(), UntargetableComp.class, card);
			}
		}
	}
	
	public void handleActiveCardZoneWithoutCursor(ZoneComp z) {
		if(zCursor.zoneType != ZoneType.FRIEND) {
			handleDefaultZonesWithoutCursor(z);
			return;
		}
		for(int i = 0; i < z.objectIDs.size(); i++) {
			Comp.add(getEngine(), UntargetableComp.class, Comp.Entity.get(z.objectIDs.get(i)));
		}
	}
	
	public boolean isValidTarget(int index) {
		Entity cursor = Comp.Entity.DefaultCursor().Entity();
		return hasValidTarget(Comp.Entity.Cursor(cursor).getPlayerID(), Comp.Find.ZoneComp.findZone(cursor).zoneType, Comp.CursorComp(Comp.CursorComp.get(cursor)).turnAction(), index, 0, 0);
	}
	
	private boolean hasValidTarget(ID ownerID, ZoneType zoneType, TurnAction t, int index, int direction, int depth) {
		return findNextValidTarget(ownerID, zoneType, t, index, direction, depth) >= 0;
	}
	
	private int findNextValidTarget(ID ownerID, ZoneType zoneType, TurnAction turnAction, int index, int direction, int depth) {
		ZoneComp z = Comp.Find.ZoneComp.findZone(ownerID, zoneType);
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
	
	/*private int findNextValidTargetOld(ID ownerID, ZoneType zoneType, TurnAction t, int index, int direction, int depth) {
		ZoneComp z = Comp.Find.ZoneComp.findZone(ownerID, zoneType);
		for(int i = 0; i < z.objectIDs.size(); i++) {
			index = ArrayUtil.findNextIndex(z.objectIDs, index, direction);
			if(!Comp.ZoneComp(z).hasIndex(index)) {
				return -1;
			}
			Entity entity = Comp.Entity.get(z.objectIDs.get(index));
			if(entity != null && (t == null || t.isValidTarget(entity))) {
				if(t == null) {
					TurnActionComp taComp = Comp.TurnActionComp.get(entity);
					if(taComp != null) {
						TurnAction ta = taComp.turnAction;
						ZoneType nextZoneType = ta.getNextTargetZoneType(depth);
						if(nextZoneType == ZoneType.NONE || hasValidTarget(ownerID, nextZoneType, ta, 0, 1, depth+1)) {
							return index;
						}
					}
				} else {
					return index;
				}
			}
			if(direction == 0) {
				break;
			}
		}			
		return -1;
	}*/

}
