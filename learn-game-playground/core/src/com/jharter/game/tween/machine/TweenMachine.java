package com.jharter.game.tween.machine;

import com.jharter.game.ecs.entities.EntityHandler;
import com.jharter.game.ecs.entities.IEntityHandler;
import com.jharter.game.layout.TweenTarget;
import com.jharter.game.util.id.ID;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.TweenEquation;

public class TweenMachine<T extends TweenMachine<?>> extends EntityHandler {
	
	private static TweenTarget buildTarget(IEntityHandler handler, ID id) {
		TweenTarget tt = TweenTarget.newInstance();
		tt.setFromEntityID(handler, id);
		return tt;
	}
	
	protected ID id;
	protected TweenTarget tt;
	protected boolean reflectX = false;
	protected boolean reflectY = false;
	protected boolean reflectAngle = false;
	
	@SuppressWarnings("unchecked")
	protected T _this = (T) this;
	
	public TweenMachine(IEntityHandler handler) {
		super(handler);
	}
	
	public T identity() {
		reflectX = false;
		reflectY = false;
		reflectAngle = false;
		return _this;
	}
	
	public T reflectX(boolean b) {
		reflectX = b;
		return _this;
	}
	
	public T reflectY(boolean b) {
		reflectY = b;
		return _this;
	}
	
	public T reflectAngle(boolean b) {
		reflectAngle = b;
		return _this;
	}
	
	public T newTarget(ID id) {
		this.id = id;
		return newTarget();
	}
	
	public T newTarget() {
		tt = buildTarget(this, id);
		identity();
		return _this;
	}
	
	public T moveX(float x) {
		tt.position.x += reflectX ? -x : x;
		return _this;
	}
	
	public T moveY(float y) {
		tt.position.y += reflectY ? -y : y;
		return _this;
	}
	
	public T moveZ(float z) {
		tt.position.z += z;
		return _this;
	}
	
	public T moveScaleX(float scaleX) {
		tt.scale.x += scaleX;
		return _this;
	}
	
	public T moveScaleY(float scaleY) {
		tt.scale.y += scaleY;
		return _this;
	}
	
	public T moveAlpha(float alpha) {
		tt.alpha += alpha;
		return _this;
	}
	
	public T moveAngle(float angle) {
		tt.angleDegrees += reflectAngle ? -angle : angle;
		return _this;
	}
	
	public T moveDuration(float duration) {
		tt.duration += duration;
		return _this;
	}
	
	public T setX(float x) {
		tt.position.x = reflectX ? -x : x;
		return _this;
	}
	
	public T setY(float y) {
		tt.position.y = reflectY ? -y : y;
		return _this;
	}
	
	public T setZ(float z) {
		tt.position.z = z;
		return _this;
	}
	
	public T setScaleX(float scaleX) {
		tt.scale.x = scaleX;
		return _this;
	}
	
	public T setScaleY(float scaleY) {
		tt.scale.y = scaleY;
		return _this;
	}
	
	public T setAlpha(float alpha) {
		tt.alpha = alpha;
		return _this;
	}
	
	public T setAngle(float angle) {
		tt.angleDegrees = reflectAngle ? -angle : angle;
		return _this;
	}
	
	public T setDuration(float duration) {
		tt.duration = duration;
		return _this;
	}
	
	public T setTarget(TweenTarget tt) {
		this.tt = tt;
		return _this;
	}
	
	public T setID(ID id) {
		this.id = id;
		return _this;
	}
	
	public T setEase(TweenEquation ease) {
		tt.ease = ease;
		return _this;
	}
	
	public TweenTarget getTarget() {
		return tt;
	}
	
	public Timeline getTimeline() {
		return getTimeline(false);
	}
	
	public Timeline getTimeline(boolean addAnimationCallback) {
		Timeline timeline = getTweenManager().build(id, tt);
		if(addAnimationCallback) {
			return timeline.setCallback(getTweenManager().buildFinishedAnimationCallback(id));
		}
		return timeline;
	}
	
	public void start() {
		getTweenManager().start(id, tt);
	}
	
}
