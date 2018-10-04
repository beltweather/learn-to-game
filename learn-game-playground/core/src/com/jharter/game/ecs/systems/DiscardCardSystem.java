package com.jharter.game.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.jharter.game.ecs.components.Components.CardComp;
import com.jharter.game.ecs.components.Components.ChangeZoneComp;
import com.jharter.game.ecs.components.Components.MultiSpriteComp;
import com.jharter.game.ecs.components.Components.DiscardCardComp;
import com.jharter.game.ecs.components.Components.TurnActionComp;
import com.jharter.game.ecs.components.Components.ZoneComp;
import com.jharter.game.ecs.components.Components.ZonePositionComp;
import com.jharter.game.ecs.systems.boilerplate.GameIteratingSystem;
import com.jharter.game.util.id.ID;

import uk.co.carelesslabs.Enums.ZoneType;

public class DiscardCardSystem extends GameIteratingSystem {

	public DiscardCardSystem() {
		super(Family.all(CardComp.class, ZonePositionComp.class, DiscardCardComp.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		ID ownerID = Comp.CardComp.get(entity).ownerID;
		ZonePositionComp zp = Comp.ZonePositionComp.get(entity);
		//ZoneComp z = Comp.ZoneComp.get(zp.zoneID);
		
		// XXX This is weird logic here, we need to decouple this from turn actions.
		// I think this is trying to capture the state where we've cancelled a
		// turn action but the card still has multi sprite targets or something.
		// Need a better way to remove multi sprite comp, likely another validator.
		//if(z.zoneType != ZoneType.HAND) {
			
			ChangeZoneComp cz = Comp.add(ChangeZoneComp.class, entity);
			Comp.util(cz).change(zp.zoneID, getZoneID(ownerID, ZoneType.DISCARD));
		
		//}
		
		// Discarding gets rid of any multi sprite information
		Comp.remove(DiscardCardComp.class, entity);
	}

}
