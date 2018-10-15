package com.jharter.game.ecs.systems.cursor;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.jharter.game.ecs.components.Components.TurnActionSelectedEvent;
import com.jharter.game.ecs.components.Components.TurnActionQueueItemComp;
import com.jharter.game.ecs.components.Components.CleanupTurnActionTag;
import com.jharter.game.ecs.components.Components.CursorComp;
import com.jharter.game.ecs.components.Components.PendingTurnActionTag;

public class CursorTurnActionValidationSystem extends CursorSystem {

	@SuppressWarnings("unchecked")
	public CursorTurnActionValidationSystem() {
		super();
		add(PendingTurnActionTag.class, Family.all(PendingTurnActionTag.class).exclude(TurnActionSelectedEvent.class, TurnActionQueueItemComp.class).get());
	}

	@Override
	public void processEntity(Entity cursor, CursorComp c, float deltaTime) {
		Entity turnActionEntity = Comp.Entity.get(c.turnActionID);
		if(turnActionEntity == null) {
			for(Entity ptaEntity : entities(PendingTurnActionTag.class)) {
				Comp.PendingTurnActionTag.swap(CleanupTurnActionTag.class, ptaEntity);
			}
			c.turnActionID = null;
		}
	}

}
