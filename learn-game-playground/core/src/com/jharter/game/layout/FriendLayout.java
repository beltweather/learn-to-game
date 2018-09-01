package com.jharter.game.layout;

import com.badlogic.ashley.core.Entity;
import com.jharter.game.ashley.components.Components.SpriteComp;
import com.jharter.game.ashley.components.Mapper;
import com.jharter.game.util.Units;
import com.jharter.game.util.id.ID;

public class FriendLayout extends ZoneLayout {

	public FriendLayout() {
		super();
	}

	@Override
	protected TweenTarget getTarget(ID id, int index, Entity entity, TweenTarget target) {
		SpriteComp s = Mapper.SpriteComp.get(entity);
		
		float anchorX = Units.u12(60);
		float anchorY = Units.u12(6);
		target.position.x = anchorX - (index % 2 == 0 ? Units.u12(4) : 0);
		target.position.y = anchorY - (Units.u12(10)) * index;
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
		
	}

}
