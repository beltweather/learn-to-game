package com.jharter.game.stages.battlestage;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector3;
import com.jharter.game.ashley.components.Comp;
import com.jharter.game.ashley.components.EntityBuilder;
import com.jharter.game.ashley.entities.EntityUtil;
import com.jharter.game.util.id.ID;

import uk.co.carelesslabs.Enums.EntityType;
import uk.co.carelesslabs.Enums.ZoneType;
import uk.co.carelesslabs.Media;

public class CursorHelper {

	private CursorHelper() {}
	
	public static EntityBuilder buildCursor(PooledEngine engine, ID cursorID, ZoneType zoneType) {
		// XXX Shouldn't have to seed this with zone info, should be taken care of at turn start
		EntityBuilder b = EntityUtil.buildBasicEntity(engine, 
				  EntityType.CURSOR, 
				  new Vector3(-550,-100,1), 
				  Media.handPointDown);
		b.IDComp().id = cursorID;
		b.CursorComp();
		b.CursorInputRegulatorComp();
		b.CursorInputComp();
		b.ChangeZoneComp().newZoneID = Comp.Find.ZoneComp.findZoneID(Comp.Entity.DefaultTurn().ActivePlayerComp().activePlayerID, zoneType);
		b.ChangeZoneComp().newIndex = 0;
		b.ZonePositionComp().zoneID = b.ChangeZoneComp().newZoneID;
		b.ZonePositionComp();
		b.ZonePositionComp().index = 0;
		b.SpriteComp().position.z = 3;
		Comp.ZoneComp(Comp.Find.ZoneComp.findZone(null, ZoneType.CURSOR)).add(cursorID, null);;
		return b;
	}
	
}
