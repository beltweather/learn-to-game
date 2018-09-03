package com.jharter.game.ashley.components.subcomponents;

import com.badlogic.ashley.core.Entity;
import com.jharter.game.ashley.components.Comp;
import com.jharter.game.ashley.components.Components.ActiveCardComp;
import com.jharter.game.ashley.components.Components.TurnActionComp;
import com.jharter.game.ashley.components.EntityBuilder;

public abstract class Callback<T, R> {

	public Callback() {}
	
	public abstract R call(T object);

	public static abstract class ValidTargetCallback extends Callback<Entity, Boolean> {
		
		public ValidTargetCallback(EntityBuilder b) {
			this(b.TurnActionComp().turnAction);
		}
		
		public ValidTargetCallback(TurnAction t) {
			t.validTargetCallback = this;
		}
		
	}
	
	public static class HasActiveCardCallback extends ValidTargetCallback {
		
		public HasActiveCardCallback(EntityBuilder b) {
			this(b.TurnActionComp().turnAction);
		}
		
		public HasActiveCardCallback(TurnAction t) {
			super(t);
		}

		@Override
		public Boolean call(Entity entity) {
			ActiveCardComp aFriend = Comp.ActiveCardComp.get(entity);
			return aFriend != null && aFriend.activeCardID != null;
		}
		
	}
	
	public static class DoesntHaveAllCallback extends ValidTargetCallback {
		
		public DoesntHaveAllCallback(EntityBuilder b) {
			this(b.TurnActionComp().turnAction);
		}
		
		public DoesntHaveAllCallback(TurnAction t) {
			super(t);
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
