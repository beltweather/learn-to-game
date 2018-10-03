package com.jharter.game.layout;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.jharter.game.ecs.components.Components.InvisibleComp;
import com.jharter.game.ecs.components.Components.SpriteComp;
import com.jharter.game.ecs.entities.EntityHandler;
import com.jharter.game.ecs.entities.IEntityHandler;
import com.jharter.game.ecs.systems.ZoneLayoutSystem;
import com.jharter.game.util.id.ID;

public abstract class ZoneLayout extends EntityHandler {
	
	protected Array<ID> ids = null;
	protected ObjectMap<ID, TweenTarget> dataById = new ObjectMap<ID, TweenTarget>();
	protected boolean tween = true;
	protected boolean allowRelativePositions = true;
	protected transient ZoneLayoutSystem system = null;
	protected int priority = 0;
	protected ID activePlayerID = null;
	
	public ZoneLayout(IEntityHandler handler) {
		super(handler);
	}
	
	public int getPriority() {
		return priority;
	}
	
	public ID getActivePlayerID() {
		return activePlayerID;
	}
	
	public ZoneLayout setPriority(int priority) {
		this.priority = priority;
		return this;
	}
	
	public void setActivePlayerID(ID activePlayerID) {
		this.activePlayerID = activePlayerID;
	}
	
	public void setIds(Array<ID> ids) {
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
		for(TweenTarget tt : dataById.values()) {
			tt.free();
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
		Entity entity = Comp.Entity.get(id);
		if(entity == null) {
			return null;
		}
		
		TweenTarget target = getTarget(id, index, entity, TweenTarget.newInstance());
		if(target == null) {
			return null;
		}
		
        boolean hide = false;
		if(allowRelativePositions) {
			SpriteComp s = Comp.SpriteComp.get(entity);
			if(target != null && s != null && s.relativePositionRules.enabled) {
				if(!s.relativePositionRules.setToRelativePosition(this, s, target)) {
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
		Entity entity = Comp.Entity.get(id);
		if(entity == null) {
			return false;
		}
		return matchesTarget(entity, target);
	}
	
	public boolean matchesTarget(Entity entity, TweenTarget target) {
		if(target == null) {
			return false;
		}
		return target.matchesTarget(Comp.SpriteComp.get(entity));
	}
	
	public void show(Entity entity) {
		Comp.remove(InvisibleComp.class, entity);
	}
	
	public void hide(Entity entity) {
		Comp.add(InvisibleComp.class, entity);
	}
	
	protected abstract TweenTarget getTarget(ID id, int index, Entity entity, TweenTarget target);
	
	protected void modifyEntity(ID id, int index, Entity entity, TweenTarget target) {
		
	}
	
	public void reset() {
		for(TweenTarget tt : dataById.values()) {
			tt.free();
		}
		dataById.clear();
		ids = null;
		priority = 0;
	}

}
