package com.jharter.game.tween;

import com.badlogic.ashley.core.Entity;
import com.jharter.game.ashley.components.Components.AnimatingComp;
import com.jharter.game.ashley.components.Mapper;
import com.jharter.game.util.id.ID;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;

public class TweenCallbacks {
	
	private TweenCallbacks() {}
	
	public static class FinishedAnimatingCallback implements TweenCallback {
		
		@Override
		public void onEvent(int type, BaseTween<?> source) {
			if(source instanceof Tween) {
				Tween tween = (Tween) source;
				Object obj = tween.getTarget();
				if(obj instanceof ID) {
					Entity entity = Mapper.Entity.get((ID) obj);
					if(Mapper.AnimatingComp.has(entity)) {
						entity.remove(AnimatingComp.class);
					}
				}
			}
		}
		
	}
	
	public static class ZoneLayoutCallback implements TweenCallback {
		
		@Override
		public void onEvent(int type, BaseTween<?> source) {
			Timeline timeline = (Timeline) source;
			Tween tween = (Tween) timeline.getChildren().get(0);
			ID id = (ID) tween.getTarget();
			Entity entity = Mapper.Entity.get(id);
			if(Mapper.AnimatingComp.has(entity)) {
				entity.remove(AnimatingComp.class);
			}
		}
		
	}

}
