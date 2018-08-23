package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.jharter.game.ashley.components.Components.ActionQueueableComp;
import com.jharter.game.ashley.components.Components.ActionQueuedComp;
import com.jharter.game.ashley.components.Components.ActiveCardComp;
import com.jharter.game.ashley.components.Components.CardComp;
import com.jharter.game.ashley.components.Components.IDComp;
import com.jharter.game.ashley.components.Components.TypeComp;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.Components.ZonePositionComp;
import com.jharter.game.ashley.components.Mapper;

import uk.co.carelesslabs.Enums.ZoneType;

public class QueueTurnActionsSystem  extends IteratingSystem {

	@SuppressWarnings("unchecked")
	public QueueTurnActionsSystem() {
		super(Family.all(ActionQueueableComp.class, TypeComp.class, IDComp.class).get());
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		IDComp id = Mapper.IDComp.get(entity);
		TypeComp ty = Mapper.TypeComp.get(entity);
		
		if(ty != null) {
			switch(ty.type) {
				case CARD:
					ZonePositionComp zp = Mapper.ZonePositionComp.get(entity);
					zp.getZoneComp().remove(id);
					
					ZoneComp z = Mapper.ZoneComp.get(ZoneType.ACTIVE_CARD);
					CardComp ca = Mapper.CardComp.get(entity);
					Entity owner = Mapper.Entity.get(ca.ownerID);
					ActiveCardComp ac = Mapper.ActiveCardComp.get(owner);
					if(ac == null) {
						ac = Mapper.Comp.get(ActiveCardComp.class);
						owner.add(ac);
					}
					
					z.add(id, zp);
					ac.activeCardID = id.id;
					break;
				default:
					break;
			}
		}
		entity.remove(ActionQueueableComp.class);
		entity.add(Mapper.Comp.get(ActionQueuedComp.class));
	}
	
}
