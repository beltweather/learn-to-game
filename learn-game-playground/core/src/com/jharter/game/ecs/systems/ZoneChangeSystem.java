package com.jharter.game.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.jharter.game.ecs.components.Components.ChangeZoneComp;
import com.jharter.game.ecs.components.Components.IDComp;
import com.jharter.game.ecs.components.Components.TypeComp;
import com.jharter.game.ecs.components.Components.ZoneComp;
import com.jharter.game.ecs.components.Components.ZonePositionComp;
import com.jharter.game.ecs.systems.boilerplate.GameIteratingSystem;
import com.jharter.game.util.Sys;
import com.jharter.game.util.id.ID;

public class ZoneChangeSystem extends GameIteratingSystem {

	@SuppressWarnings("unchecked")
	public ZoneChangeSystem() {
		super(Family.all(IDComp.class, ZonePositionComp.class, ChangeZoneComp.class).get());
	}
	
	public void processEntity(Entity entity, float deltaTime) {
		IDComp id = Comp.IDComp.get(entity);
		ZonePositionComp zp = Comp.ZonePositionComp.get(entity);
		ChangeZoneComp cz = Comp.ChangeZoneComp.get(entity);
		
		ID targetZoneID;
		if(cz.newZoneID != null && cz.newZoneID != null) {
			targetZoneID = cz.newZoneID;
		} else {
			targetZoneID = zp.zoneID;
		}
		
		ZoneComp z = Comp.ZoneComp.get(Comp.Entity.get(targetZoneID));
		
		int targetIndex;
		if(cz.useNextIndex) {
			targetIndex = z.objectIDs.size;
		} else if(cz.newIndex >= 0) {
			targetIndex = cz.newIndex;
		} else {
			targetIndex = zp.index;
		}
		
		if(!cz.useNextIndex && !Comp.util(z).hasIndex(targetIndex)) {
			return;
		}
		
		ZoneComp zOld = Comp.ZoneComp.get(zp.zoneID);
		Comp.util(zOld).remove(id.id);
		Comp.util(z).add(id.id, zp);
		
		Sys.out.println("ZoneChange from " + zOld.zoneType.name() + " to " + z.zoneType.name());
		
		zp.index = targetIndex;
		entity.remove(ChangeZoneComp.class);
	}
	
}

