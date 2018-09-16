package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.jharter.game.ashley.components.Comp;
import com.jharter.game.ashley.components.Components.ActiveTurnActionComp;
import com.jharter.game.ashley.components.Components.ChangeZoneComp;
import com.jharter.game.ashley.components.Components.CleanupTurnActionComp;
import com.jharter.game.ashley.components.Components.MultiSpriteComp;
import com.jharter.game.ashley.components.Components.TurnActionComp;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.Components.ZonePositionComp;

import uk.co.carelesslabs.Enums.ZoneType;

public class CleanupTurnActionsSystem extends IteratingSystem {
	
	public static final float DEFAULT_INTERVAL = 10f;
	
	@SuppressWarnings("unchecked")
	public CleanupTurnActionsSystem() {
		super(Family.all(TurnActionComp.class, CleanupTurnActionComp.class).get());
	}
	
	@Override
	public void processEntity(Entity entity, float deltaTime) {
		// Cleanup our turn action, this makes total sense here.
		TurnActionComp t = Comp.TurnActionComp.get(entity);
		t.turnAction.cleanUp();
		
		// XXX Multiplicity should be handled by some other system that checks for multiplicity comp or something
		if(Comp.MultiSpriteComp.has(entity) && t.turnAction.multiplicity <= 1) {
			entity.remove(MultiSpriteComp.class);
		}
		
		// Handling active card comp here for the owner also seems like a weird cross reference we don't want
		Entity owner = Comp.Entity.get(t.turnAction.ownerID);
		if(Comp.ActiveTurnActionComp.has(owner)) {
			owner.remove(ActiveTurnActionComp.class);
		}
		
		// If our entity has a zone position, handle that.
		// This also feels weird here, maybe instead we want an "home zone" component
		// that will check to see if an entity is without a zone and return it to its
		// home in some other system.
		ZonePositionComp zp = Comp.ZonePositionComp.get(entity);
		if(zp != null) {
			ZoneComp z = Comp.ZonePositionComp(zp).getZoneComp();
			if(z.zoneType != ZoneType.HAND) {
				ChangeZoneComp cz = Comp.create(getEngine(), ChangeZoneComp.class);
				cz.useNextIndex = true;
				cz.oldZoneID = z.zoneID;
				cz.newZoneID = Comp.Find.ZoneComp.findZoneID(t.turnAction.ownerID, ZoneType.HAND);
				entity.add(cz);
			}
		}
		
		entity.remove(CleanupTurnActionComp.class);
	}
	
}
