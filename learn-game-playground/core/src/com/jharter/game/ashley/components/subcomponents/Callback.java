package com.jharter.game.ashley.components.subcomponents;

import com.badlogic.ashley.core.Entity;
import com.jharter.game.ashley.components.Components.ActiveTurnActionComp;
import com.jharter.game.ashley.components.Components.TurnActionComp;
import com.jharter.game.ashley.components.EntityBuilder;
import com.jharter.game.ashley.entities.EntityFactory;
import com.jharter.game.ashley.entities.IEntityFactory;

public abstract class Callback<T, R> extends EntityFactory {

	public Callback(IEntityFactory factory) {
		super(factory);
	}
	
	public abstract R call(T object);

	public static abstract class ValidTargetCallback extends Callback<Entity, Boolean> {
		
		public ValidTargetCallback(IEntityFactory factory, EntityBuilder b) {
			this(factory, b.TurnActionComp().turnAction);
		}
		
		public ValidTargetCallback(IEntityFactory factory, TurnAction t) {
			super(factory);
			t.validTargetCallback = this;
		}
		
	}
	
	public static class HasActiveCardCallback extends ValidTargetCallback {
		
		public HasActiveCardCallback(IEntityFactory factory, EntityBuilder b) {
			this(factory, b.TurnActionComp().turnAction);
		}
		
		public HasActiveCardCallback(IEntityFactory factory, TurnAction t) {
			super(factory, t);
		}

		@Override
		public Boolean call(Entity entity) {
			ActiveTurnActionComp aFriend = Comp.ActiveTurnActionComp.get(entity);
			return aFriend != null && aFriend.activeTurnActionID != null;
		}
		
	}
	
	public static class DoesntHaveAllCallback extends ValidTargetCallback {
		
		public DoesntHaveAllCallback(IEntityFactory factory, EntityBuilder b) {
			this(factory, b.TurnActionComp().turnAction);
		}
		
		public DoesntHaveAllCallback(IEntityFactory factory, TurnAction t) {
			super(factory, t);
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
