package com.jharter.game.ecs.components.subcomponents;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;
import com.jharter.game.ecs.components.Components.ActiveTurnActionComp;
import com.jharter.game.ecs.components.Components.CardComp;
import com.jharter.game.ecs.components.Components.TurnActionComp;
import com.jharter.game.ecs.components.Components.ZonePositionComp;
import com.jharter.game.ecs.entities.EntityHandler;
import com.jharter.game.ecs.entities.IEntityHandler;

import uk.co.carelesslabs.Enums.ZoneType;

public abstract class TargetValidator extends EntityHandler {

	public static TargetValidator combine(IEntityHandler handler, TargetValidator...validators) {
		if(validators.length == 0) {
			return null;
		}
		if(validators.length == 1) {
			return validators[0];
		}
		CombinedValidator c = new CombinedValidator(handler);
		c.validators.addAll(validators);
		return c;
	}

	public TargetValidator(IEntityHandler handler) {
		super(handler);
	}

	public abstract boolean call(Entity entity);

	public static class CombinedValidator extends TargetValidator {

		protected Array<TargetValidator> validators = new Array<TargetValidator>();

		public CombinedValidator(IEntityHandler handler) {
			super(handler);
		}

		@Override
		public boolean call(Entity entity) {
			for(TargetValidator v : validators) {
				if(!v.call(entity)) {
					return false;
				}
			}
			return true;
		}

	}

	public static class HasActiveCardValidator extends TargetValidator {

		public HasActiveCardValidator(IEntityHandler handler) {
			super(handler);
		}

		@Override
		public boolean call(Entity entity) {
			ActiveTurnActionComp aFriend = Comp.ActiveTurnActionComp.get(entity);
			return aFriend != null && aFriend.activeTurnActionID != null;
		}

	}

	public static class DoesntHaveAllValidator extends TargetValidator {

		public DoesntHaveAllValidator(IEntityHandler handler) {
			super(handler);
		}

		@Override
		public boolean call(Entity entity) {
			TurnActionComp t = Comp.TurnActionComp.get(entity);
			if(t == null || t.turnAction == null) {
				return true;
			}
			return !t.turnAction.mods.all;
		}
	}

	public static class DoesntTargetFriendCardValidator extends TargetValidator {

		public DoesntTargetFriendCardValidator(IEntityHandler handler) {
			super(handler);
		}

		@Override
		public boolean call(Entity entity) {
			CardComp c = Comp.CardComp.get(entity);
			ZonePositionComp zp = Comp.ZonePositionComp.get(entity);
			TurnActionComp t = Comp.TurnActionComp.get(entity);
			return c == null ||
				   zp == null ||
				   t == null ||
				   Comp.ZoneComp.get(zp.zoneID).zoneType != ZoneType.FRIEND_ACTIVE_CARD ||
				   !t.turnAction.targetZoneTypes.contains(ZoneType.FRIEND_ACTIVE_CARD, true);
		}

	}

}
