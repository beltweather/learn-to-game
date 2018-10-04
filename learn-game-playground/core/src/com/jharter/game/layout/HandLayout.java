package com.jharter.game.layout;

import com.badlogic.ashley.core.Entity;
import com.jharter.game.ecs.components.Components.MultiSpriteComp;
import com.jharter.game.ecs.components.Components.SpriteComp;
import com.jharter.game.ecs.components.Components.TurnActionComp;
import com.jharter.game.ecs.entities.IEntityHandler;
import com.jharter.game.util.U;
import com.jharter.game.util.id.ID;

public class HandLayout extends ZoneLayout {

	public HandLayout(IEntityHandler handler) {
		super(handler);
	}

	@Override
	protected TweenTarget getTarget(ID id, int index, Entity entity, TweenTarget target) {
		SpriteComp s = Comp.SpriteComp.get(entity);
		s.relativePositionRules.enabled = false;
		
		float anchorX = U.u12(-30);
		float anchorY = U.u12(-41);
		
		target.position.x = anchorX + (Math.round(Comp.util(s).scaledWidth()) + U.u12(1)) * index;
		target.position.y = anchorY;
		target.position.z = s.position.z;
		target.scale.x = 1f;
		target.scale.y = 1f;
		target.angleDegrees = 0;
		
		if(Comp.UntargetableComp.has(entity)) {
			target.alpha = 0.25f;
		} else {
			target.alpha = 1f;
		}
		
		return target;
	}
	
	@Override
	protected void modifyEntity(ID id, int index, Entity entity, TweenTarget target) {
		TurnActionComp t = Comp.TurnActionComp.get(entity);
		if(t == null) {
			return;
		}
		ID ownerID = t.turnAction.ownerID;
		if(ownerID != getActivePlayerID()) {
			hide(entity);
		} else {
			show(entity);
		}
		if(t.turnAction.multiplicity <= 1) {
			Comp.remove(MultiSpriteComp.class, entity);
		}
	}

}
