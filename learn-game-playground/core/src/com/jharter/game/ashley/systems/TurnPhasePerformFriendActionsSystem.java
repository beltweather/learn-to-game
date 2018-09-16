package com.jharter.game.ashley.systems;

import java.util.Comparator;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.utils.Array;
import com.jharter.game.ashley.components.Comp;
import com.jharter.game.ashley.components.Components.ActionQueuedComp;
import com.jharter.game.ashley.components.Components.AnimatingComp;
import com.jharter.game.ashley.components.Components.CleanupTurnActionComp;
import com.jharter.game.ashley.components.Components.TurnActionComp;
import com.jharter.game.ashley.components.Components.TurnPhasePerformEnemyActionsComp;
import com.jharter.game.ashley.components.Components.TurnPhasePerformFriendActionsComp;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.subcomponents.TurnAction;
import com.jharter.game.layout.TweenTarget;
import com.jharter.game.tween.TweenCallbacks;
import com.jharter.game.tween.TweenCallbacks.FinishedAnimatingCallback;
import com.jharter.game.tween.TweenUtil;
import com.jharter.game.util.U;
import com.jharter.game.util.id.ID;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.TweenCallback;
import uk.co.carelesslabs.Enums.ZoneType;

public class TurnPhasePerformFriendActionsSystem extends TurnPhaseSystem {

	private boolean busy = false;
	
	@SuppressWarnings("unchecked")
	public TurnPhasePerformFriendActionsSystem() {
		super(TurnPhasePerformFriendActionsComp.class, TurnPhasePerformEnemyActionsComp.class,
			  Family.all(TurnActionComp.class, ActionQueuedComp.class).get(), new QueueSort());
		endIfNoMoreEntities();
	}

	@Override
	protected boolean processEntityPhaseStart(Entity entity, float deltaTime) {
		busy = false;
		return Comp.Entity.DefaultTurn().TurnTimerComp().turnTimer.isStopped() && isDoneAnimating(); // XXX There's probably a better way to wait for animations
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
			final TurnAction turnAction = t.turnAction;
			ID id = Comp.IDComp.get(entity).id;
			ID ownerID = turnAction.ownerID;
			
			TweenTarget tt = TweenTarget.newInstance();
			tt.setFromEntity(entity);
			tt.angleDegrees = 3600;
			tt.alpha = 0f;
			tt.scale.x = 0f;
			tt.scale.y = 0f;
			tt.position.x = U.u12(160);
			tt.position.y = U.u12(60);
			
			TweenUtil.start(getEngine(), id, TweenUtil.tween(id, tt, 1f));
			
			tt = TweenTarget.newInstance();
			tt.setFromEntityID(ownerID);
			tt.position.x -= U.u12(10);
			tt.position.y += U.u12(4);
			tt.angleDegrees = 20;
				
			Timeline tweenA = TweenUtil.tween(ownerID, tt, 0.25f).setCallback(new TweenCallback() {

				@Override
				public void onEvent(int type, BaseTween<?> source) {
					if(turnAction != null) {
						turnAction.performAcceptCallback();
					}
					
					entity.add(Comp.create(getEngine(), CleanupTurnActionComp.class));
				}
				
			}); 
			
			tt.position.x += U.u12(10);
			tt.position.y -= U.u12(4);
			tt.angleDegrees = 0;
			Timeline tweenBa = TweenUtil.tween(ownerID, tt, 0.25f);
			
			Timeline tweenBb = Timeline.createParallel();
			Array<ID> allTargetIDs = turnAction.getAllTargetIDs();
			for(int i = 0; i < allTargetIDs.size; i++) {
				ID enemyID = allTargetIDs.get(i);
				Entity enemy = Comp.Entity.get(enemyID);
				ZoneComp z = Comp.Find.ZoneComp.findZone(enemy);
				if(z.zoneType != ZoneType.ENEMY) {
					continue;
				}
				
				TweenTarget enemyTT = TweenTarget.newInstance();
				enemyTT.setFromEntity(enemy);
				enemyTT.position.x -= U.u12(10);
				enemyTT.position.y += U.u12(1);
				enemyTT.angleDegrees += 20;
				
				AnimatingComp a = Comp.getOrAdd(getEngine(), AnimatingComp.class, enemy);
				a.activeCount++;
				FinishedAnimatingCallback enemyFAC = TweenCallbacks.newInstance(FinishedAnimatingCallback.class);
				enemyFAC.setID(enemyID);
				tweenBb.push(TweenUtil.tween(enemyID, enemyTT, 0.1f).setCallback(enemyFAC).repeatYoyo(1, 0f));
			}
			
			TweenUtil.start(getEngine(), ownerID, Timeline.createSequence().push(tweenA).beginParallel().push(tweenBa).push(tweenBb).end(), new TweenCallback() {

				@Override
				public void onEvent(int type, BaseTween<?> source) {
					busy = false;
				}
				
			});
			
			busy = true;
		} else {
			entity.add(Comp.create(getEngine(), CleanupTurnActionComp.class));
		}
		
		return false;
	}

	@Override
	protected void processEntityPhaseEnd(Entity entity, float deltaTime) {
		busy = false;
	}
	
	private static class QueueSort implements Comparator<Entity> {
		@Override
		public int compare(Entity entityA, Entity entityB) {
			return Comp.ActionQueuedComp.get(entityA).queueIndex - Comp.ActionQueuedComp.get(entityB).queueIndex;
		}
	}
	
}
