package com.jharter.game.ecs.systems.cursor;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.jharter.game.ecs.components.Components.AnimatingComp;
import com.jharter.game.ecs.components.Components.CursorChangedZoneEvent;
import com.jharter.game.ecs.components.Components.CursorComp;
import com.jharter.game.ecs.components.Components.CursorTargetComp;
import com.jharter.game.ecs.components.Components.CursorTargetEvent;
import com.jharter.game.ecs.components.Components.CursorUntargetEvent;
import com.jharter.game.ecs.components.Components.IDComp;
import com.jharter.game.ecs.components.Components.MultiSpriteComp;
import com.jharter.game.ecs.components.Components.SpriteComp;
import com.jharter.game.ecs.components.Components.ZoneComp;
import com.jharter.game.ecs.components.Components.ZonePositionComp;
import com.jharter.game.layout.TweenTarget;
import com.jharter.game.tween.TweenType;
import com.jharter.game.util.U;
import com.jharter.game.util.id.ID;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenEquations;
import aurelienribon.tweenengine.equations.Circ;
import aurelienribon.tweenengine.equations.Linear;

@SuppressWarnings("unchecked")
public class CursorMultiTargetSystem extends CursorSystem {
	
	public CursorMultiTargetSystem() {
		super();
		add(CursorTargetEvent.class, Family.all(CursorTargetEvent.class, IDComp.class, CursorTargetComp.class, SpriteComp.class, ZonePositionComp.class).get());
		add(CursorUntargetEvent.class, Family.all(CursorUntargetEvent.class, IDComp.class).get());
	}
	
	@Override
	protected void processEntity(Entity cursor, CursorComp c, float deltaTime) {
		if(count(CursorTargetEvent.class) > 0) {
			Comp.remove(MultiSpriteComp.class, c.targetID);
		}
		for(Entity target : entities(CursorUntargetEvent.class)) {
			removeCursorPosition(target);
		}
		for(Entity target : entities(CursorTargetEvent.class)) {
			addAllCursorPosition(target);
			addSubCursorPosition(target);
		}
	}
	
	private void addAllCursorPosition(Entity target) {
		CursorTargetComp t = Comp.CursorTargetComp.get(target);
		Entity cursor = Comp.Entity.get(t.cursorID);
		if(!Comp.has(CursorChangedZoneEvent.class, cursor) || !getCursorManager().isAll(cursor)) {
			return;
		}
		
		float offsetX = U.u12(2);
		float offsetY = U.u12(1);
		for(int i = 0; i < t.multiplicity; i++) {
			addAllCursorPosition(target, i*offsetX, i*offsetY);
		}
	}
	
	private void addAllCursorPosition(Entity target, float offsetX, float offsetY) {
		CursorTargetComp t = Comp.CursorTargetComp.get(target);
		Entity cursor = Comp.Entity.get(t.cursorID);
		if(!Comp.has(CursorChangedZoneEvent.class, cursor) || !getCursorManager().isAll(cursor)) {
			return;
		}
		ZoneComp zTarget = Comp.ZoneComp.get(Comp.ZonePositionComp.get(target).zoneID);
		SpriteComp sCursor = Comp.SpriteComp.get(cursor);
		Vector3 goalPosition = getCursorManager().getCursorPosition(cursor, Comp.IDComp.get(target).id, zTarget);
		goalPosition.x += offsetX;
		goalPosition.y += offsetY;
		
		float duration = 0.25f;
		float splitX = sCursor.position.x + (getCursorManager().getCursorPosition(cursor).x - sCursor.position.x) * 0.75f;
		float splitY = getCursorManager().getZoneCenterY(cursor, zTarget);
		Vector3 positionMulti = new Vector3(sCursor.position);
		
		Timeline multiA = Timeline.createParallel().push(Tween.to(positionMulti, TweenType.POSITION_XY.asInt(), duration).ease(Circ.INOUT).target(splitX, splitY));
		Timeline multiB = Timeline.createParallel().push(Tween.to(positionMulti, TweenType.POSITION_XY.asInt(), duration).ease(TweenEquations.easeInOutElastic).target(goalPosition.x, goalPosition.y));
		if(!t.isAll && !t.isAll && t.mainTargetID == null) {
			multiA.push(Tween.to(cursor, TweenType.ANGLE.asInt(), duration).ease(Circ.INOUT).target(getCursorManager().getCursorAngle(zTarget.zoneType)));
			multiA.push(Tween.to(cursor, TweenType.SCALE_XY.asInt(), duration).ease(Circ.INOUT).target(0.5f, 0.5f));
			multiB.push(Tween.to(cursor, TweenType.SCALE_XY.asInt(), duration).ease(Circ.OUT).target(1f, 1f));
		} 
		Timeline tween = Timeline.createSequence().push(multiA).push(multiB).setCallback(new TweenCallback() {

			@Override
			public void onEvent(int type, BaseTween<?> source) {
				removeCursorPosition(target);
			}
			
		});
		getTweenManager().start(t.cursorID, tween);

		MultiSpriteComp mp = Comp.getOrAdd(MultiSpriteComp.class, cursor);
		mp.drawSingle = false;
		mp.positions.add(positionMulti);
		mp.ids.add(Comp.IDComp.get(target).id);
		mp.size = mp.positions.size;
	}
	
