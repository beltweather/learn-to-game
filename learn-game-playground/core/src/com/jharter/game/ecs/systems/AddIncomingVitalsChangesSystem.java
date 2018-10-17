package com.jharter.game.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.jharter.game.ecs.components.Components.AssociatedTurnActionsComp;
import com.jharter.game.ecs.components.Components.CursorComp;
import com.jharter.game.ecs.components.Components.CursorTargetComp;
import com.jharter.game.ecs.components.Components.CursorTargetEvent;
import com.jharter.game.ecs.components.Components.PendingVitalsComp;
import com.jharter.game.ecs.components.Components.VitalsComp;
import com.jharter.game.ecs.components.subcomponents.TurnAction;
import com.jharter.game.ecs.components.subcomponents.Vitals;
import com.jharter.game.ecs.entities.GameToolBox;
import com.jharter.game.ecs.helpers.CombatHelper;
import com.jharter.game.ecs.systems.boilerplate.GameIteratingSystem;
import com.jharter.game.util.id.ID;

public class AddIncomingVitalsChangesSystem extends GameIteratingSystem {

	private CombatHelper CombatHelper;

	public AddIncomingVitalsChangesSystem() {
		super(Family.all(CursorTargetEvent.class, AssociatedTurnActionsComp.class, CursorTargetComp.class).get());
		CombatHelper = new CombatHelper(this);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		CursorTargetComp ct = Comp.CursorTargetComp.get(entity);
		AssociatedTurnActionsComp a = Comp.AssociatedTurnActionsComp.get(entity);
		CursorComp c = Comp.CursorComp.get(ct.cursorID);
		if(c.turnActionID == null || a.turnActionIDs.contains(c.turnActionID, false)) {
			return;
		}

		TurnAction t = Comp.TurnActionComp.get(c.turnActionID).turnAction;
		a.turnActionIDs.add(c.turnActionID);
		a.targetIndices.add(t.targetIDs.size);
		a.cursorIDs.add(ct.cursorID);

		if(Comp.VitalsComp.has(entity)) {
			setPendingVitals(entity, a);
		}
	}

	@Override
	public void setToolBox(GameToolBox toolBox) {
		super.setToolBox(toolBox);
		CombatHelper.setHandler(this);
	}

	public void setPendingVitals(Entity entity, AssociatedTurnActionsComp a) {

		PendingVitalsComp p = Comp.PendingVitalsComp.getOrAdd(entity);
		VitalsComp v = Comp.VitalsComp.get(entity);
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
