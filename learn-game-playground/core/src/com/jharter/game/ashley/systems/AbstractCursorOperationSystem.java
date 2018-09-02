package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.jharter.game.ashley.components.Components.TurnActionComp;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.Ent;
import com.jharter.game.ashley.components.Comp;
import com.jharter.game.ashley.components.subcomponents.TurnAction;
import com.jharter.game.util.id.ID;

import uk.co.carelesslabs.Enums.ZoneType;

public abstract class AbstractCursorOperationSystem extends IteratingSystem {

	@SuppressWarnings("unchecked")
	public AbstractCursorOperationSystem(Family family) {
		super(family);
	}
	
	protected boolean isValidTarget(ID ownerID, ZoneType zoneType, TurnAction t, int index) {
		return hasValidTarget(ownerID, zoneType, t, index, 0, 0);
	}
	
	protected boolean hasValidTargetInZone(ID ownerID, ZoneType zoneType, TurnAction t, int index, int direction) {
		return hasValidTarget(ownerID, zoneType, t, index, direction, 0);
	}
	
	protected int findFirstValidTargetInZone(ID ownerID, ZoneType zoneType, TurnAction t) {
		return findNextValidTarget(ownerID, zoneType, t, -1, 1, 0);
	}
	
	protected int findNextValidTargetInZone(ID ownerID, ZoneType zoneType, TurnAction t, int index, int direction) {
		return findNextValidTarget(ownerID, zoneType, t, index, direction, 0);
	}
	
	private boolean hasValidTarget(ID ownerID, ZoneType zoneType, TurnAction t, int index, int direction, int depth) {
		return findNextValidTarget(ownerID, zoneType, t, index, direction, depth) >= 0;
	}
	
	private int findNextValidTarget(ID ownerID, ZoneType zoneType, TurnAction t, int index, int direction, int depth) {
		ZoneComp z = Comp.ZoneComp.get(ownerID, zoneType);
		for(int i = 0; i < z.objectIDs.size(); i++) {
			index = findNextIndex(index, direction, z.objectIDs.size());
			if(!z.hasIndex(index)) {
				return -1;
			}
			Entity entity = Ent.Entity.get(z.objectIDs.get(index));
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
	}
	
	private int findNextIndex(int currentIndex, int direction, int size) {
		if(direction == 0) {
			return currentIndex;
		}
		int index = currentIndex + direction;
		if(index < 0) {
			index = size - 1;
		} else if(index >= size) {
			index = 0;
		}
		return index;
	}
	
}
