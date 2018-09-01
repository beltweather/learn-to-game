package com.jharter.game.layout;

import com.badlogic.ashley.core.Entity;
import com.jharter.game.ashley.components.Components.CardComp;
import com.jharter.game.ashley.components.Components.SpriteComp;
import com.jharter.game.ashley.components.Mapper;
import com.jharter.game.util.Units;
import com.jharter.game.util.id.ID;

public class HandLayout extends ZoneLayout {

	public HandLayout() {
		super();
	}

	@Override
	protected TweenTarget getTarget(ID id, int index, Entity entity, TweenTarget target) {
		SpriteComp s = Mapper.SpriteComp.get(entity);
		
		float anchorX = Units.u12(-30);
		float anchorY = Units.u12(-41);
		
		target.position.x = anchorX + (Math.round(s.scaledWidth()) + Units.u12(1)) * index;
		target.position.y = anchorY;
		target.position.z = s.position.z;
		target.scale.x = 1f;
		target.scale.y = 1f;
		target.angleDegrees = 0;
		
		if(Mapper.UntargetableComp.has(entity)) {
			target.alpha = 0.25f;
		} else {
			target.alpha = 1f;
		}
		
		return target;
	}
	
	@Override
	protected void modifyEntity(ID id, int index, Entity entity, TweenTarget target) {
		CardComp c = Mapper.CardComp.get(entity);
		if(c.playerID != Mapper.getPlayerEntityID()) {
			hide(entity);
		} else {
			show(entity);
		}
	}

}
