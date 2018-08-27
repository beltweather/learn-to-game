package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.jharter.game.ashley.components.Components.CursorComp;
import com.jharter.game.ashley.components.Components.UntargetableComp;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.Components.ZonePositionComp;
import com.jharter.game.ashley.components.Mapper;
import com.jharter.game.ashley.components.subcomponents.TurnAction;
import com.jharter.game.util.id.ID;

import uk.co.carelesslabs.Enums.ZoneType;

public class CursorTargetValidationSystem extends AbstractCursorOperationSystem {

	@SuppressWarnings("unchecked")
	public CursorTargetValidationSystem() {
		super(Family.all(CursorComp.class,
						 ZonePositionComp.class).get());
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		CursorComp c = Mapper.CursorComp.get(entity);
		ZonePositionComp zp = Mapper.ZonePositionComp.get(entity);
		ZoneComp z = zp.getZoneComp();
		TurnAction t = c.getTurnAction();
		
		ZoneType zoneType = z.zoneType;
		for(int i = 0; i < z.objectIDs.size(); i++) {
			ID id = z.objectIDs.get(i);
			Entity zoneItem = Mapper.Entity.get(id);
			if(!isValidTarget(c.playerID(), zoneType, t, i)) {
				if(!Mapper.UntargetableComp.has(zoneItem)) {
					zoneItem.add(Mapper.Comp.get(UntargetableComp.class));
				}
			} else {
				if(Mapper.UntargetableComp.has(zoneItem)) {
					zoneItem.remove(UntargetableComp.class);
				}
			}
		}
		
	}

}
