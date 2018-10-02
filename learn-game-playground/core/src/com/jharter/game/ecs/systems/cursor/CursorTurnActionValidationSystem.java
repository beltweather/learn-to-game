package com.jharter.game.ecs.systems.cursor;

import com.badlogic.ashley.core.Entity;
import com.jharter.game.ecs.components.Components.CleanupTurnActionComp;
import com.jharter.game.ecs.components.Components.CursorComp;
import com.jharter.game.ecs.components.Components.PendingTurnActionComp;

public class CursorTurnActionValidationSystem extends CursorSystem {

	@SuppressWarnings("unchecked")
	public CursorTurnActionValidationSystem() {
		super();
		add(PendingTurnActionComp.class);
	}

	@Override
	public void processEntity(Entity cursor, CursorComp c, float deltaTime) {
		Entity turnActionEntity = Comp.Entity.get(c.turnActionID);
		if(turnActionEntity == null) {
			for(Entity ptaEntity : getEntities(PendingTurnActionComp.class)) {
				Comp.swap(PendingTurnActionComp.class, CleanupTurnActionComp.class, ptaEntity);
			}
			if(c.turnActionID != null) {
				c.turnActionID = null;
			}
		}
	}
	
}
