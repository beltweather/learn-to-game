package com.jharter.game.tween;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.jharter.game.ashley.components.Components.AnimatingComp;
import com.jharter.game.ashley.entities.EntityHandler;
import com.jharter.game.ashley.entities.IEntityHandler;
import com.jharter.game.layout.TweenTarget;
import com.jharter.game.tween.TweenCallbacks.CompositeCallback;
import com.jharter.game.tween.TweenCallbacks.FinishedAnimatingCallback;
import com.jharter.game.util.id.ID;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Circ;

public class GameTweenManager extends EntityHandler {
	
	private TweenManager manager;

	public GameTweenManager(IEntityHandler handler) {
		super(handler);
		manager = new TweenManager();
		registerAccessors();
	}
	
	private void registerAccessors() {
		//Tween.setCombinedAttributesLimit(4);
		Tween.registerAccessor(ID.class, new IDTweenAccessor(this));
		Tween.registerAccessor(Entity.class, new EntityTweenAccessor(this));
		Tween.registerAccessor(Vector3.class, new Vector3TweenAccessor());
		Tween.registerAccessor(Vector2.class, new Vector2TweenAccessor());
		Tween.registerAccessor(Float.class, new FloatTweenAccessor());
		Tween.registerAccessor(Array.class, new FloatArrayTweenAccessor());
	}
	
	public void update(float deltaTime) {
		manager.update(deltaTime);
	}
	
	public void start(Engine engine, ID id, BaseTween<?> baseTween) {
		start(engine, id, baseTween, null);
	}
	
	public void start(Engine engine, ID id, BaseTween<?> baseTween, TweenCallback callback) {
		if(id != null) {
			Entity entity = Comp.Entity.get(id);
			AnimatingComp a = Comp.getOrAdd(engine, AnimatingComp.class, entity);
			a.activeCount++;
			FinishedAnimatingCallback finishedCallback = TweenCallbacks.newInstance(this, FinishedAnimatingCallback.class);
			finishedCallback.setID(id);
			
			if(callback == null) {
				baseTween.setCallback(finishedCallback);
			} else {
				CompositeCallback cc = TweenCallbacks.newInstance(this, CompositeCallback.class);
				cc.addCallback(callback);
				cc.addCallback(finishedCallback);
				baseTween.setCallback(cc);
			}
		}
		
		baseTween.start(manager);
	}
	
	public void start(Engine engine, Entity entity, TweenTarget target) {
		start(engine, Comp.IDComp.get(entity).id, target);
	}
	
	public void start(Engine engine, ID id, TweenTarget target) {
		start(engine, id, target, target.duration);
	}
	
	public void start(Engine engine, ID id, TweenTarget target, float duration) {
		start(engine, id, tween(id, target, duration));
	}
	
	public Timeline tween(ID id, TweenTarget target) {
		return tween(id, target, target.duration);
	}
	
	public Timeline tween(ID id, TweenTarget target, float duration) {
		float d = duration;
		target.round();
		return Timeline.createParallel()
			.push(Tween.to(id, TweenType.POSITION_XY.asInt(), d).ease(Circ.INOUT).target(target.position.x, target.position.y))
			.push(Tween.to(id, TweenType.SCALE_XY.asInt(), d).ease(Circ.INOUT).target(target.scale.x, target.scale.y))
			.push(Tween.to(id, TweenType.ALPHA.asInt(), d).ease(Circ.INOUT).target(target.alpha))
			.push(Tween.to(id, TweenType.ANGLE.asInt(), d).ease(Circ.INOUT).target(target.angleDegrees));
	}
	
	public Timeline set(ID id, TweenTarget target) {
		return tween(id, target, 0f);
		
		/*target.round();
		return Timeline.createParallel()
			.push(Tween.set(id, TweenType.POSITION_XY.asInt()).target(target.position.x, target.position.y))
			.push(Tween.set(id, TweenType.SCALE_XY.asInt()).target(target.scale.x, target.scale.y))
			.push(Tween.set(id, TweenType.ALPHA.asInt()).target(target.alpha))
			.push(Tween.set(id, TweenType.ANGLE.asInt()).target(target.angleDegrees));*/
	}
	
}
