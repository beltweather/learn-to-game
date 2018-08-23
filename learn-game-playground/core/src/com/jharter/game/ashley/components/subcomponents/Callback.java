package com.jharter.game.ashley.components.subcomponents;

import com.badlogic.ashley.core.Entity;
import com.jharter.game.ashley.components.Components.ActiveCardComp;
import com.jharter.game.ashley.components.Components.TurnActionComp;
import com.jharter.game.ashley.components.EntityBuilder;
import com.jharter.game.ashley.components.Mapper;

public abstract class Callback<T, R> {

	public Callback() {}
	
	public abstract R call(T object);

	public static abstract class ValidTargetCallback extends Callback<Entity, Boolean> {
		
		public ValidTargetCallback(EntityBuilder b) {
			this(b.TurnActionComp());
		}
		
		public ValidTargetCallback(TurnActionComp t) {
			t.validTargetCallback = this;
		}
		
	}
	
	public static class HasActiveCardCallback extends ValidTargetCallback {
		
		public HasActiveCardCallback(EntityBuilder b) {
			this(b.TurnActionComp());
		}
		
		public HasActiveCardCallback(TurnActionComp t) {
			super(t);
		}

		@Override
		public Boolean call(Entity entity) {
			ActiveCardComp aFriend = Mapper.ActiveCardComp.get(entity);
			return aFriend != null && aFriend.activeCardID != null;
		}
		
	}
	
}
