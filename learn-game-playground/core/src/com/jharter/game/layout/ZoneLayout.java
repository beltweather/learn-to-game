package com.jharter.game.layout;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pools;
import com.jharter.game.ashley.components.Components.SpriteComp;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.Mapper;
import com.jharter.game.util.id.ID;

public abstract class ZoneLayout {
	
	protected ImmutableArray<ID> ids = null;
	private ObjectMap<ID, TweenTarget> dataById = new ObjectMap<ID, TweenTarget>();
	
	public ZoneLayout(ZoneComp z) {
		setIds(z.objectIDs);
	}
	
	public void setIds(ImmutableArray<ID> ids) {
		this.ids = ids;
	}
	
	public void revalidate() {
		for(TweenTarget t : dataById.values()) {
			Pools.free(t);
		}
		dataById.clear();
	}
	
	public TweenTarget getTarget(ID id) {
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
		Entity entity = Mapper.Entity.get(id);
		if(entity == null) {
			return null;
		}
		
		TweenTarget target = getTarget(id, index, entity, Pools.get(TweenTarget.class).obtain());
		dataById.put(id, target);
		return target;
	}
	
	public boolean matchesTarget(ID id) {
		TweenTarget target = getTarget(id);
		if(target == null) {
			return false;
		}
		Entity entity = Mapper.Entity.get(id);
		if(entity == null) {
			return false;
		}
		return matchesTarget(entity, target);
	}
	
	public boolean matchesTarget(Entity entity, TweenTarget target) {
		if(target == null) {
			return false;
		}
		return target.matchesTarget(Mapper.SpriteComp.get(entity));
	}
	
	protected abstract TweenTarget getTarget(ID id, int index, Entity entity, TweenTarget target);
	
	public void reset() {
		for(TweenTarget d : dataById.values()) {
			Pools.free(d);
		}
		dataById.clear();
		ids = null;
	}

}
