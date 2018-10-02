package com.jharter.game.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.jharter.game.ecs.components.Components.CardComp;
import com.jharter.game.ecs.components.Components.ChangeZoneComp;
import com.jharter.game.ecs.components.Components.MultiSpriteComp;
import com.jharter.game.ecs.components.Components.ToDiscardZoneComp;
import com.jharter.game.ecs.components.Components.TurnActionComp;
import com.jharter.game.ecs.components.Components.ZoneComp;
import com.jharter.game.ecs.components.Components.ZonePositionComp;
import com.jharter.game.ecs.systems.boilerplate.GameIteratingSystem;
import com.jharter.game.util.id.ID;

import uk.co.carelesslabs.Enums.ZoneType;

public class DiscardZoneSystem extends GameIteratingSystem {

	public DiscardZoneSystem() {
		super(Family.all(ZonePositionComp.class, ToDiscardZoneComp.class).one(CardComp.class, TurnActionComp.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		ID ownerID = Comp.CardComp.has(entity) ? Comp.CardComp.get(entity).ownerID : Comp.TurnActionComp.get(entity).turnAction.ownerID;
		ZonePositionComp zp = Comp.ZonePositionComp.get(entity);
		ZoneComp z = Comp.ZoneComp.get(zp.zoneID);
		
		if(z.zoneType != ZoneType.HAND) {
			ChangeZoneComp cz = Comp.create(getEngine(), ChangeZoneComp.class);
			cz.useNextIndex = true;
			cz.oldZoneID = z.zoneID;
			cz.newZoneID = getZoneID(ownerID, ZoneType.HAND);
			entity.add(cz);
		}
		
		// Discarding gets rid of any multi sprite information
		if(Comp.MultiSpriteComp.has(entity)) {
			entity.remove(MultiSpriteComp.class);
		}
		
		Comp.remove(ToDiscardZoneComp.class, entity);
	}

}
