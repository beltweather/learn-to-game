package com.jharter.game.ecs.systems.turnphase;

import java.util.Comparator;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.utils.Array;
import com.jharter.game.ecs.components.Components.ActionQueuedComp;
import com.jharter.game.ecs.components.Components.AnimatingComp;
import com.jharter.game.ecs.components.Components.AssociatedTurnActionsComp;
import com.jharter.game.ecs.components.Components.CleanupTurnActionTag;
import com.jharter.game.ecs.components.Components.PendingVitalsComp;
import com.jharter.game.ecs.components.Components.TurnActionComp;
import com.jharter.game.ecs.components.Components.TurnPhaseEndTurnTag;
import com.jharter.game.ecs.components.Components.TurnPhasePerformActionsTag;
import com.jharter.game.ecs.components.Components.VitalsComp;
import com.jharter.game.ecs.components.Components.ZoneComp;
import com.jharter.game.ecs.components.Components.ZonePositionComp;
import com.jharter.game.ecs.components.subcomponents.TurnAction;
import com.jharter.game.layout.TweenTarget;
import com.jharter.game.tween.TweenCallbacks;
import com.jharter.game.tween.TweenCallbacks.FinishedAnimatingCallback;
import com.jharter.game.tween.machine.CombatMachine;
import com.jharter.game.util.U;
import com.jharter.game.util.id.ID;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.TweenCallback;
import uk.co.carelesslabs.Enums.ZoneType;

public class TurnPhasePerformActionsSystem extends TurnPhaseSystem {

	private boolean busy = false;
	
	public TurnPhasePerformActionsSystem() {
		super(TurnPhasePerformActionsTag.class, TurnPhaseEndTurnTag.class);
		add(TurnActionComp.class, Family.all(TurnActionComp.class, ActionQueuedComp.class).exclude(CleanupTurnActionTag.class).get(), new TimestampSort());
		add(AssociatedTurnActionsComp.class, Family.all(AssociatedTurnActionsComp.class, VitalsComp.class).get());
		add(PendingVitalsComp.class);
	}

	@Override
	protected boolean processEntityPhaseStart(Entity turnPhase, float deltaTime) {
		busy = false;
		clearComps(PendingVitalsComp.class);
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
		removePendingResults();
		busy = false;
	}
	
	protected void handleTurnAction(Entity turnActionEntity) {
		TurnActionComp t = Comp.TurnActionComp.get(turnActionEntity);
		boolean performTurnAction = t != null && 
				t.turnAction.priority == 0 && 
				Comp.CardComp.has(turnActionEntity);
		
		turnActionEntity.remove(ActionQueuedComp.class);
		if(performTurnAction) {
			performTurnAction(turnActionEntity);
		} else {
			resolve(turnActionEntity);
		}
	}
	
	protected void performTurnAction(Entity turnActionEntity) {
		ID turnActionID = Comp.IDComp.get(turnActionEntity).id;
		final TurnAction turnAction = Comp.TurnActionComp.get(turnActionEntity).turnAction;
		ID ownerID = turnAction.ownerID;
		boolean isFriend = Comp.FriendComp.has(ownerID); 
		
		CombatMachine machine = new CombatMachine(this);
		
		// Move the turn action itself
		machine
			.newTarget(turnActionID)
			.reflectX(!isFriend)
			.spinAndDissapear()
			.start();
		
		// Start moving the owner in a series of events
		Timeline tweenA = machine
			.newTarget(ownerID)
			.reflectX(!isFriend)
			.reflectAngle(!isFriend)
			.rockForward()
			.getTimeline()
			.setCallback(new TweenCallback() {

				@Override
				public void onEvent(int type, BaseTween<?> source) {
					if(turnAction != null) {
						turnAction.performAcceptCallback();
					}
					
					resolve(turnActionEntity);
				}
			
			}); 

		Timeline tweenBa = machine
			.rockBackward()
			.getTimeline();
		
		Timeline tweenBb = Timeline.createParallel();
		
		Array<ID> allTargetIDs = turnAction.getAllTargetIDs();
		for(int i = 0; i < allTargetIDs.size; i++) {
			Timeline targetTween = getTargetTween(machine, allTargetIDs.get(i), isFriend);
			if(targetTween != null) {
				tweenBb.push(targetTween);
			}
		}
		
		Timeline turnPhaseSequence = Timeline.createSequence()
			.push(tweenA)
			.beginParallel()
				.push(tweenBa)
				.push(tweenBb)
			.end();
		
		getTweenManager().start(ownerID, turnPhaseSequence, new TweenCallback() {

			@Override
			public void onEvent(int type, BaseTween<?> source) {
				busy = false;
			}
			
		});
		
		busy = true;
	}
	
	private Timeline getTargetTween(CombatMachine machine, ID enemyID, boolean isFriend) {
		ZoneComp z = Comp.ZoneComp.get(Comp.ZonePositionComp.get(enemyID).zoneID);
		
		if((isFriend && z.zoneType != ZoneType.ENEMY) ||
		   (!isFriend && z.zoneType != ZoneType.FRIEND)) {
			return null;
		}
		
		return machine
			.newTarget(enemyID)
			.reflectX(!isFriend)
			.reflectAngle(!isFriend)
			.rockForward()
			.setDuration(0.1f)
			.getTimeline(true)
			.repeatYoyo(1, 0f);
	}
	
