package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.jharter.game.ashley.components.Components.CursorComp;
import com.jharter.game.ashley.components.Components.CursorInputComp;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.Components.ZonePositionComp;
import com.jharter.game.ashley.components.Mapper;
import com.jharter.game.ashley.components.subcomponents.TurnAction;

import uk.co.carelesslabs.Enums.ZoneType;

public class CursorNewMoveSystem extends AbstractCursorOperationSystem {

	@SuppressWarnings("unchecked")
	public CursorNewMoveSystem() {
		super(Family.all(CursorComp.class,
				 CursorInputComp.class,
				 ZonePositionComp.class).get());
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		CursorInputComp ci = Mapper.CursorInputComp.get(entity);
		CursorComp c = Mapper.CursorComp.get(entity);
		ZonePositionComp zp = Mapper.ZonePositionComp.get(entity);
		ZoneComp z = zp.getZoneComp();
		TurnAction t = c.getTurnAction();
		
		ZoneType zoneType = z.zoneType();
		int index = zp.index();
		
		boolean move = ci.move();
		boolean valid = isValidTarget(zoneType, t, index);
		
		if(!move && valid) {
			return;
		}
		
		if(!valid) {
			zoneType = ZoneType.HAND;
			index = -1;
		}
		
		int direction;
		if(!move) {
			move = true;
			direction = 1;
		} else {
			direction = (int) (ci.direction.x != 0 ? ci.direction.x : ci.direction.y);
		}
		
		int newIndex = findNextValidTargetInZone(zoneType, t, index, direction);
		if(z.hasIndex(newIndex)) {
			zp.index(newIndex);
		} else {
			zp.index(-1);
		}
	}

}
