package com.jharter.game.ecs.systems.cursor;

import com.badlogic.ashley.core.Entity;
import com.jharter.game.ecs.components.Components.CleanupTurnActionTag;
import com.jharter.game.ecs.components.Components.CursorComp;
import com.jharter.game.ecs.components.Components.PendingTurnActionTag;

public class CursorTurnActionValidationSystem extends CursorSystem {

	@SuppressWarnings("unchecked")
	public CursorTurnActionValidationSystem() {
		super();
		add(PendingTurnActionTag.class);
	}

	@Override
	public void processEntity(Entity cursor, CursorComp c, float deltaTime) {
		Entity turnActionEntity = Comp.Entity.get(c.turnActionID);
		if(turnActionEntity == null) {
			for(Entity ptaEntity : entities(PendingTurnActionTag.class)) {
				Comp.swap(PendingTurnActionTag.class, CleanupTurnActionTag.class, ptaEntity);
			}
			c.turnActionID = null;
		}
	}
	
}
