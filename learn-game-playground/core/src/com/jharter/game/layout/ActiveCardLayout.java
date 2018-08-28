package com.jharter.game.layout;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector3;
import com.jharter.game.ashley.components.Components.CardComp;
import com.jharter.game.ashley.components.Components.MultiPositionComp;
import com.jharter.game.ashley.components.Components.SpriteComp;
import com.jharter.game.ashley.components.Components.TurnActionComp;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.Mapper;
import com.jharter.game.tween.TweenType;
import com.jharter.game.tween.TweenUtil;
import com.jharter.game.util.id.ID;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;

public class ActiveCardLayout extends ZoneLayout {

	public ActiveCardLayout() {
		super();
	}

	@Override
	protected TweenTarget getTarget(ID id, int index, Entity entity, TweenTarget target) {
		float targetScale = 0.25f;
		SpriteComp s = Mapper.SpriteComp.get(entity);
		CardComp c = Mapper.CardComp.get(entity);
		
		Entity character = c.getCharacterEntity();
		SpriteComp sCharacter = Mapper.SpriteComp.get(character);
		
		target.position.x = (sCharacter.position.x - s.scaledWidth(targetScale) - 20);
		target.position.y = (sCharacter.position.y + (sCharacter.scaledHeight() - s.scaledHeight(targetScale)) / 2);
		target.scale.x = targetScale;
		target.scale.y = targetScale;
		
		if(Mapper.UntargetableComp.has(entity)) {
			target.alpha = 0.25f;
		} else {
			target.alpha = 1f;
		}
		
		return target;
	}

	private Vector3 tempPosition = new Vector3();
	protected void modifyEntity(ID id, int index, Entity entity, TweenTarget target) {
		super.modifyEntity(id, index, entity, target);
		
		TurnActionComp t = Mapper.TurnActionComp.get(entity);
		if(t != null && t.turnAction != null && t.turnAction.multiplicity > 1) {
			MultiPositionComp m;
			if(Mapper.MultiPositionComp.has(entity)) {
				m = Mapper.MultiPositionComp.get(entity);
			} else {
				m = Mapper.Comp.get(MultiPositionComp.class);
				entity.add(m);
			}
			
			if(m.positions.size == t.turnAction.multiplicity) {
				return;
			}
			//m.positions.clear();
			
			Timeline timeline = Timeline.createParallel();
			
			tempPosition.set(target.position);
			for(int i = m.positions.size; i < t.turnAction.multiplicity; i++) {
				Vector3 mPos = new Vector3(tempPosition);
				Vector3 targetPos = new Vector3(tempPosition.x - 20*i, tempPosition.y, tempPosition.z);
				m.positions.add(mPos);
				timeline.push(Tween.to(mPos, TweenType.POSITION_XY.asInt(), 0.25f).target(targetPos.x, targetPos.y));
			}
			
			TweenUtil.start(null, timeline);
			
		} //else if(Mapper.MultiPositionComp.has(entity)) {
			//entity.remove(MultiPositionComp.class);
		//}
	}
	
}
