package com.jharter.game.layout;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.jharter.game.ashley.components.Comp;
import com.jharter.game.ashley.components.Components.AnimatingComp;
import com.jharter.game.ashley.components.Components.CursorComp;
import com.jharter.game.ashley.components.Components.IDComp;
import com.jharter.game.ashley.components.Components.MultiSpriteComp;
import com.jharter.game.ashley.components.Components.SpriteComp;
import com.jharter.game.ashley.components.Components.TextureComp;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.Components.ZonePositionComp;
import com.jharter.game.ashley.components.subcomponents.RelativePositionRules;
import com.jharter.game.ashley.components.subcomponents.TurnAction;
import com.jharter.game.tween.TweenType;
import com.jharter.game.tween.TweenUtil;
import com.jharter.game.util.Sys;
import com.jharter.game.util.U;
import com.jharter.game.util.id.ID;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.equations.Circ;
import uk.co.carelesslabs.Enums.Direction;
import uk.co.carelesslabs.Enums.ZoneType;

public class CursorPositionSystem extends IteratingSystem {

	private boolean hasMulti = false;
	private RelativePositionRules rpr;
	
	@SuppressWarnings("unchecked")
	public CursorPositionSystem() {
		super(Family.all(CursorComp.class, ZonePositionComp.class, SpriteComp.class, TextureComp.class).exclude(AnimatingComp.class).get());
		rpr = new RelativePositionRules();
		rpr.enabled = true;
		rpr.tween = true;
	}

	@Override
	protected void processEntity(final Entity entity, float deltaTime) {
		CursorComp c = Comp.CursorComp.get(entity);
		SpriteComp s = Comp.SpriteComp.get(entity);
		ZonePositionComp zp = Comp.ZonePositionComp.get(entity);
		ZoneComp z = zp.getZoneComp();
		float targetAngle = getCursorAngle(entity, z.zoneType);
		
		hasMulti = false;
		if(c.lastZoneID != z.zoneID) {
			handleChangeZone(entity, c, zp, z, s, targetAngle);
		} else {
			handleStayInZone(entity, c, zp, z, s, targetAngle);
		}
		handleTargetingTurnAction(entity, c, zp, z, s, s.position);
	
		if(!hasMulti) {
			Comp.remove(MultiSpriteComp.class, entity);
		}
	}
	
	private void handleChangeZone(Entity entity, CursorComp c, ZonePositionComp zp, ZoneComp z, SpriteComp s, float targetAngle) {
		IDComp id = Comp.IDComp.get(entity);
		Vector3 position = getCursorPosition(entity, z, zp.index);
		
		if(position == null) {
			return;
		}
		
		TweenTarget tt = TweenTarget.newInstance();
		tt.setFromEntity(entity);
		tt.position.x = position.x;
		tt.position.y = position.y;
		tt.angleDegrees = targetAngle;
		
		if(tt.matchesTarget(s)) {
			if(isAll(c)) {
				hasMulti = true;
			}
			tt.free();
			return;
		}
		
		Timeline tween;
		if(isAll(c)) {
			
			float convergeX = s.position.x + (position.x - s.position.x) * 0.75f;
			float duration = 0.25f;
			
			Timeline single = TweenUtil.tween(id.id, tt, duration);
			Timeline multiA = Timeline.createParallel();
			Timeline multiB = Timeline.createParallel();
			
			float centerY = 0;
			MultiSpriteComp mp = Comp.getOrAdd(getEngine(), MultiSpriteComp.class, entity);
			mp.clear();
			
			int size = Comp.Method.ZoneComp.get(zp).objectIDs.size();
			for(int i = 0; i < size; i++) {
				position = getCursorPosition(entity, z, i);
				if(position != null) {
					centerY += position.y;
				}
			}
			centerY /= (float) size;
			
			for(int i = 0; i < size; i++) {
				position = getCursorPosition(entity, z, i);
				if(position != null) {
					Vector3 currP = new Vector3(s.position);
					mp.positions.add(currP);
					Vector3 targP = new Vector3(position);
							
					multiA.push(Tween.to(currP, TweenType.POSITION_XY.asInt(), duration).ease(Circ.INOUT).target(convergeX, centerY));
					multiB.push(Tween.to(currP, TweenType.POSITION_XY.asInt(), duration).ease(Circ.INOUT).target(targP.x, targP.y));
				}
			}
			mp.size = mp.positions.size;
			hasMulti = true;
			
			tween = Timeline.createParallel().push(single).push(Timeline.createSequence().push(multiA).push(multiB));
			
		} else {
			tween = TweenUtil.tween(id.id, tt);
		}
		
		TweenUtil.start(getEngine(), id.id, tween);
		
		tt.free();
	}
	
