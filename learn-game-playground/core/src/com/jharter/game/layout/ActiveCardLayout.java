package com.jharter.game.layout;

import com.badlogic.ashley.core.Entity;
import com.jharter.game.ashley.components.Components.AlphaComp;
import com.jharter.game.ashley.components.Components.CardComp;
import com.jharter.game.ashley.components.Components.PositionComp;
import com.jharter.game.ashley.components.Components.SizeComp;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.Mapper;
import com.jharter.game.util.id.ID;

public class ActiveCardLayout extends ZoneLayout {

	public ActiveCardLayout(ZoneComp z) {
		super(z);
	}

	@Override
	protected LayoutTarget getTarget(ID id, int index, Entity entity, LayoutTarget target) {
		float targetScale = 0.25f;
		SizeComp s = Mapper.SizeComp.get(entity);
		CardComp c = Mapper.CardComp.get(entity);
		
		Entity owner = Mapper.Entity.get(c.ownerID);
		PositionComp pOwner = Mapper.PositionComp.get(owner);
		SizeComp sOwner = Mapper.SizeComp.get(owner);
		
		target.position.x = (pOwner.position.x - s.scaledWidth(targetScale) - 20);
		target.position.y = (pOwner.position.y + (sOwner.scaledHeight() - s.scaledHeight(targetScale)) / 2);
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
