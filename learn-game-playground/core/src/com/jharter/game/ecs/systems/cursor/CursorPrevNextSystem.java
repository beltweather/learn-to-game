package com.jharter.game.ecs.systems.cursor;

import com.badlogic.ashley.core.Entity;
import com.jharter.game.ecs.components.Components.CursorComp;
import com.jharter.game.ecs.components.Components.CursorInputComp;

public class CursorPrevNextSystem extends CursorSystem {

	@SuppressWarnings("unchecked")
	public CursorPrevNextSystem() {
		super(CursorInputComp.class);
	}

	@Override
	public void processEntity(Entity cursor, CursorComp c, float deltaTime) {
		CursorInputComp ci = Comp.CursorInputComp.get(cursor);
		
		// If these are the same, don't do anything because they're
		// either both false or cancel each other out.
		if(ci.prev == ci.next) {
			return;
		}
		
		// Otherwise, change to "next" player if "next" is true or
		// previous player if "next" is false, since we know that
		// implies that "prev" is true.
		if(changePlayer(ci.next)) {
			c.reset();
		}
		
		ci.prev = false;
		ci.next = false;
	}
	
}

