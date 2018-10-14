package com.jharter.game.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.jharter.game.ecs.components.Components.CardComp;
import com.jharter.game.ecs.components.Components.ChangeZoneComp;
import com.jharter.game.ecs.components.Components.DiscardCardTag;
import com.jharter.game.ecs.components.Components.ZonePositionComp;
import com.jharter.game.ecs.systems.boilerplate.GameIteratingSystem;
import com.jharter.game.util.id.ID;

import uk.co.carelesslabs.Enums.ZoneType;

public class DiscardCardSystem extends GameIteratingSystem {

	public DiscardCardSystem() {
		super(Family.all(CardComp.class, ZonePositionComp.class, DiscardCardTag.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		ID ownerID = Comp.CardComp.get(entity).ownerID;
		ZonePositionComp zp = Comp.ZonePositionComp.get(entity);
		
		ChangeZoneComp cz = Comp.ChangeZoneComp.add(entity);
		Comp.util(cz).change(zp.zoneID, getZoneID(ownerID, ZoneType.DISCARD));
		
		Comp.DiscardCardTag.remove(entity);
	}

}
