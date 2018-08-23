package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.jharter.game.ashley.components.Components.CursorComp;
import com.jharter.game.ashley.components.Components.CursorInputComp;
import com.jharter.game.ashley.components.Components.TurnActionComp;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.Components.ZonePositionComp;
import com.jharter.game.ashley.components.Mapper;

import uk.co.carelesslabs.Enums.ZoneType;

public class CursorMoveSystem extends AbstractCursorOperationSystem {

	@SuppressWarnings("unchecked")
	public CursorMoveSystem() {
		super();
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		CursorInputComp ci = Mapper.CursorInputComp.get(entity);
		CursorComp c = Mapper.CursorComp.get(entity);
		ZonePositionComp zp = Mapper.ZonePositionComp.get(entity);
		ZoneComp z = zp.getZoneComp();
		TurnActionComp t = c.getTurnActionComp();
		boolean move = ci.move();
		
		// Force our cursor to move if it's not on a valid target
		if(!move && z.hasIndex(zp) && !hasValidTarget(z.zoneType(), t, zp.index(), 0)) {
			ci.direction.x = 1;
		} else if(!move) {
			return;
		}
		
		int direction = (int) (ci.direction.x != 0 ? ci.direction.x : ci.direction.y);
		int index = findNextValidTarget(z.zoneType(), t, zp.index(), direction);
		if(z.hasIndex(index)) {
			zp.index(index);
		} else {
			zp.index(-1);
		}
	}

}
