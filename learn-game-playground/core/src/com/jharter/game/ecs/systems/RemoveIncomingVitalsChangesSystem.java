package com.jharter.game.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.jharter.game.ecs.components.Components.AssociatedTurnActionsComp;
import com.jharter.game.ecs.components.Components.CursorComp;
import com.jharter.game.ecs.components.Components.CursorUntargetEvent;
import com.jharter.game.ecs.components.Components.IDComp;
import com.jharter.game.ecs.components.Components.PendingVitalsComp;
import com.jharter.game.ecs.components.Components.VitalsComp;
import com.jharter.game.ecs.components.subcomponents.TurnAction;
import com.jharter.game.ecs.components.subcomponents.Vitals;
import com.jharter.game.ecs.systems.boilerplate.GameIteratingSystem;
import com.jharter.game.util.id.ID;

public class RemoveIncomingVitalsChangesSystem extends GameIteratingSystem {

	public RemoveIncomingVitalsChangesSystem() {
		super(Family.all(CursorUntargetEvent.class, IDComp.class, AssociatedTurnActionsComp.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		ID id = Comp.IDComp.get(entity).id;
		CursorUntargetEvent u = Comp.CursorUntargetEvent.get(entity);
		AssociatedTurnActionsComp a = Comp.AssociatedTurnActionsComp.get(entity);
		CursorComp c = Comp.CursorComp.get(u.cursorID);

		int idx;
		if(c.turnActionID != null) {
			idx = a.turnActionIDs.indexOf(c.turnActionID, false);
		} else {
			idx = a.cursorIDs.indexOf(u.cursorID, false);
		}

		if(idx >= 0) {
			ID turnActionID = a.turnActionIDs.get(idx);
			TurnAction turnAction = Comp.TurnActionComp.get(turnActionID).turnAction;
			if(turnAction.targetIDs.contains(id, false)) {
				return;
			}
			a.turnActionIDs.removeIndex(idx);
			a.targetIndices.removeIndex(idx);
			a.cursorIDs.removeIndex(idx);
		}

		if(Comp.VitalsComp.has(entity)) {
			setPendingVitals(entity, a);
		}
	}

	public void setPendingVitals(Entity entity, AssociatedTurnActionsComp a) {
		VitalsComp v = Comp.VitalsComp.get(entity);
		PendingVitalsComp p = Comp.PendingVitalsComp.getOrAdd(entity);
		p.vitals.setFrom(v.vitals);

		Vitals temp = v.vitals;
		v.vitals = p.vitals;

		for(int i = 0; i < a.turnActionIDs.size; i++) {
			ID turnActionID = a.turnActionIDs.get(i);
			int targetIndex = a.targetIndices.get(i);
			TurnAction t = Comp.TurnActionComp.get(turnActionID).turnAction;
			t.applyResult(entity, targetIndex, true);
		}

		v.vitals = temp;
	}

}
