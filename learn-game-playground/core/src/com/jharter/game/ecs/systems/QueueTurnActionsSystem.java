package com.jharter.game.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.jharter.game.ecs.components.Components.ActionQueueableComp;
import com.jharter.game.ecs.components.Components.ActionQueuedComp;
import com.jharter.game.ecs.components.Components.ActiveTurnActionComp;
import com.jharter.game.ecs.components.Components.ChangeZoneComp;
import com.jharter.game.ecs.components.Components.IDComp;
import com.jharter.game.ecs.components.Components.TurnActionComp;
import com.jharter.game.ecs.components.Components.ZoneComp;
import com.jharter.game.ecs.components.Components.ZonePositionComp;
import com.jharter.game.ecs.systems.boilerplate.GameIteratingSystem;

import uk.co.carelesslabs.Enums.ZoneType;

public class QueueTurnActionsSystem  extends GameIteratingSystem {
	
	@SuppressWarnings("unchecked")
	public QueueTurnActionsSystem() {
		super(Family.all(TurnActionComp.class, ActionQueueableComp.class, IDComp.class, ZonePositionComp.class).get());
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		IDComp id = Comp.IDComp.get(entity);
		TurnActionComp t = Comp.TurnActionComp.get(entity);
		ZonePositionComp zp = Comp.ZonePositionComp.get(entity);
		
		Entity owner = Comp.Entity.get(t.turnAction.ownerID);
		ActiveTurnActionComp ac = Comp.getOrAdd(getEngine(), ActiveTurnActionComp.class, owner);
		
		ZoneComp z = Comp.ZoneComp.get(zp.zoneID);
		ChangeZoneComp cz = Comp.create(getEngine(), ChangeZoneComp.class);
		cz.oldZoneID = z.zoneID;
		cz.newZoneID = getZoneID(t.turnAction.ownerID, ZoneType.ACTIVE_CARD);
		cz.useNextIndex = true;
		cz.instantChange = false;
		entity.add(cz);
		
		ac.activeTurnActionID = id.id;
		
		if(t != null && t.turnAction.priority > 0) {
			t.turnAction.performAcceptCallback();
		}
		entity.remove(ActionQueueableComp.class);
		
		ActionQueuedComp aq = Comp.create(getEngine(), ActionQueuedComp.class);
		aq.queueIndex = ActionQueuedComp.QUEUE_INDEX++;
		entity.add(aq);
	}
	
}
