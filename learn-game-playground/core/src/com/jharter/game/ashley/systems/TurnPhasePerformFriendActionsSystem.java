package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.jharter.game.ashley.components.Components.ActionQueuedComp;
import com.jharter.game.ashley.components.Components.ActionSpentComp;
import com.jharter.game.ashley.components.Components.MultiPositionComp;
import com.jharter.game.ashley.components.Components.TurnActionComp;
import com.jharter.game.ashley.components.Components.TurnPhasePerformEnemyActionsComp;
import com.jharter.game.ashley.components.Components.TurnPhasePerformFriendActionsComp;
import com.jharter.game.ashley.components.Mapper;
import com.jharter.game.tween.TweenType;
import com.jharter.game.tween.TweenUtil;
import com.jharter.game.util.id.ID;

import aurelienribon.tweenengine.Tween;

public class TurnPhasePerformFriendActionsSystem extends TurnPhaseSystem {

	@SuppressWarnings("unchecked")
	public TurnPhasePerformFriendActionsSystem() {
		super(TurnPhasePerformFriendActionsComp.class, TurnPhasePerformEnemyActionsComp.class,
			  Family.all(ActionQueuedComp.class, TurnActionComp.class).get());
		endIfNoMoreEntities();
	}

	@Override
	protected boolean processEntityPhaseStart(Entity entity, float deltaTime) {
		return Mapper.TurnEntity.TurnTimerComp().isStopped() && isDoneAnimating(); // XXX There's probably a better way to wait for animations
	}

	@Override
	protected boolean processEntityPhaseMiddle(Entity entity, float deltaTime) {
		
		TurnActionComp t = Mapper.TurnActionComp.get(entity);
		if(t != null && t.turnAction.priority == 0) { // XXX This priority system needs looking into
			t.turnAction.performAcceptCallback();
		}
		
		if(Mapper.CardComp.has(entity)) {
			ID id = Mapper.IDComp.get(entity).id;
			TweenUtil.start(id, Tween.to(id, TweenType.ANGLE.asInt(), 1f).target(720));
		}
		
		entity.remove(ActionQueuedComp.class);
		entity.add(Mapper.Comp.get(ActionSpentComp.class));
		
		return false;
	}

	@Override
	protected void processEntityPhaseEnd(Entity entity, float deltaTime) {
		
	}
	
}
