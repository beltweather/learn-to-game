package com.jharter.game.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.jharter.game.ecs.components.Components.CursorComp;
import com.jharter.game.ecs.components.Components.CursorTargetComp;
import com.jharter.game.ecs.components.Components.CursorTargetEvent;
import com.jharter.game.ecs.components.Components.VitalsComp;
import com.jharter.game.ecs.components.subcomponents.TurnAction;
import com.jharter.game.ecs.entities.GameToolBox;
import com.jharter.game.ecs.helpers.CombatHelper;
import com.jharter.game.ecs.systems.boilerplate.GameIteratingSystem;

public class AddIncomingVitalsChangesSystem extends GameIteratingSystem {

	private CombatHelper CombatHelper;
	
	public AddIncomingVitalsChangesSystem() {
		super(Family.all(CursorTargetEvent.class, VitalsComp.class, CursorTargetComp.class).get());
		CombatHelper = new CombatHelper(this);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		VitalsComp v = Comp.VitalsComp.get(entity);
		CursorComp c = Comp.CursorComp.get(Comp.CursorTargetComp.get(entity).cursorID);
		if(c.turnActionID == null) {
			return;
		}
		
		TurnAction t = Comp.TurnActionComp.get(c.turnActionID).turnAction;
		Entity owner = Comp.Entity.get(t.ownerID);
		int incomingDamage = CombatHelper.getDamage(owner, entity, t.incomingDamage);
		int incomingHealing = CombatHelper.getDamage(owner, entity, t.incomingHealing);
		
		if(incomingDamage > 0) {
			v.incomingDamage.put(c.turnActionID, incomingDamage);
		}
		if(incomingHealing > 0) {
			v.incomingHealing.put(c.turnActionID, incomingHealing);
		}
	}
	
	@Override
	public void setToolBox(GameToolBox toolBox) {
		super.setToolBox(toolBox);
		CombatHelper.setHandler(this);
	}

}
