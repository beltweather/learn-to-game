package com.jharter.game.layout;

import com.badlogic.ashley.core.Entity;
import com.jharter.game.ashley.components.Components.SpriteComp;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.Components.ZonePositionPointerComp;
import com.jharter.game.ashley.components.Mapper;
import com.jharter.game.util.id.ID;

import uk.co.carelesslabs.Enums.ZoneType;

public class FriendArrowLayout extends ZoneLayout {

	public FriendArrowLayout() {
		super();
	}

	@Override
	protected TweenTarget getTarget(ID id, int index, Entity entity, TweenTarget target) {
		float scale = 1f;
		SpriteComp s = Mapper.SpriteComp.get(entity);
		
		ZoneComp z = Mapper.ZoneComp.get(null, ZoneType.FRIEND);
		int targetIndex = Mapper.getActivePlayerIndex();
		if(!z.hasIndex(targetIndex)) {
			return null;
		}
		
		Entity targetEntity = Mapper.Entity.get(z.objectIDs.get(targetIndex));
		SpriteComp sTarget = Mapper.SpriteComp.get(targetEntity);
		
		target.position.x = (sTarget.position.x + (sTarget.scaledWidth() - s.scaledWidth(scale)) / 2);
		target.position.y = sTarget.position.y + sTarget.scaledHeight();
		target.position.z = s.position.z;
		target.scale.x = scale;
		target.scale.y = scale;
		
		if(Mapper.UntargetableComp.has(entity)) {
			target.alpha = 0.25f;
		} else {
			target.alpha = 1f;
		}
		
		return target;
	}

	protected void modifyEntity(ID id, int index, Entity entity, TweenTarget target) {
		if(Mapper.CursorEntity.isDisabled()) {
			hide(entity);
			return;
		}
		
		ZoneComp z = Mapper.ZoneComp.get(null, ZoneType.FRIEND);
		int targetIndex = Mapper.getActivePlayerIndex();
		
		if(!z.hasIndex(targetIndex)) {
			hide(entity);
		}
	}
	
}
