package com.jharter.game.stages.battlestage;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector3;
import com.jharter.game.ashley.components.EntityBuilder;
import com.jharter.game.ashley.components.M;
import com.jharter.game.ashley.entities.EntityUtil;
import com.jharter.game.util.id.ID;

import uk.co.carelesslabs.Enums.EntityType;
import uk.co.carelesslabs.Enums.ZoneType;
import uk.co.carelesslabs.Media;

public class CursorHelper {

	private CursorHelper() {}
	
	public static EntityBuilder buildCursor(PooledEngine engine, ID cursorID, ID playerID, ZoneType zoneType) {
		// XXX Shouldn't have to seed this with zone info, should be taken care of at turn start
		EntityBuilder b = EntityUtil.buildBasicEntity(engine, 
				  EntityType.CURSOR, 
				  new Vector3(-550,-100,1), 
				  Media.handPointDown);
		b.IDComp().id = cursorID;
		b.CursorComp();
		b.ChangeZoneComp().newZoneID = M.ZoneComp.getID(playerID, zoneType);
		b.ChangeZoneComp().newIndex = 0;
		b.CursorInputRegulatorComp();
		b.CursorInputComp();
		b.ZonePositionComp().zoneID = b.ChangeZoneComp().newZoneID;
		b.ZonePositionComp().index = 0;
		b.SpriteComp().position.z = 3;
		return b;
	}
	
}