	private void addSubCursorPosition(Entity target) {
		CursorTargetComp t = Comp.CursorTargetComp.get(target);
		if(!t.isSub) {
			return;
		}
		
		float offsetX = U.u12(2);
		float offsetY = U.u12(1);
		for(int i = 0; i < t.multiplicity; i++) {
			addSubCursorPosition(target, i*offsetX, i*offsetY);
		}
	}
	
	private void addSubCursorPosition(Entity target, float offsetX, float offsetY) {
		CursorTargetComp t = Comp.CursorTargetComp.get(target);
		if(!t.isSub) {
			return;
		}
		
		ID targetID = Comp.IDComp.get(target).id;
		ZoneComp zTarget = Comp.ZoneComp.get(Comp.ZonePositionComp.get(target).zoneID);
		float angle = getCursorManager().getCursorAngle(zTarget.zoneType);
		Vector3 position = getCursorManager().getCursorPosition(t.cursorID, targetID, zTarget);
		position.x += offsetX;
		position.y += offsetY;
		SpriteComp s = Comp.SpriteComp.get(t.cursorID);
		TweenTarget tt = TweenTarget.newInstance();
		tt.setFromEntity(this, target);
		tt.position.set(position);
		tt.angleDegrees = angle;
		tt.duration = 0;
		
		MultiSpriteComp mp = Comp.getOrAdd(MultiSpriteComp.class, Comp.Entity.get(t.cursorID));
		mp.drawSingle = true;
		mp.positions.add(position);
		mp.anglesDegrees.add(angle);
		mp.alphas.add(0f);
		mp.scales.add(new Vector2(0.5f*s.scale.x, 0.5f*s.scale.y));
		mp.ids.add(targetID);
		mp.size = mp.positions.size;
		
		getTweenManager().start(null, Tween.to(mp.alphas, TweenType.ALPHA.asInt(), 0.5f).ease(Linear.INOUT).target(1f));
	}		
	
	private void removeCursorPosition(Entity target) {
		ID cursorID = Comp.CursorUntargetEvent.get(target).cursorID;
		if(cursorID == null || Comp.has(AnimatingComp.class, cursorID)) {
			return;
		}
		MultiSpriteComp mp = Comp.MultiSpriteComp.get(cursorID);
		if(mp == null) {
			return;
		}
		if(mp.positions.size > 0) {
			ID targetID = Comp.IDComp.get(target).id;
			int index = mp.ids.indexOf(targetID, false);
			while(index >= 0) {
				mp.ids.removeIndex(index);
				if(index < mp.positions.size) {
					mp.positions.removeIndex(index);
				}
				if(index < mp.anglesDegrees.size) {
					mp.anglesDegrees.removeIndex(index);
				}
				if(index < mp.alphas.size) {
					mp.alphas.removeIndex(index);
				}
				if(index < mp.scales.size) {
					mp.scales.removeIndex(index);
				}
				mp.size--;
				index = mp.ids.indexOf(targetID, false);
			}
		}
		if(mp.positions.size == 0) {
			Comp.remove(MultiSpriteComp.class, cursorID);
		}
	}
	
}
