package com.jharter.game.layout;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector3;
import com.jharter.game.ashley.components.Comp;
import com.jharter.game.ashley.components.Components.CardComp;
import com.jharter.game.ashley.components.Components.MultiSpriteComp;
import com.jharter.game.ashley.components.Components.SpriteComp;
import com.jharter.game.ashley.components.Components.TurnActionComp;
import com.jharter.game.tween.TweenType;
import com.jharter.game.tween.TweenUtil;
import com.jharter.game.util.U;
import com.jharter.game.util.id.ID;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import uk.co.carelesslabs.Enums.Direction;

public class ActiveCardLayout extends ZoneLayout {

	public ActiveCardLayout() {
		super();
	}

	@Override
	protected TweenTarget getTarget(ID id, int index, Entity entity, TweenTarget target) {
		SpriteComp s = Comp.SpriteComp.get(entity);
		CardComp c = Comp.CardComp.get(entity);
		
		s.relativePositionRules.enabled = true;
		s.relativePositionRules.setRelativeToID(Comp.Method.CardComp.getBattleAvatarID(c));
		s.relativePositionRules.xAlign = Direction.WEST;
		s.relativePositionRules.yAlign = Direction.CENTER;
		s.relativePositionRules.offset.x = -U.u12(1);
		
		target.scale.y = 0.25f;
		target.scale.x = 0.25f;
		target.alpha = Comp.UntargetableComp.has(entity) ? 0.25f : 1f;
		
		return target;
	}

	private Vector3 tempPosition = new Vector3();
	protected void modifyEntity(ID id, int index, Entity entity, TweenTarget target) {
		TurnActionComp t = Comp.TurnActionComp.get(entity);
		if(t != null && t.turnAction != null && t.turnAction.multiplicity > 1) {
			MultiSpriteComp m = Comp.getOrAdd(getEngine(), MultiSpriteComp.class, entity);
			if(m.size == t.turnAction.multiplicity) {
				return;
			}
			m.clear();
			
			Timeline timeline = Timeline.createParallel();
			
			tempPosition.set(target.position);
			for(int i = m.positions.size; i < t.turnAction.multiplicity; i++) {
				Vector3 mPos = new Vector3(tempPosition);
				Vector3 targetPos = new Vector3(tempPosition.x - U.u12(1)*i, tempPosition.y, tempPosition.z);
				m.positions.add(mPos);
				timeline.push(Tween.to(mPos, TweenType.POSITION_XY.asInt(), 0.25f).target(targetPos.x, targetPos.y));
			}
			m.size = m.positions.size;
			
			TweenUtil.start(getEngine(), null, timeline);
			
		} else {
			Comp.remove(MultiSpriteComp.class, entity);
		}
	}
	
}
