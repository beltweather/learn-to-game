package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.jharter.game.ashley.components.Comp;
import com.jharter.game.ashley.components.Components.CursorComp;
import com.jharter.game.ashley.components.Components.UntargetableComp;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.Components.ZonePositionComp;
import com.jharter.game.util.id.ID;

public class CursorTargetValidationSystem extends IteratingSystem {

	@SuppressWarnings("unchecked")
	public CursorTargetValidationSystem() {
		super(Family.all(CursorComp.class,
						 ZonePositionComp.class).get());
	}

	@Override
	public void processEntity(Entity cursor, float deltaTime) {
		ZoneComp z = Comp.Method.ZoneComp.get(cursor);
		for(int i = 0; i < z.objectIDs.size(); i++) {
			ID id = z.objectIDs.get(i);
			Entity zoneItem = Comp.Entity.get(id);
			if(!Comp.Method.CursorComp.isValidTarget(cursor, i)) {
				if(!Comp.UntargetableComp.has(zoneItem)) {
					zoneItem.add(Comp.create(getEngine(), UntargetableComp.class));
				}
			} else {
				if(Comp.UntargetableComp.has(zoneItem)) {
					zoneItem.remove(UntargetableComp.class);
				}
			}
		}
		
	}

}
