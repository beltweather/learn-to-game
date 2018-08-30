package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.jharter.game.ashley.components.Components.ActionSpentComp;
import com.jharter.game.ashley.components.Components.ActiveCardComp;
import com.jharter.game.ashley.components.Components.CardComp;
import com.jharter.game.ashley.components.Components.ChangeZoneComp;
import com.jharter.game.ashley.components.Components.IDComp;
import com.jharter.game.ashley.components.Components.MultiSpriteComp;
import com.jharter.game.ashley.components.Components.TurnActionComp;
import com.jharter.game.ashley.components.Components.TypeComp;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.Components.ZonePositionComp;
import com.jharter.game.ashley.components.Mapper;

import uk.co.carelesslabs.Enums.ZoneType;

public class CleanupTurnActionsSystem extends IteratingSystem {
	
	public static final float DEFAULT_INTERVAL = 10f;
	
	@SuppressWarnings("unchecked")
	public CleanupTurnActionsSystem() {
		super(Family.all(ActionSpentComp.class, IDComp.class, TypeComp.class).get());
	}
	
	@Override
	public void processEntity(Entity entity, float deltaTime) {
		IDComp id = Mapper.IDComp.get(entity);
		TypeComp ty = Mapper.TypeComp.get(entity);
		
		TurnActionComp t = Mapper.TurnActionComp.get(entity);
		if(t != null) {
			t.turnAction.cleanUp();
		}
		
		if(ty != null) {
			switch(ty.type) {
				case CARD:
					//ZonePositionComp zp = Mapper.ZonePositionComp.get(entity);
					//zp.getZoneComp().remove(id);
					
					if(Mapper.MultiSpriteComp.has(entity) && (t == null || t.turnAction.multiplicity <= 1)) {
						entity.remove(MultiSpriteComp.class);
					}
					
					CardComp ca = Mapper.CardComp.get(entity);
					Entity owner = Mapper.Entity.get(ca.playerID);
					ZonePositionComp zp = Mapper.ZonePositionComp.get(entity);
					if(Mapper.ActiveCardComp.has(owner)) {
						owner.remove(ActiveCardComp.class);
					}
					
					ZoneComp z = zp.getZoneComp();
					ChangeZoneComp cz = Mapper.Comp.get(ChangeZoneComp.class);
					cz.useNextIndex = true;
					cz.oldZoneID = z.zoneID;
					cz.newZoneID = Mapper.ZoneComp.getID(ca.playerID, ZoneType.HAND);
					entity.add(cz);
					break;
				default:
					break;
			}
		}
		
		entity.remove(ActionSpentComp.class);
	}
	
}
