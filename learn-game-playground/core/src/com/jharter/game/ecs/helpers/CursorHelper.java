package com.jharter.game.ecs.helpers;

import com.badlogic.gdx.math.Vector3;
import com.jharter.game.ecs.entities.EntityBuildUtil;
import com.jharter.game.ecs.entities.EntityBuilder;
import com.jharter.game.ecs.entities.EntityHandler;
import com.jharter.game.ecs.entities.IEntityHandler;

import uk.co.carelesslabs.Enums.EntityType;
import uk.co.carelesslabs.Enums.ZoneType;
import uk.co.carelesslabs.Media;

public class CursorHelper extends EntityHandler {

	public CursorHelper(IEntityHandler handler) {
		super(handler);
	}

	public EntityBuilder buildCursor(ZoneType zoneType) {
		// XXX Shouldn't have to seed this with zone info, should be taken care of at turn start
		EntityBuilder b = EntityBuildUtil.buildBasicEntity(getEngine(), 
				  EntityType.CURSOR, 
				  new Vector3(-550,-100,1), 
				  Media.handPointDown);
		b.CursorComp();
		b.CursorInputRegulatorComp();
		b.CursorInputComp();
		b.SpriteComp().position.z = 3;
		Comp.ZoneComp(Comp.ZoneComp.get(getIDManager().getZoneID(null, ZoneType.CURSOR))).add(b.IDComp().id, null); 
		
		/*b.ChangeZoneComp().newZoneID = idManager.getZoneID(getActivePlayerID(engine), zoneType);
		b.ChangeZoneComp().newIndex = 0;
		b.ZonePositionComp().zoneID = b.ChangeZoneComp().newZoneID;
		b.ZonePositionComp();
		b.ZonePositionComp().index = 0;*/
		
		return b;
	}
	
}
