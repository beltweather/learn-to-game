package com.jharter.game.tween;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.jharter.game.ashley.components.Components.AnimatingComp;
import com.jharter.game.ashley.components.Mapper;
import com.jharter.game.util.id.ID;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.TweenCallback;

public class TweenCallbacks {
	
	private TweenCallbacks() {}
	
	public static class FinishedAnimatingCallback implements TweenCallback, Poolable {
		
		private ID id;
		
		private FinishedAnimatingCallback() {}

		public void setID(ID id) {
			this.id = id;
		}
		
		@Override
		public void onEvent(int type, BaseTween<?> source) {
			Entity entity = Mapper.Entity.get(id);
			if(Mapper.AnimatingComp.has(entity)) {
				AnimatingComp a = Mapper.AnimatingComp.get(entity);
				a.activeCount--;
				if(a.activeCount == 0) {
					entity.remove(AnimatingComp.class);
				}
			}
			Pools.free(this);
		}

		@Override
		public void reset() {
			id = null;
		}
		
	}
	
}
