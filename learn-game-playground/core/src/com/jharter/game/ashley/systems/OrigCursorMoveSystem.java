package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.jharter.game.ashley.components.Comp;
import com.jharter.game.ashley.components.Components.AnimatingComp;
import com.jharter.game.ashley.components.Components.ChangeZoneComp;
import com.jharter.game.ashley.components.Components.CursorComp;
import com.jharter.game.ashley.components.Components.CursorInputComp;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.Components.ZonePositionComp;
import com.jharter.game.ashley.components.subcomponents.TurnAction;
import com.jharter.game.util.ArrayUtil;
import com.jharter.game.util.id.ID;

import uk.co.carelesslabs.Enums.ZoneType;
import uk.co.carelesslabs.Media;

public class OrigCursorMoveSystem extends IteratingSystem {

	@SuppressWarnings("unchecked")
	public OrigCursorMoveSystem() {
		super(Family.all(CursorComp.class,
				 CursorInputComp.class,
				 ZonePositionComp.class).exclude(AnimatingComp.class).get());
	}
	
	private boolean hasMovement(CursorInputComp ci) {
		return ci.direction.x != 0 || ci.direction.y != 0;
	}

	@Override
	public void processEntity(Entity cursor, float deltaTime) {
		CursorComp c = Comp.CursorComp.get(cursor);
		CursorInputComp ci = Comp.CursorInputComp.get(cursor);
		ZonePositionComp zp = Comp.ZonePositionComp.get(cursor);
		
		ZoneComp z = Comp.ZonePositionComp(zp).getZoneComp();
		ZoneComp origZ = z;
		
		TurnAction t = Comp.CursorComp(c).turnAction();
		ID playerID = Comp.Entity.Cursor(cursor).getPlayerID();
		
		ZoneType zoneType = z.zoneType;
		int index = zp.index;
		
		boolean move = hasMovement(ci) && (t == null || !t.all);
		boolean valid = isTargetable(z, index);
		
		if(!move && valid) {
			return;
		}
		
		if(!valid) {
			zoneType = ZoneType.HAND;
			z = Comp.Find.ZoneComp.findZone(playerID, zoneType);
			index = -1;
			
			// We need to be sure to cleanup our selection if we end up in an invalid state
			if(t != null) {
				t.targetIDs.clear();
				c.turnActionID = null;
				t = null;
			}
		}
		
		int direction;
		if(!move) {
			move = true;
			direction = 1;
		} else {
			direction = (int) (ci.direction.x != 0 ? ci.direction.x : ci.direction.y);
			Media.moveBeep.play();
		}
		
		if(index == -1) {
			index = 0;
		}
		int newIndex = findNextTargetable(z, direction, index);
		if(zp.index != newIndex || origZ.zoneID != z.zoneID) {
			ChangeZoneComp cz = Comp.create(getEngine(), ChangeZoneComp.class);
			cz.oldZoneID = origZ.zoneID;
			cz.newZoneID = z.zoneID;
			cz.newIndex = newIndex;
			cursor.add(cz);
		}
		
	}
	
	private boolean isTargetable(ZoneComp zone, int index) {
		return index >= 0 && index < zone.objectIDs.size() && !Comp.UntargetableComp.has(zone.objectIDs.get(index));
	}
	
	private int findNextTargetable(ZoneComp zone, int direction, int index) {
		return ArrayUtil.findNextIndex(zone.objectIDs, index, direction, (id) -> !Comp.UntargetableComp.has(id));
	}
}
