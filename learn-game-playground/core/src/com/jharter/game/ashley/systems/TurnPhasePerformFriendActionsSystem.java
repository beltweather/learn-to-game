package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.utils.Pools;
import com.jharter.game.ashley.components.Components.ActionQueuedComp;
import com.jharter.game.ashley.components.Components.ActionSpentComp;
import com.jharter.game.ashley.components.Components.IDComp;
import com.jharter.game.ashley.components.Components.SpriteComp;
import com.jharter.game.ashley.components.Components.TurnActionComp;
import com.jharter.game.ashley.components.Components.TurnPhasePerformEnemyActionsComp;
import com.jharter.game.ashley.components.Components.TurnPhasePerformFriendActionsComp;
import com.jharter.game.ashley.components.Mapper;
import com.jharter.game.ashley.components.subcomponents.TurnAction;
import com.jharter.game.layout.TweenTarget;
import com.jharter.game.tween.TweenType;
import com.jharter.game.tween.TweenUtil;
import com.jharter.game.util.Units;
import com.jharter.game.util.id.ID;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Circ;

public class TurnPhasePerformFriendActionsSystem extends TurnPhaseSystem {

	private boolean busy = false;
	
	@SuppressWarnings("unchecked")
	public TurnPhasePerformFriendActionsSystem() {
		super(TurnPhasePerformFriendActionsComp.class, TurnPhasePerformEnemyActionsComp.class,
			  Family.all(ActionQueuedComp.class, TurnActionComp.class).get());
		endIfNoMoreEntities();
	}

	@Override
	protected boolean processEntityPhaseStart(Entity entity, float deltaTime) {
		busy = false;
		return Mapper.TurnEntity.TurnTimerComp().isStopped() && isDoneAnimating(); // XXX There's probably a better way to wait for animations
	}

	@Override
	protected boolean processEntityPhaseMiddle(final Entity entity, float deltaTime) {
		if(busy) {
			return false;
		}
		
		TurnActionComp t = Mapper.TurnActionComp.get(entity);
		boolean performTurnAction = t != null && t.turnAction.priority == 0 && Mapper.CardComp.has(entity);
		
		entity.remove(ActionQueuedComp.class);
		if(performTurnAction) {
			final TurnAction turnAction = performTurnAction ? t.turnAction : null;
			ID id = Mapper.IDComp.get(entity).id;
			Entity player = Mapper.Entity.get(Mapper.CardComp.get(entity).playerID);
			Entity battleAvatar = Mapper.PlayerComp.get(player).getBattleAvatarEntity();
			IDComp idAvatar = Mapper.IDComp.get(battleAvatar);
			SpriteComp sAvatar = Mapper.SpriteComp.get(battleAvatar);
			
			TweenTarget tt = Pools.get(TweenTarget.class).obtain();
			tt.setFromEntity(entity);
			tt.angleDegrees = 3600;
			tt.alpha = 0f;
			tt.scale.x = 0f;
			tt.scale.y = 0f;
			tt.position.x = Units.u12(160);
			tt.position.y = Units.u12(60);
			
			TweenUtil.start(id, TweenUtil.tween(id, tt, 1f), new TweenCallback() {

				@Override
				public void onEvent(int type, BaseTween<?> source) {
					if(turnAction != null) {
						turnAction.performAcceptCallback();
					}
					
					entity.add(Mapper.Comp.get(ActionSpentComp.class));

					busy = false;
				}
				
			});
			
			tt = Pools.get(TweenTarget.class).obtain();
			tt.setFromEntity(battleAvatar);
			tt.position.x -= Units.u12(10);
			tt.position.y += Units.u12(4);
			tt.angleDegrees = 20;
			
			TweenUtil.start(idAvatar.id, TweenUtil.tween(idAvatar.id, tt, 0.25f).repeatYoyo(1, 0f));
			
			//TweenUtil.start(idAvatar.id, Tween.to(sAvatar.position, TweenType.POSITION_X.asInt(), 0.25f).target(sAvatar.position.x - Units.u12(10)));
			
			busy = true;
		} else {
			entity.add(Mapper.Comp.get(ActionSpentComp.class));
		}
		
		return false;
	}

	@Override
	protected void processEntityPhaseEnd(Entity entity, float deltaTime) {
		busy = false;
	}
	
}
