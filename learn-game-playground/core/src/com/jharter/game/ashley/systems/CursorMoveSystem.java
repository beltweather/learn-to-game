package com.jharter.game.ashley.systems;

import java.util.Comparator;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.utils.Array;
import com.jharter.game.ashley.components.Comp;
import com.jharter.game.ashley.components.Components.AnimatingComp;
import com.jharter.game.ashley.components.Components.CursorComp;
import com.jharter.game.ashley.components.Components.CursorInputComp;
import com.jharter.game.ashley.components.Components.DisabledComp;
import com.jharter.game.ashley.components.Components.IDComp;
import com.jharter.game.ashley.components.Components.SpriteComp;
import com.jharter.game.ashley.components.Components.TargetableComp;
import com.jharter.game.ashley.components.subcomponents.TurnAction;
import com.jharter.game.ashley.systems.boilerplate.FirstSystem;
import com.jharter.game.util.ArrayUtil;
import com.jharter.game.util.GenericUtils;
import com.jharter.game.util.Sys;
import com.jharter.game.util.id.ID;

import uk.co.carelesslabs.Media;

public class CursorMoveSystem extends FirstSystem {
	
	@SuppressWarnings("unchecked")
	public CursorMoveSystem() {
		super(Family.all(CursorComp.class, CursorInputComp.class).exclude(AnimatingComp.class, DisabledComp.class).get());
		add(TargetableComp.class, Family.all(TargetableComp.class, SpriteComp.class, IDComp.class).get(), new Comparator<Entity>() {

			@Override
			public int compare(Entity entityA, Entity entityB) {
				System.out.println("sorting");
				SpriteComp sA = Comp.SpriteComp.get(entityA);
				SpriteComp sB = Comp.SpriteComp.get(entityB);
				if(sA.position.y == sB.position.y) {
					return (int) Math.signum(sA.position.x - sB.position.x);
				}
				return (int) Math.signum(sB.position.y - sA.position.y);
			}
			
		});
	}
	
	@Override
	public void processEntity(Entity cursor, float deltaTime) {
		Sys.out.println("Found " + getTargets().size + " targets");
		CursorComp c = Comp.CursorComp.get(cursor);
		CursorInputComp ci = Comp.CursorInputComp.get(cursor);
		
		ID origID = c.targetID;
		c.targetID = getNewTargetID(c, ci);
		if(!GenericUtils.safeEquals(origID, c.targetID)) {
			Media.moveBeep.play();
		}
		consumeMovement(ci);
	}
	
	private Array<Entity> getTargets() {
		return getSortedEntities(TargetableComp.class);
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
	
	private boolean isAll(CursorComp c) {
		TurnAction t = Comp.CursorComp(c).turnAction();
		return t != null && t.all;
	}
	
	private ID getNewTargetID(CursorComp c, CursorInputComp ci) {
		boolean move = hasMovement(ci) && !isAll(c);
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
	
}
