package com.jharter.game.ecs.helpers;

import com.jharter.game.ecs.components.Components.ZoneComp;
import com.jharter.game.ecs.entities.EntityBuilder;
import com.jharter.game.ecs.entities.EntityHandler;
import com.jharter.game.ecs.entities.IEntityHandler;
import com.jharter.game.render.HealthBarRenderMethod;
import com.jharter.game.util.U;
import com.jharter.game.util.id.ID;
import com.jharter.game.util.id.IDUtil;

import uk.co.carelesslabs.Enums.Direction;

public class HealthBarHelper extends EntityHandler {

	public HealthBarHelper(IEntityHandler handler) {
		super(handler);
	}

	public void addHealthBar(ZoneComp infoZone, ID relativeToID) {
		EntityBuilder b = EntityBuilder.create(getEngine());
		b.IDComp().id = IDUtil.newID();
		b.SpriteComp().position.x = 0;
		b.SpriteComp().position.y = -U.u12(2f);
		b.SpriteComp().position.z = 2;
		b.SpriteComp().width = U.u12(6);
		b.SpriteComp().height = U.u12(1f);
		b.SpriteComp().relativePositionRules.enabled = true;
		b.SpriteComp().relativePositionRules.setRelativeToID(relativeToID);
		b.SpriteComp().relativePositionRules.xAlign = Direction.EAST;
		b.SpriteComp().relativePositionRules.yAlign = Direction.CENTER;
		b.SpriteComp().relativePositionRules.offset.y = U.u12(5);
		b.SpriteComp().relativePositionRules.tween = false;
		b.ShapeRenderComp().renderMethod = new HealthBarRenderMethod(this);
		Comp.util(infoZone).add(b);
		getEngine().addEntity(b.Entity());
		b.free();
	}

}
