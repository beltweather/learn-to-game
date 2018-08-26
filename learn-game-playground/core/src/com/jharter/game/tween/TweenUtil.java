package com.jharter.game.tween;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.jharter.game.ashley.components.Components.AnimatingComp;
import com.jharter.game.ashley.components.Mapper;
import com.jharter.game.layout.LayoutTarget;
import com.jharter.game.tween.TweenCallbacks.FinishedAnimatingCallback;
import com.jharter.game.tween.TweenCallbacks.ZoneLayoutCallback;
import com.jharter.game.util.id.ID;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;

public class TweenUtil {
	private TweenUtil() {}
		
	private static TweenManager manager;
	private static TweenCallback finishedAnimatingCallback = new FinishedAnimatingCallback();
	private static TweenCallback zoneLayoutCallback = new ZoneLayoutCallback();
	
	public static void init() {
		manager = new TweenManager();
		//Tween.setCombinedAttributesLimit(4);
		Tween.registerAccessor(ID.class, new IDTweenAccessor());
		Tween.registerAccessor(Entity.class, new EntityTweenAccessor());
		Tween.registerAccessor(Vector3.class, new Vector3TweenAccessor());
		Tween.registerAccessor(Vector2.class, new Vector2TweenAccessor());
		Tween.registerAccessor(Float.class, new FloatTweenAccessor());
	}
	
	public static void update(float deltaTime) {
		if(manager != null) {
			manager.update(deltaTime);
		}
	}
	
	public static void start(BaseTween<?> baseTween) {
		if(manager == null) {
			return;
		}
		
		if(baseTween instanceof Timeline) {
			Timeline timeline = (Timeline) baseTween;
			timeline.start(manager);
		
		} else if(baseTween instanceof Tween) {
			Tween tween = (Tween) baseTween;
			Object target = tween.getTarget();
			if(!(target instanceof ID)) {
				tween.start(manager);
				return;
			}
			
			Entity entity = Mapper.Entity.get((ID) target);
			if(!Mapper.AnimatingComp.has(entity)) {
				entity.add(Mapper.Comp.get(AnimatingComp.class));
			}
			
			tween.setCallback(finishedAnimatingCallback).start(manager);
		}
	}
	
	public static void tween(ID id, LayoutTarget target) {
		Entity entity = Mapper.Entity.get((ID) id);
		if(!Mapper.AnimatingComp.has(entity)) {
			entity.add(Mapper.Comp.get(AnimatingComp.class));
		}
		
		float d = 1f;
		start(Timeline.createParallel()
			.push(Tween.to(id, TweenType.POSITION_XY.asInt(), d).target(target.position.x, target.position.y))
			.push(Tween.to(id, TweenType.SCALE_XY.asInt(), d).target(target.scale.x, target.scale.y))
			.push(Tween.to(id, TweenType.ALPHA.asInt(), d).target(target.alpha))
			.push(Tween.to(id, TweenType.ANGLE.asInt(), d).target(target.angleDegrees)).setCallback(zoneLayoutCallback));
	}
	
}
