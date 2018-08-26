package com.jharter.game.layout;

import com.badlogic.ashley.core.Entity;
import com.jharter.game.ashley.components.Components.AlphaComp;
import com.jharter.game.ashley.components.Components.SizeComp;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.Mapper;
import com.jharter.game.util.id.ID;

public class HandLayout extends ZoneLayout {

	public HandLayout(ZoneComp z) {
		super(z);
	}

	@Override
	protected LayoutTarget getTarget(ID id, int index, Entity entity, LayoutTarget target) {
		SizeComp s = Mapper.SizeComp.get(entity);
		
		int anchorX = -700;
		int anchorY = -475;
		
		target.position.x = anchorX + (s.scaledWidth() + 20) * index;
		target.position.y = anchorY;
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

}
