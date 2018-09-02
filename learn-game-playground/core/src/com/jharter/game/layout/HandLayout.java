package com.jharter.game.layout;

import com.badlogic.ashley.core.Entity;
import com.jharter.game.ashley.components.Comp;
import com.jharter.game.ashley.components.Components.CardComp;
import com.jharter.game.ashley.components.Components.SpriteComp;
import com.jharter.game.util.U;
import com.jharter.game.util.id.ID;
import com.jharter.game.util.id.IDUtil;

public class HandLayout extends ZoneLayout {

	public HandLayout() {
		super();
	}

	@Override
	protected TweenTarget getTarget(ID id, int index, Entity entity, TweenTarget target) {
		SpriteComp s = Comp.SpriteComp.get(entity);
		s.relativePositionRules.relative = false;
		
		float anchorX = U.u12(-30);
		float anchorY = U.u12(-41);
		
		target.position.x = anchorX + (Math.round(s.scaledWidth()) + U.u12(1)) * index;
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
		CardComp c = Comp.CardComp.get(entity);
		if(c.playerID != IDUtil.getPlayerEntityID()) {
			hide(entity);
		} else {
			show(entity);
		}
	}

}
