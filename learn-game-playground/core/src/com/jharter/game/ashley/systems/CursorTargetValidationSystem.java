package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.jharter.game.ashley.components.Comp;
import com.jharter.game.ashley.components.Components.UntargetableComp;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.subcomponents.TurnAction;

import uk.co.carelesslabs.Enums.ZoneType;

public class CursorTargetValidationSystem extends IteratingSystem {

	private ZoneComp zCursor;
	
	@SuppressWarnings("unchecked")
	public CursorTargetValidationSystem() {
		super(Family.all(ZoneComp.class).get());
	}
	
	@Override
	public void update(float deltaTime) {
		zCursor = Comp.Entity.CursorEntity.ZonePositionComp().getZoneComp();
		super.update(deltaTime);
	}

	@Override
	public void processEntity(Entity zone, float deltaTime) {
		ZoneComp z = Comp.ZoneComp.get(zone);
		if(z.zoneID == zCursor.zoneID) {
			handleZoneWithCursor(z);
		} else {
			handleZoneWithoutCursor(z);
		}
	}
	
	public void handleZoneWithCursor(ZoneComp z) {
		Entity cursor = Comp.Entity.CursorEntity.Entity();
		for(int i = 0; i < z.objectIDs.size(); i++) {
			Entity zoneItem = Comp.Entity.get(z.objectIDs.get(i));
			if(!Comp.Method.CursorComp.isValidTarget(cursor, i)) {
				Comp.add(getEngine(), UntargetableComp.class, zoneItem);
			} else {
				Comp.remove(UntargetableComp.class, zoneItem);
			}
		}
	}
	
	public void handleZoneWithoutCursor(ZoneComp z) {
		switch(z.zoneType) {
			case HAND:
				handleHandZoneWithoutCursor(z);
				break;
			case ACTIVE_CARD:
				handleActiveCardZoneWithoutCursor(z);
				break;
			default:
				handleDefaultZonesWithoutCursor(z);
				break;
		}
	}
	
	public void handleDefaultZonesWithoutCursor(ZoneComp z) {
		for(int i = 0; i < z.objectIDs.size(); i++) {
			Comp.remove(UntargetableComp.class, Comp.Entity.get(z.objectIDs.get(i)));
		}		
	}
	
	public void handleHandZoneWithoutCursor(ZoneComp z) {
		for(int i = 0; i < z.objectIDs.size(); i++) {
			Entity card = Comp.Entity.get(z.objectIDs.get(i));
			TurnAction t = Comp.TurnActionComp.get(card).turnAction;
			if(t.targetIDs.size > 0) {
				Comp.remove(UntargetableComp.class, card);
			} else {
				Comp.add(getEngine(), UntargetableComp.class, card);
			}
		}
	}
	
	public void handleActiveCardZoneWithoutCursor(ZoneComp z) {
		if(zCursor.zoneType != ZoneType.FRIEND) {
			handleDefaultZonesWithoutCursor(z);
			return;
		}
		for(int i = 0; i < z.objectIDs.size(); i++) {
			Comp.add(getEngine(), UntargetableComp.class, Comp.Entity.get(z.objectIDs.get(i)));
		}
	}

}
