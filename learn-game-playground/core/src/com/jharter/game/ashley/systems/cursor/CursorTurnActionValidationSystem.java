package com.jharter.game.ashley.systems.cursor;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.jharter.game.ashley.components.Comp;
import com.jharter.game.ashley.components.Components.CleanupTurnActionComp;
import com.jharter.game.ashley.components.Components.CursorComp;
import com.jharter.game.ashley.components.Components.PendingTurnActionComp;

public class CursorTurnActionValidationSystem extends CursorSystem {

	@SuppressWarnings("unchecked")
	public CursorTurnActionValidationSystem() {
		super();
		add(PendingTurnActionComp.class, Family.all(PendingTurnActionComp.class).get());
	}

	@Override
	public void processEntity(Entity cursor, CursorComp c, float deltaTime) {
		Entity turnActionEntity = Comp.Entity.get(c.turnActionID);
		if(turnActionEntity == null) {
			for(Entity ptaEntity : getEntities(PendingTurnActionComp.class)) {
				Comp.swap(getEngine(), PendingTurnActionComp.class, CleanupTurnActionComp.class, ptaEntity);
			}
			if(c.turnActionID != null) {
				c.turnActionID = null;
			}
		}
	}
	
}
