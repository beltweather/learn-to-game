package com.jharter.game.ashley.systems.turnphase;

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
import com.jharter.game.ashley.components.Components.ZonePositionComp;
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
		super(TurnPhasePerformFriendActionsComp.class, TurnPhasePerformEnemyActionsComp.class);
		add(TurnActionComp.class, Family.all(TurnActionComp.class, ActionQueuedComp.class).get(), new QueueSort());
	}

	@Override
	protected boolean processEntityPhaseStart(Entity turnPhase, float deltaTime) {
		busy = false;
		return getTurnTimer().isStopped() && isDoneAnimating(); // XXX There's probably a better way to wait for animations
	}

	@Override
	protected boolean processEntityPhaseMiddle(final Entity turnPhase, float deltaTime) {
		if(busy) {
			return false;
		}
		
		// Just grab the next turn action and process it if we're not busy
		Entity turnActionEntity = getNextTurnActionEntity();
		if(turnActionEntity != null) {
			handleTurnAction(turnActionEntity);
			return false;
		}
		
		return true;
	}

	@Override
	protected void processEntityPhaseEnd(Entity turnPhase, float deltaTime) {
		busy = false;
	}
	
	protected void handleTurnAction(Entity turnActionEntity) {
		TurnActionComp t = Comp.TurnActionComp.get(turnActionEntity);
		boolean performTurnAction = t != null && t.turnAction.priority == 0 && Comp.CardComp.has(turnActionEntity);
		
		turnActionEntity.remove(ActionQueuedComp.class);
		if(performTurnAction) {
			final TurnAction turnAction = t.turnAction;
			ID id = Comp.IDComp.get(turnActionEntity).id;
			ID ownerID = turnAction.ownerID;
			
			TweenTarget tt = TweenTarget.newInstance();
			tt.setFromEntity(turnActionEntity);
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
					
					turnActionEntity.add(Comp.create(getEngine(), CleanupTurnActionComp.class));
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
				ZonePositionComp zp = Comp.ZonePositionComp.get(enemy);
				ZoneComp z = Comp.ZoneComp.get(zp.zoneID);
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
			turnActionEntity.add(Comp.create(getEngine(), CleanupTurnActionComp.class));
		}
	}
	
	protected Entity getNextTurnActionEntity() {
		return getFirstEntity(TurnActionComp.class);
	}
	
	private static class QueueSort implements Comparator<Entity> {
		@Override
		public int compare(Entity entityA, Entity entityB) {
			return Comp.ActionQueuedComp.get(entityA).queueIndex - Comp.ActionQueuedComp.get(entityB).queueIndex;
		}
	}
	
}
