package com.jharter.game.layout;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pools;
import com.jharter.game.ashley.components.Comp;
import com.jharter.game.ashley.components.Components.ActiveCardComp;
import com.jharter.game.ashley.components.Components.CursorComp;
import com.jharter.game.ashley.components.Components.IDComp;
import com.jharter.game.ashley.components.Components.MultiSpriteComp;
import com.jharter.game.ashley.components.Components.SpriteComp;
import com.jharter.game.ashley.components.Components.TextureComp;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.Components.ZonePositionComp;
import com.jharter.game.ashley.components.Ent;
import com.jharter.game.ashley.components.Link;
import com.jharter.game.ashley.components.subcomponents.TurnAction;
import com.jharter.game.tween.TweenType;
import com.jharter.game.tween.TweenUtil;
import com.jharter.game.util.Sys;
import com.jharter.game.util.U;
import com.jharter.game.util.id.ID;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.equations.Circ;
import uk.co.carelesslabs.Enums.ZoneType;

public class CursorPositionSystem extends IteratingSystem {

	private boolean hasMulti = false;
	
	@SuppressWarnings("unchecked")
	public CursorPositionSystem() {
		super(Family.all(CursorComp.class, ZonePositionComp.class, SpriteComp.class, TextureComp.class).get());
	}

