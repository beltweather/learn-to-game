package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.jharter.game.ashley.components.Components.CursorComp;
import com.jharter.game.ashley.components.Components.UntargetableComp;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.Components.ZonePositionComp;
import com.jharter.game.ashley.components.Ent;
import com.jharter.game.ashley.components.Link;
import com.jharter.game.ashley.components.Comp;
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
		CursorComp c = Comp.CursorComp.get(entity);
		ZonePositionComp zp = Comp.ZonePositionComp.get(entity);
		ZoneComp z = zp.getZoneComp();
		TurnAction t = Link.CursorComp.getTurnAction(c);
		ID playerID = Link.CursorComp.getPlayerID(c);
		
		ZoneType zoneType = z.zoneType;
		for(int i = 0; i < z.objectIDs.size(); i++) {
			ID id = z.objectIDs.get(i);
			Entity zoneItem = Ent.Entity.get(id);
			if(!isValidTarget(playerID, zoneType, t, i)) {
				if(!Comp.UntargetableComp.has(zoneItem)) {
					zoneItem.add(Comp.create(UntargetableComp.class));
				}
			} else {
				if(Comp.UntargetableComp.has(zoneItem)) {
					zoneItem.remove(UntargetableComp.class);
				}
			}
		}
		
	}

}
