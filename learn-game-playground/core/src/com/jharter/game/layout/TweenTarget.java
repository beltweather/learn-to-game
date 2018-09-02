package com.jharter.game.layout;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.Pools;
import com.jharter.game.ashley.components.Components.SpriteComp;
import com.jharter.game.ashley.components.Comp;

public class TweenTarget implements Poolable {
	
	public static TweenTarget newInstance() {
		return Pools.get(TweenTarget.class).obtain();
	}
	
	public static TweenTarget newInstance(SpriteComp s) {
		TweenTarget tt = newInstance();
		tt.setFromEntity(s);
		return tt;
	}
	
	public static TweenTarget newInstance(Entity entity) {
		TweenTarget tt = newInstance();
		tt.setFromEntity(entity);
		return tt;
	}
	
	private static float round(float x) {
		return Math.round(x);
	}
	
	public Vector3 position = new Vector3(0, 0, 0);
	public Vector2 scale = new Vector2(0, 0);
	public float alpha = 1f;
	public float angleDegrees = 0f;
	public float defaultDuration = 0.25f;
	public float duration = defaultDuration;
	
	private TweenTarget() {}
	
	public void setFromEntity(Entity entity) {
		setFromEntity(Comp.SpriteComp.get(entity));
	}
	
	public void setFromEntity(SpriteComp s) {
		position.set(s.position);
		scale.set(s.scale);
		alpha = s.alpha;
		angleDegrees = s.angleDegrees;
	}
	
	public boolean matchesTarget(SpriteComp s) {
		if(s == null) {
			return false;
		}
		
		round();
		boolean val = s.position.x == position.x &&
			   s.position.y == position.y &&
			   s.position.z == position.z &&
			   s.scale.x == scale.x &&
			   s.scale.y == scale.y &&
			   s.angleDegrees == angleDegrees &&
			   s.alpha == alpha;
	
		return val;
	}
	
	public void round() {
		position.x = round(position.x);
		position.y = round(position.y);
		position.z = round(position.z);
		angleDegrees = round(angleDegrees);
	}

	@Override
	public void reset() {
		position.set(0, 0, 0);
		scale.set(0, 0);
		alpha = 1f;
		angleDegrees = 0f;
		defaultDuration = 0.25f;
		duration = defaultDuration;
	}
	
}