	private void handleStayInZone(Entity entity, CursorComp c, ZonePositionComp zp, ZoneComp z, SpriteComp s, float targetAngle) {
		Vector3 targetPosition = getCursorPosition(entity, z, zp.index);
		
		if(targetPosition == null) {
			return;
		}
		
		//s.position.x = targetPosition.x;
		//s.position.y = targetPosition.y;
		//s.angleDegrees = targetAngle;
		
		TweenTarget tt = TweenTarget.newInstance(s);
		tt.position.x = targetPosition.x;
		tt.position.y = targetPosition.y;
		tt.angleDegrees = targetAngle;
		tt.duration = 0.03f;
		
		if(tt.matchesTarget(s)) {
			return;
		}
		
		Sys.out.println("Tweening");
		TweenUtil.start(getEngine(), entity, tt);
		
		if(isAll(c)) {
			MultiSpriteComp mp = Comp.getOrAdd(getEngine(), MultiSpriteComp.class, entity);
			for(int i = 0; i < Comp.Method.ZoneComp.get(zp).objectIDs.size(); i++) {
				targetPosition = getCursorPosition(entity, z, i);
				if(targetPosition != null) {
					Vector3 targP = new Vector3(targetPosition);
					mp.positions.add(targP);
				}
			}
			mp.size = mp.positions.size;
			hasMulti = true;
		}
		
		tt.free();
	}
	
	private void handleTargetingTurnAction(Entity cursor, CursorComp c, ZonePositionComp zp, ZoneComp z, SpriteComp s, Vector3 position) {
		
		// Make sure cursor is in a valid place
		if(!z.hasIndex(zp.index) || z.zoneType != ZoneType.ACTIVE_CARD) {
			return;
		}
		
		// See if cursor is modifying an action
		TurnAction t = Comp.Method.CursorComp.getTurnAction(c);
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
		if(turnAction.targetIDs.size <= 1) {
			return;
		}
		
		int multiplicity = forceMultiplicity * turnAction.multiplicity;
		
		// Iterate through all targets of this card, looking in particular for the last two pairs
		// of targets so we can handle their "all" status or lack thereof
		Entity targetEntity = Comp.Entity.get(turnAction.targetIDs.get(turnAction.targetIDs.size-1));
		ZonePositionComp targetZone = Comp.ZonePositionComp.get(targetEntity);
		ZoneComp zone = targetZone.getZoneComp();
		
		MultiSpriteComp ms = Comp.getOrAdd(getEngine(), MultiSpriteComp.class, cursor);
		ms.drawSingle = true;
		if(!hasMulti) {
			ms.clear();
		}
		
		// If the last pairs have an "all connection", find all targets within that zone and
		// render lines to them.
		if((turnAction.all || forceAll)) {
			for(int i = 0; i < zone.objectIDs.size(); i++) {
				addMultiPositions(ms, multiplicity, cursor, s, zone, Comp.IDComp.get(Comp.Entity.get(zone.objectIDs.get(i))).id);
			}

		// Otherwise, connect the pairs as usual
		} else {
			addMultiPositions(ms, multiplicity, cursor, s, zone, Comp.IDComp.get(targetEntity).id);
		}
		ms.size = ms.positions.size;
		hasMulti = true;
	}
	
	private void addMultiPositions(MultiSpriteComp ms, int multiplicity, Entity cursor, SpriteComp s, ZoneComp zone, ID targetID) {
		for(int m = 0; m < multiplicity; m++) {
			Vector3 pos = getCursorPosition(cursor, zone, targetID);
			pos.x -= U.u1(25)*m;
			pos.y -= U.u1(10)*m;
			ms.positions.add(pos);
			ms.scales.add(new Vector2(0.5f*s.scale.x, 0.5f*s.scale.y));
			if(zone.zoneType == ZoneType.ENEMY) {
				ms.reflectAngle = true;
			}
		}
	}

	private boolean isAll(CursorComp c) {
		TurnAction ta = Comp.Method.CursorComp.getTurnAction(c);
		return ta != null && ta.all;
	}
	
	private float getCursorAngle(Entity entity, ZoneType zoneType) {
		switch(zoneType) {
			case FRIEND:
			case ACTIVE_CARD:
				return 90f;
			case ENEMY:
				return 270f;
			case HAND:
			default:
				return 0f;
		}
	}
	
	private Vector3 getCursorPosition(Entity entity, ZoneComp z, int index) {
		if(!z.hasIndex(index)) {
			return null;
		}
		
		ID cursorTargetID = z.objectIDs.get(index);
		return getCursorPosition(entity, z, cursorTargetID);
	}
	
	private Vector3 getCursorPosition(Entity entity, ZoneComp z, ID cursorTargetID) {
		Entity target = Comp.Entity.get(cursorTargetID);
		if(target == null) {
			return null;
		}
		
		TweenTarget lTarget = z.layout.getTarget(cursorTargetID);
		SpriteComp sTarget = Comp.SpriteComp.get(target);
		
		if(lTarget == null || sTarget == null) {
			return null;
		}
		
		SpriteComp s = Comp.SpriteComp.get(entity);
		Vector3 cursorPosition = new Vector3();
		
		rpr.setRelativeToID(cursorTargetID);
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
			case ACTIVE_CARD:
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
		
		rpr.setToRelativePosition(s, cursorPosition);
		return cursorPosition;
	}
}
