package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.jharter.game.ashley.components.Comp;
import com.jharter.game.ashley.components.Components.ActionQueuedComp;
import com.jharter.game.ashley.components.Components.ActionSpentComp;
import com.jharter.game.ashley.components.Components.IDComp;
import com.jharter.game.ashley.components.Components.SpriteComp;
import com.jharter.game.ashley.components.Components.TurnActionComp;
import com.jharter.game.ashley.components.Components.TurnPhasePerformEnemyActionsComp;
import com.jharter.game.ashley.components.Components.TurnPhasePerformFriendActionsComp;
import com.jharter.game.ashley.components.Ent;
import com.jharter.game.ashley.components.Link;
import com.jharter.game.ashley.components.subcomponents.TurnAction;
import com.jharter.game.layout.TweenTarget;
import com.jharter.game.tween.TweenUtil;
import com.jharter.game.util.U;
import com.jharter.game.util.id.ID;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.TweenCallback;

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
		return Ent.TurnEntity.TurnTimerComp().turnTimer.isStopped() && isDoneAnimating(); // XXX There's probably a better way to wait for animations
	}

	@Override
	protected boolean processEntityPhaseMiddle(final Entity entity, float deltaTime) {
		if(busy) {
			return false;
		}
		
		TurnActionComp t = Comp.TurnActionComp.get(entity);
		boolean performTurnAction = t != null && t.turnAction.priority == 0 && Comp.CardComp.has(entity);
		
		entity.remove(ActionQueuedComp.class);
		if(performTurnAction) {
			final TurnAction turnAction = performTurnAction ? t.turnAction : null;
			ID id = Comp.IDComp.get(entity).id;
			Entity player = Ent.Entity.get(Comp.CardComp.get(entity).playerID);
			Entity battleAvatar = Link.PlayerComp.getBattleAvatarEntity(Comp.PlayerComp.get(player));
			IDComp idAvatar = Comp.IDComp.get(battleAvatar);
			SpriteComp sAvatar = Comp.SpriteComp.get(battleAvatar);
			
			TweenTarget tt = TweenTarget.newInstance();
			tt.setFromEntity(entity);
			tt.angleDegrees = 3600;
			tt.alpha = 0f;
			tt.scale.x = 0f;
			tt.scale.y = 0f;
			tt.position.x = U.u12(160);
			tt.position.y = U.u12(60);
			
			TweenUtil.start(id, TweenUtil.tween(id, tt, 1f), new TweenCallback() {

				@Override
				public void onEvent(int type, BaseTween<?> source) {
					if(turnAction != null) {
						turnAction.performAcceptCallback();
					}
					
					entity.add(Comp.create(ActionSpentComp.class));

					busy = false;
				}
				
			});
			
			tt = TweenTarget.newInstance();
			tt.setFromEntity(battleAvatar);
			tt.position.x -= U.u12(10);
			tt.position.y += U.u12(4);
			tt.angleDegrees = 20;
			
			TweenUtil.start(idAvatar.id, TweenUtil.tween(idAvatar.id, tt, 0.25f).repeatYoyo(1, 0f));
			
			busy = true;
		} else {
			entity.add(Comp.create(ActionSpentComp.class));
		}
		
		return false;
	}

	@Override
	protected void processEntityPhaseEnd(Entity entity, float deltaTime) {
		busy = false;
	}
	
}
