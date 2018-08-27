package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.jharter.game.ashley.components.Components.ChangeZoneComp;
import com.jharter.game.ashley.components.Components.CursorComp;
import com.jharter.game.ashley.components.Components.IDComp;
import com.jharter.game.ashley.components.Components.TypeComp;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.Components.ZonePositionComp;
import com.jharter.game.ashley.components.Mapper;
import com.jharter.game.util.id.ID;

public class ZonePositionSystem extends IteratingSystem {

	@SuppressWarnings("unchecked")
	public ZonePositionSystem() {
		super(Family.all(IDComp.class, TypeComp.class, ZonePositionComp.class, ChangeZoneComp.class).get());
	}
	
	public void processEntity(Entity entity, float deltaTime) {
		IDComp id = Mapper.IDComp.get(entity);
		TypeComp ty = Mapper.TypeComp.get(entity);
		ZonePositionComp zp = Mapper.ZonePositionComp.get(entity);
		ChangeZoneComp cz = Mapper.ChangeZoneComp.get(entity);
		
		ID targetZoneID;
		if(cz.newZoneID != null && cz.newZoneID != null) {
			targetZoneID = cz.newZoneID;
		} else {
			targetZoneID = zp.getZoneComp().zoneID;
		}
		
		ZoneComp z = Mapper.ZoneComp.get(targetZoneID);
		
		int targetIndex;
		if(cz.useNextIndex) {
			targetIndex = z.objectIDs.size();
		} else if(cz.newIndex >= 0) {
			targetIndex = cz.newIndex;
		} else {
			targetIndex = zp.index;
		}
		
		if(!cz.useNextIndex && !z.hasIndex(targetIndex)) {
			return;
		}
		
		switch(ty.type) {
			case CARD:
				ZoneComp zOld = Mapper.ZoneComp.get(zp);
				zOld.remove(id.id);
				z.add(id.id, zp);
				zp.index = targetIndex;
				entity.remove(ChangeZoneComp.class);
				break;
			case CURSOR:
				if(cz.checkpoint) {
					zp.checkpoint();
				}
				CursorComp c = Mapper.CursorComp.get(entity);
				c.lastZoneID = cz.oldZoneID;
				zp.zoneID = targetZoneID;
				zp.index = targetIndex;
				entity.remove(ChangeZoneComp.class);
				break;
			case FRIEND:
				
				break;
			case ENEMY:
				
				break;
			default:
				break;
		}
	}
	
}

