package com.jharter.game.stages.battlestage;

import com.badlogic.ashley.core.PooledEngine;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.EntityBuilder;
import com.jharter.game.layout.IdentityLayout;
import com.jharter.game.layout.ZoneLayout;
import com.jharter.game.util.id.ID;
import com.jharter.game.util.id.IDUtil;

import uk.co.carelesslabs.Enums.ZoneType;

public class ZoneHelper {

	private ZoneHelper() {}
	
	public static ZoneComp addZone(PooledEngine engine, ID playerID, ZoneType zoneType) {
		return addZone(engine, playerID, zoneType, null);
	}
	
	public static ZoneComp addZone(PooledEngine engine, ID playerID, ZoneType zoneType, ZoneLayout layout) {
		EntityBuilder b = EntityBuilder.create(engine);
		b.IDComp().id = IDUtil.generateZoneID(playerID, zoneType);
		b.ZoneComp().zoneID = b.IDComp().id;
		b.ZoneComp().zoneType = zoneType;
		if(layout == null) {
			layout = new IdentityLayout();
		}
		layout.setIds(b.ZoneComp().objectIDs);
		b.ZoneComp().layout = layout;
		ZoneComp z = b.ZoneComp();
		engine.addEntity(b.Entity());
		b.free();
		return z;
	}
 	
}
