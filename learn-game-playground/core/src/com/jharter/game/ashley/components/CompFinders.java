package com.jharter.game.ashley.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.Components.ZonePositionComp;
import com.jharter.game.util.id.ID;
import com.jharter.game.util.id.IDUtil;

import uk.co.carelesslabs.Enums.ZoneType;

public class CompFinders {
	
	CompFinders() {}
	
	private class CompFinder<T extends Component> {
		
	}
	
	public class CompFinderZoneComp extends CompFinder<ZoneComp> {
		
		private CompFinderZoneComp() {}
		
		public ID findZoneID(ID ownerID, ZoneType type) {
			return IDUtil.getZoneID(ownerID, type);
		}
		
		public ZoneComp findZone(Entity entity) {
			return findZone(Comp.ZonePositionComp.get(entity));
		}
		
		public ZoneComp findZone(ZonePositionComp zp) {
			Entity zone = Comp.Entity.get(zp.zoneID);
			return com.jharter.game.ashley.components.Comp.ZoneComp.get(zone);
		}
		
		public ZoneComp findZone(ID ownerID, ZoneType zoneType) {
			ID zoneID = findZoneID(ownerID, zoneType);
			if(zoneID == null) {
				return null;
			}
			Entity zone = Comp.Entity.get(zoneID);
			if(zone == null) {
				return null;
			}
			return com.jharter.game.ashley.components.Comp.ZoneComp.get(zone);
		}
		
	}
	
	public final CompFinderZoneComp ZoneComp = new CompFinderZoneComp();
	
}
