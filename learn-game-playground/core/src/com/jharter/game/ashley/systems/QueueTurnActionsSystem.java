package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.jharter.game.ashley.components.Comp;
import com.jharter.game.ashley.components.Components.ActionQueueableComp;
import com.jharter.game.ashley.components.Components.ActionQueuedComp;
import com.jharter.game.ashley.components.Components.ActiveTurnActionComp;
import com.jharter.game.ashley.components.Components.ChangeZoneComp;
import com.jharter.game.ashley.components.Components.IDComp;
import com.jharter.game.ashley.components.Components.OwnerIDComp;
import com.jharter.game.ashley.components.Components.TurnActionComp;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.Components.ZonePositionComp;
import com.jharter.game.util.id.ID;

import uk.co.carelesslabs.Enums.ZoneType;

public class QueueTurnActionsSystem  extends IteratingSystem {
	
	@SuppressWarnings("unchecked")
	public QueueTurnActionsSystem() {
		super(Family.all(TurnActionComp.class, OwnerIDComp.class, ActionQueueableComp.class, IDComp.class, ZonePositionComp.class).get());
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		IDComp id = Comp.IDComp.get(entity);
		ID ownerID = Comp.OwnerIDComp.get(entity).ownerID;
		ZonePositionComp zp = Comp.ZonePositionComp.get(entity);
		
		Entity owner = Comp.Entity.get(ownerID);
		ActiveTurnActionComp ac = Comp.getOrAdd(getEngine(), ActiveTurnActionComp.class, owner);
		
		ZoneComp z = Comp.ZonePositionComp(zp).getZoneComp();
		ChangeZoneComp cz = Comp.create(getEngine(), ChangeZoneComp.class);
		cz.oldZoneID = z.zoneID;
		cz.newZoneID = Comp.Find.ZoneComp.findZoneID(ownerID, ZoneType.ACTIVE_CARD);
		cz.useNextIndex = true;
		cz.instantChange = false;
		entity.add(cz);
		
		ac.activeTurnActionID = id.id;
		
		TurnActionComp t = Comp.TurnActionComp.get(entity);
		if(t != null && t.turnAction.priority > 0) {
			t.turnAction.performAcceptCallback();
		}
		entity.remove(ActionQueueableComp.class);
		
		ActionQueuedComp aq = Comp.create(getEngine(), ActionQueuedComp.class);
		aq.queueIndex = ActionQueuedComp.QUEUE_INDEX++;
		entity.add(aq);
	}
	
}