	@Override
	protected void processEntity(final Entity entity, float deltaTime) {
		if(Comp.AnimatingComp.has(entity)) {
			return;
		}
		
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
		
		if(!tt.matchesTarget(s)) {
			
			Timeline tween;
			if(isAll(c)) {
				
				float convergeX = s.position.x + (position.x - s.position.x) * 0.75f;
				float duration = 0.25f;
				
				Timeline single = TweenUtil.tween(id.id, tt, duration);
				Timeline multiA = Timeline.createParallel();
				Timeline multiB = Timeline.createParallel();
				
				float centerY = 0;
				MultiSpriteComp mp = Comp.getOrAdd(MultiSpriteComp.class, entity);
				mp.clear();
				
				int size = Link.ZoneComp.get(zp).objectIDs.size();
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
			
			TweenUtil.start(id.id, tween);
		} else if(isAll(c)) {
			hasMulti = true;
		}
		
		Pools.free(tt);
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
		tt.duration = 0.0f;
		
		if(tt.matchesTarget(s)) {
			return;
		}
		
		Sys.out.println("Tweening");
		TweenUtil.start(entity, tt);
		
		if(isAll(c)) {
			MultiSpriteComp mp = Comp.getOrAdd(MultiSpriteComp.class, entity);
			for(int i = 0; i < Link.ZoneComp.get(zp).objectIDs.size(); i++) {
				targetPosition = getCursorPosition(entity, z, i);
				if(targetPosition != null) {
					Vector3 targP = new Vector3(targetPosition);
					mp.positions.add(targP);
				}
			}
			mp.size = mp.positions.size;
			hasMulti = true;
		}
		
		Pools.free(tt);
	}
	
	private void handleTargetingTurnAction(Entity cursor, CursorComp c, ZonePositionComp zp, ZoneComp z, SpriteComp s, Vector3 position) {
		
		// Make sure cursor is in a valid place
		if(!z.hasIndex(zp.index) || z.zoneType != ZoneType.ACTIVE_CARD) {
			return;
		}
		
		// See if cursor is modifying an action
		TurnAction t = Link.CursorComp.getTurnAction(c);
		if(t == null) {
			return;
		}
		
		// See if the cursor has already selected a card from hand
		// that will force the card it targets to target all
		boolean forceAll = t.makesTargetAll;
		int forceMultiplicity = t.makesTargetMultiplicity;
		
		// Get the card that the cursor is above and verify it has a turn action associated with it
		Entity activeCard = Ent.Entity.get(z.objectIDs.get(zp.index));
		if(Comp.TurnActionComp.has(activeCard)) {
			
			TurnAction turnAction = Comp.TurnActionComp.get(activeCard).turnAction;
			if(turnAction.targetIDs.size > 1) {
				
				int multiplicity = forceMultiplicity * turnAction.multiplicity; //Math.max(forceMultiplicity, turnAction.multiplicity);
				
				// Iterate through all targets of this card, looking in particular for the last two pairs
				// of targets so we can handle their "all" status or lack thereof
				for(int j = 0; j < turnAction.targetIDs.size - 1; j++) {
					Entity subTargetEntity = Ent.Entity.get(turnAction.targetIDs.get(j+1));
					IDComp subTargetID = Comp.IDComp.get(subTargetEntity);
					ZonePositionComp subTargetZone = Comp.ZonePositionComp.get(subTargetEntity);
					ZoneComp zone = subTargetZone.getZoneComp();
					
					// If the last pairs have an "all connection", find all targets within that zone and
					// render lines to them.
					if((turnAction.all || forceAll) && j == turnAction.targetIDs.size - 2) {
						MultiSpriteComp ms = Comp.getOrAdd(MultiSpriteComp.class, cursor);
						ms.drawSingle = true;
						if(!hasMulti) {
							ms.clear();
						}
						for(int k = 0; k < zone.objectIDs.size(); k++) {
							IDComp sTargetBID = Comp.IDComp.get(Ent.Entity.get(zone.objectIDs.get(k)));

							for(int m = 0; m < multiplicity; m++) {
								
								//ms.positions.add(new Vector3(sTargetB.position.x - Units.u1(25) * m, sTargetB.position.y - Units.u1(10) * m, 0));
								Vector3 pos = getCursorPosition(cursor, zone, sTargetBID.id);
								pos.x -= U.u1(25)*m;
								pos.y -= U.u1(10)*m;
								ms.positions.add(pos);
								ms.scales.add(new Vector2(0.5f*s.scale.x, 0.5f*s.scale.y));
								//ms.anglesDegrees.add(getCursorAngle(cursor, zone.zoneType));
								if(zone.zoneType == ZoneType.ENEMY) {
									ms.reflectAngle = true;
								}
							}
							
						}
						ms.size = ms.positions.size;
						hasMulti = true;
						
					// Otherwise, connect the pairs as usual
					} else if(j == turnAction.targetIDs.size - 2) {
						//SpriteComp sTargetB = Mapper.SpriteComp.get(subTargetEntity);
						
						MultiSpriteComp ms = Comp.getOrAdd(MultiSpriteComp.class, cursor);
						ms.drawSingle = true;
						if(!hasMulti) {
							ms.clear();
						}
						for(int m = 0; m < multiplicity; m++) {
							//ms.positions.add(new Vector3(sTargetB.position.x - Units.u1(30) * m, sTargetB.position.y - Units.u1(10) * m, 0));
							Vector3 pos = getCursorPosition(cursor, zone, subTargetID.id);
							pos.x -= U.u1(25)*m;
							pos.y -= U.u1(10)*m;
							ms.positions.add(pos);
							ms.scales.add(new Vector2(0.5f*s.scale.x, 0.5f*s.scale.y));
							//ms.anglesDegrees.add(getCursorAngle(cursor, zone.zoneType));
							if(zone.zoneType == ZoneType.ENEMY) {
								ms.reflectAngle = true;
							}
						}
						ms.size = ms.positions.size;
						hasMulti = true;
					}
				}
			}
		}
	}

	private boolean isAll(CursorComp c) {
		TurnAction ta = Link.CursorComp.getTurnAction(c);
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
		Entity target = Ent.Entity.get(cursorTargetID);
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
		switch(z.zoneType) {
			case HAND:
				cursorPosition.x = lTarget.position.x + (sTarget.scaledWidth() - s.scaledWidth()) /2;
				cursorPosition.y = lTarget.position.y + sTarget.scaledHeight() - (int) (s.scaledHeight() * 0.25);
				break;
			case FRIEND:
				ActiveCardComp ac = Comp.ActiveCardComp.get(target);
				float cardOffset = 0;
				
				/*if(Mapper.ZoneComp.get(null, ZoneType.ACTIVE_CARD).hasIndex(index)) { //ac != null && ac.activeCardID != null) {
					Entity card = Mapper.Entity.get(ac.activeCardID);
					if(card != null) {
						SpriteComp sCard = Mapper.SpriteComp.get(card);
						cardOffset = sCard.scaledWidth(0.25f) + 20;
					}
					//cardOffset = 70; // XXX Fix all this!!!
				}*/
		
				cursorPosition.x = lTarget.position.x - s.scaledWidth() - U.u12(1) - cardOffset;
				cursorPosition.y = lTarget.position.y + (sTarget.scaledHeight() - s.scaledHeight()) / 2;
				break;
			case ACTIVE_CARD:
				cursorPosition.x = lTarget.position.x - s.scaledWidth() - U.u12(2);
				cursorPosition.y = lTarget.position.y + (sTarget.scaledHeight() - s.scaledHeight()) / 2;
				break;
			case ENEMY:
				cursorPosition.x = lTarget.position.x + sTarget.scaledWidth() + U.u12(2);  
				cursorPosition.y = lTarget.position.y + (sTarget.scaledHeight() - s.scaledHeight()) / 2;
				break;
			default:
				break;
		}
		
		return cursorPosition;
	}
}
