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
	private ObjectMap<ID, LayoutTarget> dataById = new ObjectMap<ID, LayoutTarget>();
	
	public ZoneLayout(ZoneComp z) {
		setIds(z.objectIDs);
	}
	
	public void setIds(ImmutableArray<ID> ids) {
		this.ids = ids;
	}
	
	public void revalidate() {
		for(LayoutTarget t : dataById.values()) {
			Pools.free(t);
		}
		dataById.clear();
	}
	
	public LayoutTarget getTarget(ID id) {
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
		
		LayoutTarget target = getTarget(id, index, entity, Pools.get(LayoutTarget.class).obtain());
		dataById.put(id, target);
		return target;
	}
	
	public boolean matchesTarget(ID id) {
		LayoutTarget target = getTarget(id);
		if(target == null) {
			return false;
		}
		Entity entity = Mapper.Entity.get(id);
		if(entity == null) {
			return false;
		}
		return matchesTarget(entity, target);
	}
	
	public boolean matchesTarget(Entity entity, LayoutTarget target) {
		SpriteComp s = Mapper.SpriteComp.get(entity);
		if(s == null) {
			return false;
		}
		
		return s.position.x == target.position.x &&
			   s.position.y == target.position.y &&
			   s.position.z == target.position.z &&
			   s.scale.x == target.scale.x &&
			   s.scale.y == target.scale.y &&
			   s.angleDegrees == target.angleDegrees &&
			   s.alpha == target.alpha;
	}
	
	protected abstract LayoutTarget getTarget(ID id, int index, Entity entity, LayoutTarget target);
	
	public void reset() {
		for(LayoutTarget d : dataById.values()) {
			Pools.free(d);
		}
		dataById.clear();
		ids = null;
	}

}
