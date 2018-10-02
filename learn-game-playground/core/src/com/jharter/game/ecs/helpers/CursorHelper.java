package com.jharter.game.ecs.helpers;

import com.badlogic.gdx.math.Vector3;
import com.jharter.game.ecs.components.Components.ZoneComp;
import com.jharter.game.ecs.entities.EntityBuildUtil;
import com.jharter.game.ecs.entities.EntityBuilder;
import com.jharter.game.ecs.entities.EntityHandler;
import com.jharter.game.ecs.entities.IEntityHandler;
import com.jharter.game.util.id.ID;

import uk.co.carelesslabs.Enums.EntityType;
import uk.co.carelesslabs.Enums.ZoneType;
import uk.co.carelesslabs.Media;

public class CursorHelper extends EntityHandler {

	public CursorHelper(IEntityHandler handler) {
		super(handler);
	}

	public EntityBuilder buildCursor(ZoneComp zone) {
		// XXX Shouldn't have to seed this with zone info, should be taken care of at turn start
		EntityBuilder b = EntityBuildUtil.buildBasicEntity(getEngine(), 
				  EntityType.CURSOR, 
				  new Vector3(-550,-100,1), 
				  Media.handPointDown);
		b.CursorComp();
		b.CursorInputRegulatorComp();
		b.CursorInputComp();
		b.SpriteComp().position.z = 3;
		Comp.util(zone).add(b.IDComp().id, null); 
		
		return b;
	}
	
}
