package com.jharter.game.layout;

import com.badlogic.ashley.core.Entity;
import com.jharter.game.ashley.components.Components.CardComp;
import com.jharter.game.ashley.components.Components.SpriteComp;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.Mapper;
import com.jharter.game.util.id.ID;

public class ActiveCardLayout extends ZoneLayout {

	public ActiveCardLayout(ZoneComp z) {
		super(z);
	}

	@Override
	protected TweenTarget getTarget(ID id, int index, Entity entity, TweenTarget target) {
		float targetScale = 0.25f;
		SpriteComp s = Mapper.SpriteComp.get(entity);
		CardComp c = Mapper.CardComp.get(entity);
		
		Entity owner = Mapper.Entity.get(c.ownerID);
		SpriteComp sOwner = Mapper.SpriteComp.get(owner);
		
		target.position.x = (sOwner.position.x - s.scaledWidth(targetScale) - 20);
		target.position.y = (sOwner.position.y + (sOwner.scaledHeight() - s.scaledHeight(targetScale)) / 2);
		target.scale.x = targetScale;
		target.scale.y = targetScale;
		
		if(Mapper.UntargetableComp.has(entity)) {
			target.alpha = 0.25f;
		} else {
			target.alpha = 1f;
		}
		
		return target;
	}

}
