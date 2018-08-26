package com.jharter.game.layout;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pools;
import com.jharter.game.ashley.components.Components.AlphaComp;
import com.jharter.game.ashley.components.Components.PositionComp;
import com.jharter.game.ashley.components.Components.SizeComp;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.Mapper;
import com.jharter.game.util.id.ID;

public abstract class ZoneLayout {
	
	protected ImmutableArray<ID> ids = null;
	private ObjectMap<ID, LayoutTarget> dataById = new ObjectMap<ID, LayoutTarget>();
	
	public ZoneLayout(ZoneComp z) {
		setIds(new ImmutableArray<ID>(z.getIds()));
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
		PositionComp p = Mapper.PositionComp.get(entity);
		SizeComp s = Mapper.SizeComp.get(entity);
		AlphaComp a = Mapper.AlphaComp.get(entity);
		if(p == null || s == null) {
			return false;
		}
		
		// Allow alpha to be null only if our alpha
		// is set to one. Require position and size though
		if(target.alpha != 1 && a == null) {
			return false;
		} else if(target.alpha != a.alpha) {
			return false;
		}
		
		return p.position.x == target.position.x &&
			   p.position.y == target.position.y &&
			   p.position.z == target.position.z &&
			   s.scale.x == target.scale.x &&
			   s.scale.y == target.scale.y &&
			   p.angleDegrees == target.angleDegrees;
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
