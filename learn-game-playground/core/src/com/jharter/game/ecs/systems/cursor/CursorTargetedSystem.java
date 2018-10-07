package com.jharter.game.ecs.systems.cursor;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.jharter.game.ecs.components.Components.CursorComp;
import com.jharter.game.ecs.components.Components.CursorTargetComp;
import com.jharter.game.ecs.components.Components.IDComp;
import com.jharter.game.ecs.components.Components.VitalsComp;
import com.jharter.game.ecs.components.subcomponents.TurnAction;
import com.jharter.game.util.id.ID;

public class CursorTargetedSystem extends CursorSystem {

	@SuppressWarnings("unchecked")
	public CursorTargetedSystem() {
		super();
		add(CursorTargetComp.class, Family.all(IDComp.class, CursorTargetComp.class).get());
	}
	
	@Override
	protected void processEntity(Entity cursor, CursorComp c, float deltaTime) {
		ID cursorID = Comp.IDComp.get(cursor).id;
		
		boolean add = false;
		if(c.targetID == null) {
			add = false;
		} else if(!Comp.has(CursorTargetComp.class, c.targetID)) {
			add = true;
		} else {
			CursorTargetComp t = Comp.CursorTargetComp.get(c.targetID);
			if(t.cursorID == null || !t.cursorID.equals(cursorID)) {
				add = true;
			}
		} 
		
		if(add) {
			Entity target = Comp.Entity.get(c.targetID);
			CursorTargetComp t = Comp.add(CursorTargetComp.class, Comp.Entity.get(c.targetID));
			t.cursorID = cursorID;
			handleTargeted(target, c);
		}
		
		removeOldTargets();
	}
	
	private void removeOldTargets() {
		for(Entity target : entities(CursorTargetComp.class)) {
			boolean remove = false;
			ID targetID = Comp.IDComp.get(target).id;
			CursorTargetComp t = Comp.CursorTargetComp.get(target);
			CursorComp c = null;
			if(t.cursorID == null) {
				remove = true;
			} else {
				c = Comp.CursorComp.get(t.cursorID);
				if(c == null) {
					remove = true;
				} else if(c.targetID == null || !c.targetID.equals(targetID)) {
					remove = true;
					c.lastTargetID = c.targetID;
				}
			}
			
			if(remove) {
				Comp.remove(CursorTargetComp.class, target);
				handleUntargeted(target, c);
			}
		}
	}
	
	private void handleTargeted(Entity entity, CursorComp c) {
		addIncomingVitals(entity, c);
	}
	
	private void handleUntargeted(Entity entity, CursorComp c) {
		removeIncomingVitals(entity);
	}
	
	private void addIncomingVitals(Entity entity, CursorComp c) {
		if(c.turnActionID == null) {
			return;
		}
		TurnAction t = Comp.TurnActionComp.get(c.turnActionID).turnAction;
		VitalsComp v = Comp.VitalsComp.get(entity);
		if(v != null) {
			v.incomingDamage = t.incomingDamage;
			v.incomingHealing = t.incomingHealing;
		}
	}
	
	private void removeIncomingVitals(Entity entity) {
		VitalsComp v = Comp.VitalsComp.get(entity);
		if(v != null) {
			v.incomingDamage = 0;
			v.incomingHealing = 0;
		}
	}
	
	private void handleSwitchFocus(Entity cursor, ID lastTargetID, ID targetID) {
		CursorComp c = Comp.CursorComp.get(cursor);
		if(c.turnActionID != null) {
			TurnAction t = Comp.TurnActionComp.get(c.turnActionID).turnAction;
			if(lastTargetID != null) {
				VitalsComp v = Comp.VitalsComp.get(lastTargetID);
				if(v != null) {
					v.incomingDamage = 0;
					v.incomingHealing = 0;
				}
			}
			if(targetID != null) {
				VitalsComp v = Comp.VitalsComp.get(targetID);
				if(v != null) {
					v.incomingDamage = t.incomingDamage;
					v.incomingHealing = t.incomingHealing;
				}
			}
		}
	}

}
