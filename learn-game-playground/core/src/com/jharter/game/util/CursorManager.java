package com.jharter.game.util;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.jharter.game.ecs.components.Components.CursorComp;
import com.jharter.game.ecs.components.Components.SpriteComp;
import com.jharter.game.ecs.components.Components.ZoneComp;
import com.jharter.game.ecs.components.Components.ZonePositionComp;
import com.jharter.game.ecs.components.subcomponents.RelativePositionRules;
import com.jharter.game.ecs.components.subcomponents.TurnAction;
import com.jharter.game.ecs.entities.EntityHandler;
import com.jharter.game.ecs.entities.IEntityHandler;
import com.jharter.game.util.id.ID;

import uk.co.carelesslabs.Enums.Direction;
import uk.co.carelesslabs.Enums.ZoneType;

public class CursorManager extends EntityHandler {

	private RelativePositionRules rpr;
	
	public CursorManager(IEntityHandler handler) {
		super(handler);
		rpr = new RelativePositionRules();
		rpr.enabled = true;
		rpr.tween = true;
	}
	
	public boolean changedZones(Entity cursor) {
		return Comp.CursorChangedZoneEvent.has(cursor);
	}
	
	public TurnAction getTurnAction(CursorComp c) {
		Entity taEntity = Comp.Entity.get(c.turnActionID);
		if(taEntity == null) {
			return null;
		}
		return Comp.TurnActionComp.get(taEntity).turnAction;
	}		
	
	public boolean isDisabled(Entity cursor) {
		return Comp.DisabledComp.has(cursor);
	}
	
	public float getZoneCenterY(Entity cursor, ZoneComp z) {
		float centerY = 0;
		int size = z.objectIDs.size;
		for(int i = 0; i < size; i++) {
			Vector3 p = getCursorPosition(cursor, z.objectIDs.get(i), z);
			if(p != null) {
				centerY += p.y;
			}
		}
		if(size > 0) {
			centerY /= (float) size;
		}
		return centerY;
	}
	
	public float getCursorAngle(ZoneType zoneType) {
		switch(zoneType) {
			case FRIEND:
			case FRIEND_ACTIVE_CARD:
				return 90f;
			case ENEMY:
				return 270f;
			case HAND:
			default:
				return 0f;
		}
	}

	public Vector3 getCursorPosition(ID cursorID) {
		return getCursorPosition(Comp.Entity.get(cursorID));
	}
	
	public Vector3 getCursorPosition(Entity cursor) {
		return getCursorPosition(cursor, Comp.CursorComp.get(cursor).targetID);
	}
	
	public Vector3 getCursorPosition(ID cursorID, ID targetID) {
		return getCursorPosition(Comp.Entity.get(cursorID), targetID);
	}
	
	public Vector3 getCursorPosition(Entity cursor, ID targetID) {
		return getCursorPosition(cursor, targetID, Comp.ZoneComp.get(Comp.ZonePositionComp.get(targetID).zoneID));
	}
	
	public Vector3 getCursorPosition(ID cursorID, ID targetID, ZoneComp z) {
		return getCursorPosition(Comp.Entity.get(cursorID), targetID, z);
	}
	
	public Vector3 getCursorPosition(Entity cursor, ID targetID, ZoneComp z) {
		if(targetID == null) {
			return null;
		}
		
		// Could use this if you always wanted the cursor to point to the target
		// layout location as opposed to the target actual location. These two
		// would be different if the target entity was currently moving back in
		// to place or something like that.
		//TweenTarget lTarget = z.layout.getTarget(cursorTargetID);
		
		SpriteComp sTarget = Comp.SpriteComp.get(targetID);
		if(sTarget == null) {
			return null;
		}
		
		SpriteComp s = Comp.SpriteComp.get(cursor);
		Vector3 cursorPosition = new Vector3();
		
		rpr.setRelativeToID(targetID);
		rpr.offset.set(0,0,0);
		switch(z.zoneType) {
			case HAND:
				rpr.xAlign = Direction.CENTER;
				rpr.yAlign = Direction.NORTH;
				rpr.offset.y = -U.u12(2);
				break;
			case FRIEND:
				rpr.xAlign = Direction.WEST;
				rpr.yAlign = Direction.CENTER;
				rpr.offset.x = -U.u12(2);
				break;
			case FRIEND_ACTIVE_CARD:
				rpr.xAlign = Direction.WEST;
				rpr.yAlign = Direction.CENTER;
				rpr.offset.x = -U.u12(2);
				break;
			case ENEMY:
				rpr.xAlign = Direction.EAST;
				rpr.yAlign = Direction.CENTER;
				rpr.offset.x = U.u12(2);
				break;
			default:
				break;
		}
		
		rpr.setToRelativePosition(this, s, cursorPosition);
		return cursorPosition;
	}
	
	public boolean isAll(ID cursorID) {
		return isAll(Comp.CursorComp.get(cursorID));
	}
	
	public boolean isAll(Entity cursor) {
		return isAll(Comp.CursorComp.get(cursor));
	}
	
	public boolean isAll(CursorComp c) {
		TurnAction t = getTurnAction(c);
		return t != null && t.all;
	}
	
	public Array<ID> getCursorAllIDs(CursorComp c) {
		Array<ID> allIDs = new Array<ID>();
		ZoneComp z = Comp.ZoneComp.get(Comp.ZonePositionComp.get(c.targetID).zoneID);
		if(z != null) {
			for(ID id : z.objectIDs) {
				if(Comp.TargetableComp.has(id)) {
					allIDs.add(id);
				}
			}
		}
		return allIDs;
	}
	
	public boolean isTargetingCard(CursorComp c) {
		ZoneType zoneType = Comp.ZoneComp.get(Comp.ZonePositionComp.get(c.targetID).zoneID).zoneType;
		return zoneType == ZoneType.FRIEND_ACTIVE_CARD || zoneType == ZoneType.ENEMY_ACTIVE_CARD;
	}
	
	public Array<ID> getCursorSecondaryIDs(CursorComp c) {
		Array<ID> ids = new Array<ID>();
		TurnAction cursorTurnAction = Comp.TurnActionComp.get(c.turnActionID).turnAction;
		TurnAction turnAction = Comp.TurnActionComp.get(c.targetID).turnAction;
		if(cursorTurnAction == null || turnAction == null) {
			return ids;
		}
		
		boolean all = turnAction.all || cursorTurnAction.makesTargetAll;
		ID subID = turnAction.targetIDs.peek();
		Entity subEntity = Comp.Entity.get(subID);
		ZonePositionComp subZonePosition = Comp.ZonePositionComp.get(subEntity);
		ZoneComp subZone = Comp.ZoneComp.get(subZonePosition.zoneID);
		
		if(all) {
			for(int i = 0; i < subZone.objectIDs.size; i++) {
				ID id = subZone.objectIDs.get(i);
				if(turnAction.isValidTarget(Comp.Entity.get(id))) {
					ids.add(id);
				}
			}
		} else {
			ids.add(subID);
		}
		
		return ids;
	}
	
}
