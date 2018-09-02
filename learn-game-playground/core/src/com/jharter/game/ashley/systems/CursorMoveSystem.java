package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.jharter.game.ashley.components.Components.ChangeZoneComp;
import com.jharter.game.ashley.components.Components.CursorComp;
import com.jharter.game.ashley.components.Components.CursorInputComp;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.Components.ZonePositionComp;
import com.jharter.game.ashley.components.Link;
import com.jharter.game.ashley.components.Comp;
import com.jharter.game.ashley.components.subcomponents.TurnAction;
import com.jharter.game.util.id.ID;

import uk.co.carelesslabs.Enums.ZoneType;

public class CursorMoveSystem extends AbstractCursorOperationSystem {

	@SuppressWarnings("unchecked")
	public CursorMoveSystem() {
		super(Family.all(CursorComp.class,
				 CursorInputComp.class,
				 ZonePositionComp.class).get());
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		CursorInputComp ci = Comp.CursorInputComp.get(entity);
		CursorComp c = Comp.CursorComp.get(entity);
		ZonePositionComp zp = Comp.ZonePositionComp.get(entity);
		ZoneComp z = zp.getZoneComp();
		ZoneComp origZ = z;
		TurnAction t = Link.CursorComp.getTurnAction(c);
		ID playerID = Link.CursorComp.getPlayerID(c);
		
		ZoneType zoneType = z.zoneType;
		int index = zp.index;
		
		boolean move = ci.move() && (t == null || !t.all);
		boolean valid = isValidTarget(playerID, zoneType, t, index);
		
		if(!move && valid) {
			return;
		}
		
		if(!valid) {
			zoneType = ZoneType.HAND;
			z = Comp.ZoneComp.get(playerID, zoneType);
			index = -1;
			
			// We need to be sure to cleanup our selection if we end up in an invalid state
			if(t != null) {
				t.targetIDs.clear();
				c.turnActionEntityID = null;
				t = null;
			}
		}
		
		int direction;
		if(!move) {
			move = true;
			direction = 1;
		} else {
			direction = (int) (ci.direction.x != 0 ? ci.direction.x : ci.direction.y);
		}
		
		int newIndex = findNextValidTargetInZone(playerID, zoneType, t, index, direction);
		if(!z.hasIndex(newIndex)) {
			newIndex = -1;
		}
		
		if(zp.index != newIndex || origZ.zoneID != z.zoneID) {
			ChangeZoneComp cz = Comp.create(ChangeZoneComp.class);
			cz.oldZoneID = origZ.zoneID;
			cz.newZoneID = z.zoneID;
			cz.newIndex = newIndex;
			entity.add(cz);
		}
		
		/*if(z.hasIndex(newIndex)) {
			if(zp.zoneType != zoneType) {
				zp.zoneType(zoneType);
			}
			zp.index(newIndex);
		} else {
			zp.index(-1);
		}*/
	}

}
