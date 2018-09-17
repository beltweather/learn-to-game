package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.jharter.game.ashley.components.Comp;
import com.jharter.game.ashley.components.Components.CleanupTurnActionComp;
import com.jharter.game.ashley.components.Components.ToDiscardZoneComp;
import com.jharter.game.ashley.components.Components.TurnActionComp;

public class CleanupTurnActionsSystem extends IteratingSystem {
	
	public static final float DEFAULT_INTERVAL = 10f;
	
	@SuppressWarnings("unchecked")
	public CleanupTurnActionsSystem() {
		super(Family.all(TurnActionComp.class, CleanupTurnActionComp.class).get());
	}
	
	@Override
	public void processEntity(Entity entity, float deltaTime) {
		TurnActionComp t = Comp.TurnActionComp.get(entity);
		
		t.turnAction.cleanUp();
		Comp.add(getEngine(), ToDiscardZoneComp.class, entity);

		entity.remove(CleanupTurnActionComp.class);
	}
	
	// XXX Multiplicity should be handled by some other system that checks for multiplicity comp or something
	/*if(Comp.MultiSpriteComp.has(entity) && t.turnAction.multiplicity <= 1) {
		entity.remove(MultiSpriteComp.class);
	}*/
	
	// XXX This system should not reason at all about zone position
	/*ZonePositionComp zp = Comp.ZonePositionComp.get(entity);
	if(zp != null) {
		ZoneComp z = Comp.ZonePositionComp(zp).getZoneComp();
		if(z.zoneType != ZoneType.HAND) {
			ChangeZoneComp cz = Comp.create(getEngine(), ChangeZoneComp.class);
			cz.useNextIndex = true;
			cz.oldZoneID = z.zoneID;
			cz.newZoneID = Comp.Find.ZoneComp.findZoneID(t.turnAction.ownerID, ZoneType.HAND);
			entity.add(cz);
		}
	}*/

}
