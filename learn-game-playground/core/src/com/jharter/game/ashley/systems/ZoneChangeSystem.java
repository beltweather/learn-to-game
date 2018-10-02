package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.jharter.game.ashley.components.Comp;
import com.jharter.game.ashley.components.Components.ChangeZoneComp;
import com.jharter.game.ashley.components.Components.CursorComp;
import com.jharter.game.ashley.components.Components.IDComp;
import com.jharter.game.ashley.components.Components.TypeComp;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.Components.ZonePositionComp;
import com.jharter.game.util.id.ID;

public class ZoneChangeSystem extends IteratingSystem {

	@SuppressWarnings("unchecked")
	public ZoneChangeSystem() {
		super(Family.all(IDComp.class, TypeComp.class, ZonePositionComp.class, ChangeZoneComp.class).get());
	}
	
	public void processEntity(Entity entity, float deltaTime) {
		IDComp id = Comp.IDComp.get(entity);
		TypeComp ty = Comp.TypeComp.get(entity);
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
			targetIndex = z.objectIDs.size();
		} else if(cz.newIndex >= 0) {
			targetIndex = cz.newIndex;
		} else {
			targetIndex = zp.index;
		}
		
		if(!cz.useNextIndex && !Comp.ZoneComp(z).hasIndex(targetIndex)) {
			return;
		}
		
		switch(ty.type) {
			case CARD:
				ZoneComp zOld = Comp.ZoneComp.get(zp.zoneID);
				Comp.ZoneComp(zOld).remove(id.id);
				Comp.ZoneComp(z).add(id.id, zp);
				zp.index = targetIndex;
				entity.remove(ChangeZoneComp.class);
				break;
			/*case CURSOR:
				if(cz.checkpoint) {
					Comp.ZonePositionComp(zp).checkpoint(getEngine());
				}
				CursorComp c = Comp.CursorComp.get(entity);
				c.lastZoneID = cz.oldZoneID;
				zp.zoneID = targetZoneID;
				zp.index = targetIndex;
				entity.remove(ChangeZoneComp.class);
				break;*/
			case FRIEND:
				
				break;
			case ENEMY:
				
				break;
			default:
				break;
		}
	}
	
}

