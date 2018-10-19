package com.jharter.game.ecs.helpers;

import com.jharter.game.ecs.components.Components.ZoneComp;
import com.jharter.game.ecs.entities.EntityBuilder;
import com.jharter.game.ecs.entities.EntityHandler;
import com.jharter.game.ecs.entities.IEntityHandler;
import com.jharter.game.render.StatusEffectBarRenderMethod;
import com.jharter.game.util.U;
import com.jharter.game.util.id.ID;
import com.jharter.game.util.id.IDUtil;

import uk.co.carelesslabs.Enums.Direction;

public class StatusEffectBarHelper extends EntityHandler {

	public StatusEffectBarHelper(IEntityHandler handler) {
		super(handler);
	}

	public void addStatusEffectBar(ZoneComp infoZone, ID relativeToID, boolean right) {
		EntityBuilder b = EntityBuilder.create(getEngine());
		b.IDComp().id = IDUtil.newID();
		b.SpriteComp().position.x = 0;
		b.SpriteComp().position.y = -U.u12(2f);
		b.SpriteComp().position.z = 2;
		b.SpriteComp().width = U.u12(3);
		b.SpriteComp().height = U.u12(3);
		b.SpriteComp().relativePositionRules.enabled = true;
		b.SpriteComp().relativePositionRules.setRelativeToID(relativeToID);
		b.SpriteComp().relativePositionRules.xAlign = right ? Direction.EAST : Direction.WEST;
		b.SpriteComp().relativePositionRules.yAlign = Direction.CENTER;
		b.SpriteComp().relativePositionRules.offset.x = U.u12(right ? 3f : -1f);
		b.SpriteComp().relativePositionRules.offset.y = U.u12(4f);
		b.SpriteComp().relativePositionRules.tween = false;
		b.ShapeRenderComp().renderMethod = new StatusEffectBarRenderMethod(this);
		Comp.util(infoZone).add(b);
		getEngine().addEntity(b.Entity());
		b.free();
	}

}
