package com.jharter.game.ashley.components.subcomponents;

import com.badlogic.ashley.core.Entity;
import com.jharter.game.ashley.components.Components.ActiveTurnActionComp;
import com.jharter.game.ashley.components.Components.TurnActionComp;
import com.jharter.game.ashley.entities.EntityBuilder;
import com.jharter.game.ashley.entities.EntityHandler;
import com.jharter.game.ashley.entities.IEntityHandler;

public abstract class Callback<T, R> extends EntityHandler {

	public Callback(IEntityHandler handler) {
		super(handler);
	}
	
	public abstract R call(T object);

	public static abstract class ValidTargetCallback extends Callback<Entity, Boolean> {
		
		public ValidTargetCallback(IEntityHandler handler, EntityBuilder b) {
			this(handler, b.TurnActionComp().turnAction);
		}
		
		public ValidTargetCallback(IEntityHandler handler, TurnAction t) {
			super(handler);
			t.validTargetCallback = this;
		}
		
	}
	
	public static class HasActiveCardCallback extends ValidTargetCallback {
		
		public HasActiveCardCallback(IEntityHandler handler, EntityBuilder b) {
			this(handler, b.TurnActionComp().turnAction);
		}
		
		public HasActiveCardCallback(IEntityHandler handler, TurnAction t) {
			super(handler, t);
		}

		@Override
		public Boolean call(Entity entity) {
			ActiveTurnActionComp aFriend = Comp.ActiveTurnActionComp.get(entity);
			return aFriend != null && aFriend.activeTurnActionID != null;
		}
		
	}
	
	public static class DoesntHaveAllCallback extends ValidTargetCallback {
		
		public DoesntHaveAllCallback(IEntityHandler handler, EntityBuilder b) {
			this(handler, b.TurnActionComp().turnAction);
		}
		
		public DoesntHaveAllCallback(IEntityHandler handler, TurnAction t) {
			super(handler, t);
		}

		@Override
		public Boolean call(Entity entity) {
			TurnActionComp t = Comp.TurnActionComp.get(entity);
			if(t == null || t.turnAction == null) {
				return true;
			}
			return !t.turnAction.all;
		}
	}
	
}
