package com.jharter.game.ecs.systems.cursor;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.jharter.game.ecs.components.Components.CursorComp;
import com.jharter.game.ecs.components.Components.CursorTargetComp;
import com.jharter.game.ecs.components.Components.CursorTargetEvent;
import com.jharter.game.ecs.components.Components.CursorUntargetEvent;
import com.jharter.game.ecs.components.Components.IDComp;
import com.jharter.game.ecs.components.subcomponents.TurnAction;
import com.jharter.game.util.Sys;
import com.jharter.game.util.id.ID;

public class CursorTargetEventSystem extends CursorSystem {

	@SuppressWarnings("unchecked")
	public CursorTargetEventSystem() {
		super();
		add(CursorTargetComp.class, Family.all(IDComp.class, CursorTargetComp.class).get());
		event(CursorTargetEvent.class);
		event(CursorUntargetEvent.class);
	}

	@Override
	protected void processEntity(Entity cursor, CursorComp c, float deltaTime) {
		removeTargets(cursor, c);
		addTargets(cursor, c);
	}

	private void removeTargets(Entity cursor, CursorComp c) {
		for(Entity target : entities(CursorTargetComp.class)) {
			removeTarget(cursor, c, target);
		}
	}

	private void removeTarget(Entity cursor, CursorComp c, Entity target) {
		ID targetID = Comp.IDComp.get(target).id;
		if(targetID.equals(c.targetID)) {
			return;
		}
		CursorTargetComp t = Comp.CursorTargetComp.get(target);
		if(t.mainTargetID != null && t.mainTargetID.equals(c.targetID)) {
			return;
		}
		Sys.out.println("Remove target");
		Comp.CursorTargetComp.remove(target);
		Comp.CursorUntargetEvent.add(target).cursorID = Comp.IDComp.get(cursor).id;
	}

	private void addTargets(Entity cursor, CursorComp c) {
		ID cursorID = Comp.IDComp.get(cursor).id;
		CursorTargetComp t = Comp.CursorTargetComp.get(c.targetID);
		if(c.targetID == null || (t != null && t.cursorID != null && t.cursorID.equals(cursorID))) {
			return;
		}

		Entity target = Comp.Entity.get(c.targetID);
		t = Comp.CursorTargetComp.getOrAdd(target);
		t.cursorID = cursorID;
		Comp.CursorTargetEvent.add(target);

		if(getCursorManager().isAll(c)) {
			for(ID id : getCursorManager().getCursorAllIDs(c)) {
				if(id.equals(c.targetID)) {
					continue;
				}
				t = Comp.CursorTargetComp.getOrAdd(id);
				t.cursorID = cursorID;
				t.mainTargetID = c.targetID;
				t.isAll = true;
				Comp.CursorTargetEvent.add(id);
			}
		}

		if(getCursorManager().isTargetingCard(c)) {
			TurnAction cursorTurnAction = Comp.TurnActionComp.get(c.turnActionID).turnAction;
			int multiplicity = getCursorManager().getMods(c.targetID).multiplicity.v() * cursorTurnAction.makesTargetMultiplicity;
			for(ID id : getCursorManager().getCursorSecondaryIDs(c)) {
				t = Comp.CursorTargetComp.getOrAdd(id);
				t.cursorID = cursorID;
				t.mainTargetID = c.targetID;
				t.isSub = true;
				t.multiplicity = multiplicity;
				Comp.CursorTargetEvent.add(id);
			}
		}
	}

}
