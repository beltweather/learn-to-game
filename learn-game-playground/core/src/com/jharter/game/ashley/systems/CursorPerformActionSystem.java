package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.utils.Pools;
import com.jharter.game.ashley.components.Components.ActiveCardComp;
import com.jharter.game.ashley.components.Components.CardComp;
import com.jharter.game.ashley.components.Components.CursorComp;
import com.jharter.game.ashley.components.Components.IDComp;
import com.jharter.game.ashley.components.Components.PositionComp;
import com.jharter.game.ashley.components.Components.SizeComp;
import com.jharter.game.ashley.components.Components.TargetingComp;
import com.jharter.game.ashley.components.Components.TypeComp;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.Components.ZonePositionComp;
import com.jharter.game.ashley.components.Mapper;

import uk.co.carelesslabs.Enums.ZoneType;

public class CursorPerformActionSystem  extends IteratingSystem {

	@SuppressWarnings("unchecked")
	public CursorPerformActionSystem() {
		super(Family.all(CursorComp.class).get());
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		CursorComp c = Mapper.CursorComp.get(entity);
		
		TargetingComp t = c.getTargetingComp();
		if(t == null || !t.hasAllTargets()) {
			return;
		}
		
		Entity actionEntity = t.getEntity(0);
		TypeComp ty = Mapper.TypeComp.get(actionEntity);
		if(ty != null) {
			switch(ty.type) {
				case CARD:
					IDComp id = Mapper.IDComp.get(actionEntity);
					ZonePositionComp zp = Mapper.ZonePositionComp.get(actionEntity);
					zp.getZoneComp().remove(id);
					
					ZoneComp z = Mapper.ZoneComp.get(ZoneType.ACTIVE_CARD);
					CardComp ca = Mapper.CardComp.get(actionEntity);
					Entity owner = Mapper.Entity.get(ca.ownerID);
					ActiveCardComp ac = Mapper.ActiveCardComp.get(owner);
					if(ac == null) {
						ac = Mapper.NewComp.get(ActiveCardComp.class);
						owner.add(ac);
					}
					
					z.add(id, zp);
					ac.activeCardID = id.id;
					break;
				default:
					break;
			}
		}
		
		t.performAcceptCallback();
		
		// Clear out data related to action
		c.targetingEntityID = null;
		t.targetIDs.clear();
		
	}
	
}
