package com.jharter.game.layout;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.jharter.game.ecs.components.Components.CursorComp;
import com.jharter.game.ecs.components.Components.MultiSpriteComp;
import com.jharter.game.ecs.components.Components.SpriteComp;
import com.jharter.game.ecs.components.Components.ZoneComp;
import com.jharter.game.ecs.components.Components.ZonePositionComp;
import com.jharter.game.ecs.components.subcomponents.RelativePositionRules;
import com.jharter.game.ecs.components.subcomponents.TurnAction;
import com.jharter.game.ecs.entities.IEntityHandler;
import com.jharter.game.tween.TweenType;
import com.jharter.game.util.U;
import com.jharter.game.util.id.ID;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.equations.Circ;
import uk.co.carelesslabs.Enums.Direction;
import uk.co.carelesslabs.Enums.ZoneType;

public class CursorLayout extends ZoneLayout {

	private boolean hasMulti = false;
	private RelativePositionRules rpr;
	
	public CursorLayout(IEntityHandler handler) {
		super(handler);
		rpr = new RelativePositionRules();
		rpr.enabled = true;
		rpr.tween = true;
	}
	
	@Override
	protected TweenTarget getTarget(ID id, int index, Entity cursor, TweenTarget tt) {
		CursorComp c = Comp.CursorComp.get(cursor);
		SpriteComp s = Comp.SpriteComp.get(cursor);
		
		//Sys.out.println("Target ID: " + c.targetID);
		if(c.targetID == null) {
			return null;
		}
		
		Entity target = Comp.Entity.get(c.targetID);
		
		ZonePositionComp zp = Comp.ZonePositionComp.get(target);
		ZoneComp z = Comp.ZoneComp.get(zp.zoneID);
		float targetAngle = getCursorAngle(cursor, z.zoneType);
		
		Vector3 targetPosition = getCursorPosition(cursor, c.targetID, target, z);
		if(targetPosition == null) {
			return null;
		}
		
		tt.setFromSpriteComp(s);
		tt.position.x = targetPosition.x;
		tt.position.y = targetPosition.y;
		tt.angleDegrees = targetAngle;
		
		if(tt.matchesTarget(s)) {
			return null;
		}
		
		// If we haven't changed zones since our last target, increase our cursor
		// speed for better responsiveness.
		if(!changedZone(c, z)) {
			tt.duration = 0.10f;
		}
		
		return tt;
	}
	
	protected boolean changedZone(CursorComp c, ZoneComp z) {
		Entity lastTarget = Comp.Entity.get(c.lastTargetID);
		ID zoneID = z == null ? null : z.zoneID;
		ID lastZoneID = null;
		if(lastTarget != null) {
			ZonePositionComp zpLastTarget = Comp.ZonePositionComp.get(lastTarget);
			ZoneComp zLastTarget = Comp.ZoneComp.get(zpLastTarget.zoneID); 
			if(zLastTarget != null) {
				lastZoneID = zLastTarget.zoneID;
			}
		}
		return zoneID != lastZoneID;
	}
	
