package com.jharter.game.layout;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pools;
import com.jharter.game.ashley.components.Components.InvisibleComp;
import com.jharter.game.ashley.components.Components.SpriteComp;
import com.jharter.game.ashley.components.M;
import com.jharter.game.util.id.ID;

public abstract class ZoneLayout {
	
	protected ImmutableArray<ID> ids = null;
	protected ObjectMap<ID, TweenTarget> dataById = new ObjectMap<ID, TweenTarget>();
	protected boolean tween = true;
	protected boolean allowRelativePositions = true;
	
	public ZoneLayout() {}
	
	public void setIds(ImmutableArray<ID> ids) {
		this.ids = ids;
	}
	
	public boolean isTweenEnabled() {
		return tween;
	}
	
	public void enableTween() {
		tween = true;
	}
	
	public void disableTween() {
		tween = false;
	}
	
	public void enableRelativePositions() {
		allowRelativePositions = true;
	}
	
	public void disableRelativePositions() {
		allowRelativePositions = false;
	}
	
	public void revalidate() {
		for(TweenTarget t : dataById.values()) {
			Pools.free(t);
		}
		dataById.clear();
	}
	
	public TweenTarget getTarget(ID id) {
		return getTarget(id, false);
	}
	
	public TweenTarget getTarget(ID id, boolean applyModification) {
		if(id == null) {
			return null;
		}
		if(dataById.containsKey(id)) {
			return dataById.get(id);
		}
		int index = ids.indexOf(id, false);
		if(index < 0) {
			return null;
		}
		Entity entity = M.Entity.get(id);
		if(entity == null) {
			return null;
		}
		
		TweenTarget target = getTarget(id, index, entity, TweenTarget.newInstance());
        boolean hide = false;
		if(allowRelativePositions) {
			SpriteComp s = M.SpriteComp.get(entity);
			if(target != null && s != null && s.relativePositionRules.relative) {
				if(!s.relativePositionRules.setToRelativePosition(s, target)) {
					hide(entity);
					hide = true;
				} else if(!s.relativePositionRules.tween) {
					target.duration = 0f;
				}
			}
		}
		
		if(applyModification) {
			if(!hide) {
				show(entity);
			}
			modifyEntity(id, index, entity, target);
		}
		
		dataById.put(id, target);
		
		if(!isTweenEnabled()) {
			target.duration = 0f;
		}
		
		return target;
	}
	
	public boolean matchesTarget(ID id) {
		TweenTarget target = getTarget(id);
		if(target == null) {
			return false;
		}
		Entity entity = M.Entity.get(id);
		if(entity == null) {
			return false;
		}
		return matchesTarget(entity, target);
	}
	
	public boolean matchesTarget(Entity entity, TweenTarget target) {
		if(target == null) {
			return false;
		}
		return target.matchesTarget(M.SpriteComp.get(entity));
	}
	
	public void show(Entity entity) {
		if(M.InvisibleComp.has(entity)) {
			entity.remove(InvisibleComp.class);
		}
	}
	
	public void hide(Entity entity) {
		if(!M.InvisibleComp.has(entity)) {
			entity.add(M.Comp.get(InvisibleComp.class));
		}
	}
	
	protected abstract TweenTarget getTarget(ID id, int index, Entity entity, TweenTarget target);
	
	protected void modifyEntity(ID id, int index, Entity entity, TweenTarget target) {
		
	}
	
	public void reset() {
		for(TweenTarget d : dataById.values()) {
			Pools.free(d);
		}
		dataById.clear();
		ids = null;
	}

}
