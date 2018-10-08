package com.jharter.game.ecs.systems.cursor;

import java.util.Comparator;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.utils.Array;
import com.jharter.game.ecs.components.Components.CursorChangedZoneEvent;
import com.jharter.game.ecs.components.Components.CursorComp;
import com.jharter.game.ecs.components.Components.CursorInputComp;
import com.jharter.game.ecs.components.Components.IDComp;
import com.jharter.game.ecs.components.Components.InvisibleTag;
import com.jharter.game.ecs.components.Components.SpriteComp;
import com.jharter.game.ecs.components.Components.TargetableTag;
import com.jharter.game.util.ArrayUtil;
import com.jharter.game.util.GenericUtils;
import com.jharter.game.util.id.ID;

import uk.co.carelesslabs.Media;

public class CursorMoveSystem extends CursorSystem implements Comparator<Entity> {
	
	@SuppressWarnings("unchecked")
	public CursorMoveSystem() {
		super(CursorInputComp.class);
		add(TargetableTag.class, Family.all(TargetableTag.class, SpriteComp.class, IDComp.class).get(), this);
		event(CursorChangedZoneEvent.class);
	}
	
	@Override
	public void processEntity(Entity cursor, CursorComp c, float deltaTime) {
		CursorInputComp ci = Comp.CursorInputComp.get(cursor);
		
		ID origID = c.targetID;
		ID lastTargetID = c.targetID;
		c.targetID = getNewTargetID(c, ci);
		
		// We always want a last target, so even if it's null, set it to our target.
		if(lastTargetID == null) {
			lastTargetID = c.targetID;
		}
		
		if(hasChangedZones(c, lastTargetID)) {
			Comp.add(CursorChangedZoneEvent.class, cursor);
		}
		
		Comp.toggle(InvisibleTag.class, cursor, c.targetID == null);
		
		if(!GenericUtils.safeEquals(origID, c.targetID)) {
			Media.moveBeep.play();
		}
		consumeMovement(ci);
	}
	
	private boolean hasChangedZones(CursorComp c, ID lastTargetID) {
		if(lastTargetID == null || c.targetID == null || lastTargetID.equals(c.targetID)) {
			return false;
		}
		return !Comp.ZoneComp.get(Comp.ZonePositionComp.get(lastTargetID).zoneID).zoneID.equals(
			    Comp.ZoneComp.get(Comp.ZonePositionComp.get(c.targetID).zoneID).zoneID);
	}
	
	private Array<Entity> getTargets() {
		return entitiesSorted(TargetableTag.class);
	}
	
	private int getCurrentTargetIndex(CursorComp c) {
		Array<Entity> targets = getTargets();
		if(targets.size == 0) {
			return -1;
		}
		if(c.targetID == null) {
			return -1;
		}
		for(int i = 0; i < targets.size; i++) {
			if(Comp.IDComp.get(targets.get(i)).id.equals(c.targetID)) {
				return i;
			}
		}
		return -1;
	}
	
	private boolean hasMovement(CursorInputComp ci) {
		return ci.direction.x != 0 || ci.direction.y != 0;
	}
	
	private void consumeMovement(CursorInputComp ci) {
		ci.direction.setZero();
	}
	
	private ID getNewTargetID(CursorComp c, CursorInputComp ci) {
		boolean move = hasMovement(ci) && !isTargetAll(c);
		if(!move && c.targetID != null && Comp.TargetableComp.has(c.targetID)) {
			return c.targetID;
		}
		int direction = (int) (ci.direction.x != 0 ? ci.direction.x : ci.direction.y);
		Array<Entity> targets = getTargets();
		int currentIndex = getCurrentTargetIndex(c);
		int newTargetIndex = !ArrayUtil.has(targets, currentIndex) ? 0 : ArrayUtil.findNextIndex(targets, currentIndex, direction);
		if(!ArrayUtil.has(targets, newTargetIndex)) {
			return null;
		}
		return Comp.IDComp.get(targets.get(newTargetIndex)).id;
	}

	@Override
	public int compare(Entity entityA, Entity entityB) {
		SpriteComp sA = Comp.SpriteComp.get(entityA);
		SpriteComp sB = Comp.SpriteComp.get(entityB);
		if(sA.position.y == sB.position.y) {
			return (int) Math.signum(sA.position.x - sB.position.x);
		}
		return (int) Math.signum(sB.position.y - sA.position.y);
	}
	
}
