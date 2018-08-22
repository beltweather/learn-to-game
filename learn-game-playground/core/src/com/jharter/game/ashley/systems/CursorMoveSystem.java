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

	private ObjectSet<Integer> usedIndices = new ObjectSet<Integer>();
	private Vector2 position = new Vector2();
	
	@SuppressWarnings("unchecked")
	public CursorMoveSystem() {
		super(Family.all(CursorComp.class,
						 CursorInputComp.class,
						 TextureComp.class,
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
		
		int index = findNextValidIndex(zp, z, t, (int) ci.direction.x, (int) ci.direction.y);
		if(index >= 0) {
			zp.set(index);
		}
	}
	
	protected int findNextValidIndex(ZonePositionComp zp, ZoneComp z, TargetingComp t, int colDirection, int rowDirection) {
		position.set(zp.col, zp.row);
		usedIndices.clear();
		int index;
		while(true) {
			index = findNextIndex(position, z.cols, z.rows, colDirection, rowDirection);
			if(t == null || t.isValidTarget(Mapper.Entity.get(z.objects.get(index)))) {
				return index;
			}
			if(usedIndices.contains(index)) {
				return -1;
			}
			usedIndices.add(index);
		}
	}
	
	protected int findNextIndex(Vector2 position, int cols, int rows, int colDirection, int rowDirection) {
		
		if(colDirection != 0) {
			position.x += colDirection;
		
			if(position.x >= cols) {
				position.x = 0;
				position.y++;
			} else if(position.x < 0) {
				position.x = cols - 1;
				position.y--;
			}
			
			if(position.y >= rows) {
				position.x = 0;
				position.y = 0;
			} else if(position.y < 0) {
				position.x = cols - 1;
				position.y = rows - 1;
			}
		
		} else if(rowDirection != 0) {
			position.y += rowDirection;
		
			if(position.y >= rows) {
				position.y = 0;
				position.x++;
			} else if(position.y < 0) {
				position.y = rows - 1;
				position.x--;
			}
			
			if(position.x >= cols) {
				position.y = 0;
				position.x = 0;
			} else if(position.x < 0) {
				position.y = rows - 1;
				position.x = cols - 1;
			}
		
		}
		
		
		return (int) (position.y * cols + position.x);
		
	}
	
}
