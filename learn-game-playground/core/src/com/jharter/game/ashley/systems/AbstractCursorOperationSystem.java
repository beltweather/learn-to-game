package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.jharter.game.ashley.components.Components.CursorComp;
import com.jharter.game.ashley.components.Components.CursorInputComp;
import com.jharter.game.ashley.components.Components.TurnActionComp;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.Components.ZonePositionComp;
import com.jharter.game.ashley.components.Mapper;

import uk.co.carelesslabs.Enums.ZoneType;

public abstract class AbstractCursorOperationSystem extends IteratingSystem {

	@SuppressWarnings("unchecked")
	public AbstractCursorOperationSystem() {
		super(Family.all(CursorComp.class,
						 CursorInputComp.class,
						 ZonePositionComp.class).get());
	}
	
	protected boolean hasValidTarget(ZoneType zoneType, TurnActionComp t, int index, int direction) {
		return hasValidTarget(zoneType, t, index, direction, 0);
	}
	
	protected int findNextValidTarget(ZoneType zoneType, TurnActionComp t, int index, int direction) {
		return findNextValidTarget(zoneType, t, index, direction, 0);
	}
	
	private boolean hasValidTarget(ZoneType zoneType, TurnActionComp t, int index, int direction, int depth) {
		return findNextValidTarget(zoneType, t, index, direction, depth) >= 0;
	}
	
	private int findNextValidTarget(ZoneType zoneType, TurnActionComp t, int index, int direction, int depth) {
		ZoneComp z = Mapper.ZoneComp.get(zoneType);
		for(int i = 0; i < z.size(); i++) {
			index = findNextIndex(index, direction, z.size());
			Entity entity = Mapper.Entity.get(z.get(index));
			if(entity != null && (t == null || t.isValidTarget(entity))) {
				if(t == null) {
					TurnActionComp ta = Mapper.TurnActionComp.get(entity);
					ZoneType nextZoneType = ta.getNextTargetZoneType(depth);
					if(nextZoneType == ZoneType.NONE || hasValidTarget(nextZoneType, ta, 0, 1, depth+1)) {
						return index;
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
		int index = currentIndex + direction;
		if(index < 0) {
			index = size - 1;
		} else if(index >= size) {
			index = 0;
		}
		return index;
	}
	
}