	private void removePendingResults() {
		for(Entity entity : entities(AssociatedTurnActionsComp.class)) {
			AssociatedTurnActionsComp a = Comp.AssociatedTurnActionsComp.get(entity);
			a.cursorIDs.clear();
			a.targetIndices.clear();
			a.turnActionIDs.clear();
			Comp.PendingVitalsComp.remove(entity);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	protected void performTurnActionOrig(Entity turnActionEntity, TurnActionComp t) {
		final TurnAction turnAction = t.turnAction;
		ID id = Comp.IDComp.get(turnActionEntity).id;
		ID ownerID = turnAction.ownerID;
		boolean isFriend = Comp.FriendComp.has(ownerID); 
		
		TweenTarget tt = TweenTarget.newInstance();
		tt.setFromEntity(this, turnActionEntity);
		tt.angleDegrees = 3600;
		tt.alpha = 0f;
		tt.scale.x = 0f;
		tt.scale.y = 0f;
		tt.position.x = U.u12(160);
		tt.position.y = U.u12(60);
		
		getTweenManager().start(id, tt, 1f);
		
		tt = TweenTarget.newInstance();
		tt.setFromEntityID(this, ownerID);
		
		if(isFriend) {
			tt.angleDegrees = 20;
			tt.position.x -= U.u12(10);
		} else {
			tt.angleDegrees = -20;
			tt.position.x += U.u12(10);
		}
		tt.position.y += U.u12(4);
			
		Timeline tweenA = getTweenManager().build(ownerID, tt, 0.25f).setCallback(new TweenCallback() {

			@Override
			public void onEvent(int type, BaseTween<?> source) {
				if(turnAction != null) {
					turnAction.performAcceptCallback();
				}
				
				resolve(turnActionEntity);
			}
			
		}); 
		
		if(isFriend) {
			tt.position.x += U.u12(10);
		} else {
			tt.position.x -= U.u12(10);
		}
		tt.position.y -= U.u12(4);
		tt.angleDegrees = 0;
		Timeline tweenBa = getTweenManager().build(ownerID, tt, 0.25f);
		
		
		Timeline tweenBb = Timeline.createParallel();
		Array<ID> allTargetIDs = turnAction.getAllTargetIDs();
		for(int i = 0; i < allTargetIDs.size; i++) {
			ID enemyID = allTargetIDs.get(i);
			Entity enemy = Comp.Entity.get(enemyID);
			ZonePositionComp zp = Comp.ZonePositionComp.get(enemy);
			ZoneComp z = Comp.ZoneComp.get(zp.zoneID);
			
			if((isFriend && z.zoneType != ZoneType.ENEMY) ||
			   (!isFriend && z.zoneType != ZoneType.FRIEND)) {
				continue;
			}
			
			TweenTarget enemyTT = TweenTarget.newInstance();
			enemyTT.setFromEntity(this, enemy);
			if(isFriend) {
				enemyTT.position.x -= U.u12(10);
				enemyTT.angleDegrees += 20;
			} else {
				enemyTT.position.x += U.u12(10);
				enemyTT.angleDegrees -= 20;
			}
			enemyTT.position.y += U.u12(1);
			
			AnimatingComp a = Comp.AnimatingComp.getOrAdd(enemy);
			a.activeCount++;
			FinishedAnimatingCallback enemyFAC = TweenCallbacks.newInstance(this, FinishedAnimatingCallback.class);
			enemyFAC.setID(enemyID);
			tweenBb.push(getTweenManager().build(enemyID, enemyTT, 0.1f).setCallback(enemyFAC).repeatYoyo(1, 0f));
		}
		
		Timeline turnPhaseSequence = Timeline.createSequence().push(tweenA).beginParallel().push(tweenBa).push(tweenBb).end();
		getTweenManager().start(ownerID, turnPhaseSequence, new TweenCallback() {

			@Override
			public void onEvent(int type, BaseTween<?> source) {
				busy = false;
			}
			
		});
		
		busy = true;
	}
	
	protected void resolve(Entity turnActionEntity) {
		Comp.CleanupTurnActionTag.add(turnActionEntity);
		if(Comp.CardComp.has(turnActionEntity)) {
			Comp.DiscardCardTag.add(turnActionEntity);	
		}
	}
	
	protected Entity getNextTurnActionEntity() {
		return entitySorted(TurnActionComp.class);
	}
	
	private class TimestampSort implements Comparator<Entity> {
	
		@Override
		public int compare(Entity entityA, Entity entityB) {
			long timeA = Comp.ActionQueuedComp.get(entityA).timestamp;
			long timeB = Comp.ActionQueuedComp.get(entityB).timestamp;
			if(timeA == timeB) {
				return 0;
			}
			if(timeA < timeB) {
				return -1;
			}
			return 1;
		}
	
	}
	
}
