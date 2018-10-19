package com.jharter.game.layout;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.jharter.game.ecs.components.Components.SpriteComp;
import com.jharter.game.ecs.components.Components.TurnActionComp;
import com.jharter.game.ecs.entities.IEntityHandler;
import com.jharter.game.tween.TweenType;
import com.jharter.game.util.U;
import com.jharter.game.util.id.ID;

import aurelienribon.tweenengine.Tween;

public class HandLayout extends ZoneLayout {

	private Array<Vector3> tweenPositions = new Array<Vector3>();

	public HandLayout(IEntityHandler handler) {
		super(handler);
	}

	@Override
	protected TweenTarget getTarget(ID id, int index, Entity entity, TweenTarget target) {
		if(tweenPositions.size <= index) {
			addTweenPosition(MathUtils.random(2f, 3f), 2f);//MathUtils.random(1f, 3f));
		}

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

		tween = s.position.x != target.position.x;
		if(!tween) {
			//target.position.y += tweenPositions.get(index).y;
			s.visualOffset.y = tweenPositions.get(index).y;
		}

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
		if(t.turnAction.mods.multiplicity <= 1) {
			Comp.MultiSpriteComp.remove(entity);
		}
	}

	public void addTweenPosition(float duration, float targetY) {
		Vector3 tweenPosition = new Vector3();
		getTweenManager().start(null, Tween.to(tweenPosition, TweenType.POSITION_Y.asInt(), duration).target(U.u12(targetY)).repeatYoyo(Tween.INFINITY, 0));
		tweenPositions.add(tweenPosition);
	}

}
