package com.jharter.game.stages.battlestage;

import com.badlogic.ashley.core.PooledEngine;
import com.jharter.game.ashley.components.EntityBuilder;
import com.jharter.game.render.HealthBarRenderMethod;
import com.jharter.game.util.Units;
import com.jharter.game.util.id.ID;
import com.jharter.game.util.id.IDGenerator;

import uk.co.carelesslabs.Enums.Direction;

public class HealthBarHelper {

	private HealthBarHelper() {}
	
	public static void addHealthBar(PooledEngine engine, ID baselineID) {
		EntityBuilder b = EntityBuilder.create(engine);
		b.IDComp().id = IDGenerator.newID();
		b.SpriteComp().position.x = 0;
		b.SpriteComp().position.y = -Units.u12(2f);
		b.SpriteComp().width = Units.u12(6);
		b.SpriteComp().height = Units.u12(1f);
		b.RelativePositionComp().baselineID = baselineID;
		b.RelativePositionComp().xAlign = Direction.EAST;
		b.RelativePositionComp().yAlign = Direction.NORTH;
		b.ShapeRenderComp().renderMethod = new HealthBarRenderMethod();
		engine.addEntity(b.Entity());
		b.free();
	}

}
