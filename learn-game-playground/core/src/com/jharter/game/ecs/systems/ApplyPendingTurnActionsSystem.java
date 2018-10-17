package com.jharter.game.ecs.systems;

import java.util.Comparator;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.jharter.game.ecs.components.Components.PendingTurnActionModsComp;
import com.jharter.game.ecs.components.Components.PendingTurnActionTag;
import com.jharter.game.ecs.components.Components.PendingVitalsComp;
import com.jharter.game.ecs.components.Components.TurnActionComp;
import com.jharter.game.ecs.components.Components.TurnActionQueueItemComp;
import com.jharter.game.ecs.components.Components.VitalsComp;
import com.jharter.game.ecs.components.subcomponents.TurnAction;
import com.jharter.game.ecs.systems.boilerplate.GameSortedIteratingSystem;

public class ApplyPendingTurnActionsSystem extends GameSortedIteratingSystem {

	public ApplyPendingTurnActionsSystem() {
		super(Family.all(PendingTurnActionTag.class, TurnActionComp.class).get());
		setComparator(new PriorityAndTimingSort());
		add(PendingVitalsComp.class, Family.all(PendingVitalsComp.class, VitalsComp.class).get());
		add(PendingTurnActionModsComp.class, Family.all(PendingTurnActionModsComp.class, TurnActionComp.class).get());
	}

	@Override
	public void beforeUpdate(float deltaTime) {
		forceSort();
		clearComps(PendingVitalsComp.class);
		clearComps(PendingTurnActionModsComp.class);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		Comp.TurnActionComp.get(entity).turnAction.perform(true);
	}

	private class PriorityAndTimingSort implements Comparator<Entity> {

		@Override
		public int compare(Entity entityA, Entity entityB) {
			TurnAction tA = Comp.TurnActionComp.get(entityA).turnAction;
			TurnAction tB = Comp.TurnActionComp.get(entityB).turnAction;
			TurnActionQueueItemComp qA = Comp.TurnActionQueueItemComp.get(entityA);
			TurnActionQueueItemComp qB = Comp.TurnActionQueueItemComp.get(entityB);

			if(tA.priority != tB.priority) {
				if(tA.priority > tB.priority) {
					return -1;
				}
				return 1;
			}

			if(qA == null && qB == null) {
				return 0;
			}

			if(qA == null && qB != null) {
				return 1;
			}

			if(qA != null && qB == null) {
				return -1;
			}

			return 0;
		}

	}
}
