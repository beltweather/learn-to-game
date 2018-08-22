package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectSet;
import com.jharter.game.ashley.components.Components.CursorComp;
import com.jharter.game.ashley.components.Components.CursorInputComp;
import com.jharter.game.ashley.components.Components.TargetingComp;
import com.jharter.game.ashley.components.Components.TextureComp;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.Components.ZonePositionComp;
import com.jharter.game.ashley.components.Mapper;
import com.jharter.game.ashley.entities.EntityUtil;

public class CursorMoveSystem extends IteratingSystem {

	@SuppressWarnings("unchecked")
	public CursorMoveSystem() {
		super(Family.all(CursorComp.class,
						 CursorInputComp.class,
						 ZonePositionComp.class).get());
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		CursorInputComp ci = Mapper.CursorInputComp.get(entity);
		if(!ci.move()) {
			return;
		}
		
		CursorComp c = Mapper.CursorComp.get(entity);
		ZonePositionComp zp = Mapper.ZonePositionComp.get(entity);
		ZoneComp z = zp.getZoneComp();
		TargetingComp t = c.getTargetingComp();
		
		int direction = (int) (ci.direction.x != 0 ? ci.direction.x : ci.direction.y);
		
		int index = findNextValidIndex(zp, z, t, direction);
		if(z.hasIndex(index)) {
			zp.index(index);
		} else {
			zp.index(-1);
		}
	}
	
	protected int findNextValidIndex(ZonePositionComp zp, ZoneComp z, TargetingComp t, int direction) {
		int index = zp.index();
		for(int i = 0; i < z.size(); i++) {
			index = findNextIndex(index, direction, z.size());
			if(t == null || t.isValidTarget(Mapper.Entity.get(z.get(index)))) {
				return index;
			}
		}			
		return -1;
	}
	
	protected int findNextIndex(int currentIndex, int direction, int size) {
		int index = currentIndex + direction;
		if(index < 0) {
			index = size - 1;
		} else if(index >= size) {
			index = 0;
		}
		return index;
	}
	
}
