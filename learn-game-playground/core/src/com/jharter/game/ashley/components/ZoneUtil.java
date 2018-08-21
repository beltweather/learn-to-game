package com.jharter.game.ashley.components;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.ashley.core.Entity;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.Components.ZonePositionComp;
import com.jharter.game.ashley.entities.EntityUtil;
import com.jharter.game.util.id.ID;
import com.jharter.game.util.id.IDGenerator;

import uk.co.carelesslabs.Enums.ZoneType;

public class ZoneUtil {
	
	private static Map<ZoneType, ID> idsByZoneType = new HashMap<ZoneType, ID>();
	
	private ZoneUtil() {}
	
	public static ID getID(ZoneType type) {
		if(!idsByZoneType.containsKey(type)) {
			idsByZoneType.put(type, IDGenerator.newID());
		}
		return idsByZoneType.get(type);
	}
	
	public static Entity getZone(ZoneType type) {
		return EntityUtil.findEntity(getID(type));
	}
	
	public static ZoneComp ZoneComp(ZonePositionComp zp) {
		Entity zone = getZone(zp.zoneType);
		return Mapper.ZoneComp.get(zone);
	}
	
	public static ZoneComp ZoneComp(ZoneType zoneType) {
		Entity zone = getZone(zoneType);
		return Mapper.ZoneComp.get(zone);
	}

}