	private float getCursorAngle(Entity entity, ZoneType zoneType) {
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
	
	private Vector3 getCursorPosition(Entity cursor, ID targetID, Entity target, ZoneComp z) {
		if(target == null) {
			return null;
		}
		
		// Could use this if you always wanted the cursor to point to the target
		// layout location as opposed to the target actual location. These two
		// would be different if the target entity was currently moving back in
		// to place or something like that.
		//TweenTarget lTarget = z.layout.getTarget(cursorTargetID);
		
		SpriteComp sTarget = Comp.SpriteComp.get(target);
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
	
	///////////////////////// MAYBE MOVE TO A NEW CLASS WHAT'S BELOW THIS LINE ////////////////////
	
	@Override
	protected void modifyEntity(ID id, int index, Entity cursor, TweenTarget tt) {
		CursorComp c = Comp.CursorComp.get(cursor);
		SpriteComp s = Comp.SpriteComp.get(cursor);
		Entity target = Comp.Entity.get(c.targetID);
		ZonePositionComp zp = Comp.ZonePositionComp.get(target);
		ZoneComp z = Comp.ZoneComp.get(zp.zoneID);
		boolean changeZone = changedZone(c, z);
		hasMulti = changeZone && isAll(c);
		
		if(changeZone) {
			handleChangeZone(cursor, c, zp, z, s, tt);
		} else {
			handleStayInZone(cursor, c, zp, z, s, tt);
		}
		handleTargetingTurnAction(cursor, c, zp, z, s, s.position);
	
		if(!hasMulti) {
			Comp.remove(MultiSpriteComp.class, cursor);
		}
	}

	private void handleChangeZone(Entity cursor, CursorComp c, ZonePositionComp zp, ZoneComp z, SpriteComp s, TweenTarget tt) {
		if(!isAll(c)) {
			return;
		}
			
		float convergeX = s.position.x + (tt.position.x - s.position.x) * 0.75f;
		float duration = 0.25f;
		
		Timeline multiA = Timeline.createParallel();
		Timeline multiB = Timeline.createParallel();
		
		float centerY = 0;
		MultiSpriteComp mp = Comp.getOrAdd(MultiSpriteComp.class, cursor);
		mp.clear();
		
		int size = z.objectIDs.size;
		for(int i = 0; i < size; i++) {
			Vector3 position = getCursorPosition(cursor, z.objectIDs.get(i), Comp.Entity.get(z.objectIDs.get(i)), z);
			if(position != null) {
				centerY += position.y;
			}
		}
		centerY /= (float) size;
		
		for(int i = 0; i < size; i++) {
			Vector3 position = getCursorPosition(cursor, z.objectIDs.get(i), Comp.Entity.get(z.objectIDs.get(i)), z);
			if(position != null) {
				Vector3 currP = new Vector3(s.position);
				mp.positions.add(currP);
				Vector3 targP = new Vector3(position);
						
				multiA.push(Tween.to(currP, TweenType.POSITION_XY.asInt(), duration).ease(Circ.INOUT).target(convergeX, centerY));
				multiB.push(Tween.to(currP, TweenType.POSITION_XY.asInt(), duration).ease(Circ.INOUT).target(targP.x, targP.y));
			}
		}
		mp.size = mp.positions.size;
		
		Timeline tween = Timeline.createSequence().push(multiA).push(multiB);
		getTweenManager().start(Comp.IDComp.get(cursor).id, tween);
	}
	
	private void handleStayInZone(Entity cursor, CursorComp c, ZonePositionComp zp, ZoneComp z, SpriteComp s, TweenTarget tt) {
		if(!isAll(c)) {
			return;
		}
		
		MultiSpriteComp mp = Comp.getOrAdd(MultiSpriteComp.class, cursor);
		Array<ID> objectIDs = Comp.ZoneComp.get(zp.zoneID).objectIDs;
		for(int i = 0; i < objectIDs.size; i++) {
			Vector3 position = getCursorPosition(cursor, objectIDs.get(i), Comp.Entity.get(objectIDs.get(i)), z);
			if(position != null) {
				mp.positions.add(new Vector3(position));
			}
		}
		mp.size = mp.positions.size;
	}
	
	private void handleTargetingTurnAction(Entity cursor, CursorComp c, ZonePositionComp zp, ZoneComp z, SpriteComp s, Vector3 position) {
		
		// Make sure cursor is in a valid place
		if(!Comp.util(z).hasIndex(zp.index) || z.zoneType != ZoneType.FRIEND_ACTIVE_CARD) {
			return;
		}
		
		// See if cursor is modifying an action
		if(c.turnActionID == null) {
			return;
		}
		
		TurnAction t = Comp.TurnActionComp.get(Comp.Entity.get(c.turnActionID)).turnAction;
		if(t == null) {
			return;
		}
		
		// See if the cursor has already selected a card from hand
		// that will force the card it targets to target all
		boolean forceAll = t.makesTargetAll;
		int forceMultiplicity = t.makesTargetMultiplicity;
		
		// Get the card that the cursor is above and verify it has a turn action associated with it
		Entity activeCard = Comp.Entity.get(z.objectIDs.get(zp.index));
		if(!Comp.TurnActionComp.has(activeCard)) {
			return;
		}
		
		TurnAction turnAction = Comp.TurnActionComp.get(activeCard).turnAction;
		if(turnAction.targetIDs.size < 1) {
			return;
		}
		
		int multiplicity = forceMultiplicity * turnAction.multiplicity;
		
		// Get the last target in the list and find its zone
		Entity targetEntity = Comp.Entity.get(turnAction.targetIDs.peek());
		ZonePositionComp targetZonePosition = Comp.ZonePositionComp.get(targetEntity);
		ZoneComp targetZone = Comp.ZoneComp.get(targetZonePosition.zoneID);
		
		MultiSpriteComp ms = Comp.getOrAdd(MultiSpriteComp.class, cursor);
		ms.drawSingle = true;
		ms.reflectAngle = false;
		if(!hasMulti) {
			ms.clear();
		}
		
		// If the last target has an "all connection", find all targets within that zone and
		// add multiplicity to them.
		if((turnAction.all || forceAll)) {
			for(int i = 0; i < targetZone.objectIDs.size; i++) {
				addMultiPositions(ms, multiplicity, cursor, s, targetZone, Comp.IDComp.get(Comp.Entity.get(targetZone.objectIDs.get(i))).id);
			}
			
		// Otherwise, just add multiplicity
		} else {
			addMultiPositions(ms, multiplicity, cursor, s, targetZone, Comp.IDComp.get(targetEntity).id);
		}
		ms.size = ms.positions.size;
		hasMulti = true;
	}
	
	private void addMultiPositions(MultiSpriteComp ms, int multiplicity, Entity cursor, SpriteComp s, ZoneComp zone, ID targetID) {
		Vector3 cursorPosition = getCursorPosition(cursor, targetID, Comp.Entity.get(targetID), zone);
		for(int m = 0; m < multiplicity; m++) {
			Vector3 pos = new Vector3(cursorPosition);
			pos.x -= U.u12(2)*m;
			pos.y -= U.u12(1)*m;
			if(zone.zoneType == ZoneType.FRIEND) {
				pos.x += U.u12(4);
			}
			ms.positions.add(pos);
			ms.scales.add(new Vector2(0.5f*s.scale.x, 0.5f*s.scale.y));
			ms.alphas.add(0f);
			if(zone.zoneType == ZoneType.ENEMY) {
				ms.reflectAngle = true;
			}
		}
		
		getTweenManager().start(Comp.IDComp.get(cursor).id, Tween.to(ms.alphas, TweenType.ALPHA.asInt(), 0.5f).target(1f));
	}

	private boolean isAll(CursorComp c) {
		if(c.turnActionID == null) {
			return false;
		}
		TurnAction ta = Comp.TurnActionComp.get(Comp.Entity.get(c.turnActionID)).turnAction;
		return ta != null && ta.all;
	}
	
}
