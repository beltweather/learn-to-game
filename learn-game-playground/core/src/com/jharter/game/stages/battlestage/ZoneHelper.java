package com.jharter.game.stages.battlestage;

import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.EntityBuilder;
import com.jharter.game.ashley.entities.EntityFactory;
import com.jharter.game.ashley.entities.IEntityFactory;
import com.jharter.game.layout.IdentityLayout;
import com.jharter.game.layout.ZoneLayout;
import com.jharter.game.util.id.ID;

import uk.co.carelesslabs.Enums.ZoneType;

public class ZoneHelper extends EntityFactory {
	
	public ZoneHelper(IEntityFactory factory) {
		super(factory);
	}

	public ZoneComp addZone(ID playerID, ZoneType zoneType) {
		return addZone(playerID, zoneType, null);
	}
	
	public ZoneComp addZone(ID playerID, ZoneType zoneType, ZoneLayout layout) {
		EntityBuilder b = EntityBuilder.create(getEngine());
		b.IDComp().id = getIDManager().generateZoneID(playerID, zoneType);
		b.ZoneComp().zoneID = b.IDComp().id;
		b.ZoneComp().zoneType = zoneType;
		if(layout == null) {
			layout = new IdentityLayout(this);
		}
		layout.setIds(b.ZoneComp().objectIDs);
		b.ZoneComp().layout = layout;
		ZoneComp z = b.ZoneComp();
		getEngine().addEntity(b.Entity());
		b.free();
		return z;
	}
 	
}
