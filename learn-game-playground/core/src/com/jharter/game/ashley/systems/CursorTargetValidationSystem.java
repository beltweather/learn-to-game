package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.jharter.game.ashley.components.Components.CursorComp;
import com.jharter.game.ashley.components.Components.UntargetableComp;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.Components.ZonePositionComp;
import com.jharter.game.ashley.components.M;
import com.jharter.game.ashley.components.subcomponents.CompLinker;
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
		CursorComp c = M.CursorComp.get(entity);
		ZonePositionComp zp = M.ZonePositionComp.get(entity);
		ZoneComp z = zp.getZoneComp();
		TurnAction t = CompLinker.CursorComp.getTurnAction(c);
		
		ZoneType zoneType = z.zoneType;
		for(int i = 0; i < z.objectIDs.size(); i++) {
			ID id = z.objectIDs.get(i);
			Entity zoneItem = M.Entity.get(id);
			if(!isValidTarget(c.playerID(), zoneType, t, i)) {
				if(!M.UntargetableComp.has(zoneItem)) {
					zoneItem.add(M.Comp.get(UntargetableComp.class));
				}
			} else {
				if(M.UntargetableComp.has(zoneItem)) {
					zoneItem.remove(UntargetableComp.class);
				}
			}
		}
		
	}

}
