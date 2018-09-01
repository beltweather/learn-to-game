package com.jharter.game.stages.battlestage;

import com.badlogic.ashley.core.PooledEngine;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.EntityBuilder;
import com.jharter.game.render.HealthBarRenderMethod;
import com.jharter.game.util.Units;
import com.jharter.game.util.id.ID;
import com.jharter.game.util.id.IDGenerator;

import uk.co.carelesslabs.Enums.Direction;

public class HealthBarHelper {

	private HealthBarHelper() {}
	
	public static void addHealthBar(PooledEngine engine, ZoneComp infoZone, ID relativeToID) {
		EntityBuilder b = EntityBuilder.create(engine);
		b.IDComp().id = IDGenerator.newID();
		b.SpriteComp().position.x = 0;
		b.SpriteComp().position.y = -Units.u12(2f);
		b.SpriteComp().position.z = 2;
		b.SpriteComp().width = Units.u12(6);
		b.SpriteComp().height = Units.u12(1f);
		b.SpriteComp().relativePositionRules.relative = true;
		b.SpriteComp().relativePositionRules.setRelativeToID(relativeToID);
		b.SpriteComp().relativePositionRules.xAlign = Direction.EAST;
		b.SpriteComp().relativePositionRules.yAlign = Direction.CENTER;
		b.SpriteComp().relativePositionRules.offset.y = Units.u12(5);
		b.SpriteComp().relativePositionRules.tween = false;
		b.ShapeRenderComp().renderMethod = new HealthBarRenderMethod();
		infoZone.add(b);
		engine.addEntity(b.Entity());
		b.free();
	}

}
