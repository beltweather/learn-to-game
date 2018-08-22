package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.jharter.game.ashley.components.Components.CursorComp;
import com.jharter.game.ashley.components.Components.PositionComp;
import com.jharter.game.ashley.components.Components.SizeComp;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.Components.ZonePositionComp;
import com.jharter.game.ashley.components.Mapper;

public class CursorPositionSystem extends IteratingSystem {

	@SuppressWarnings("unchecked")
	public CursorPositionSystem() {
		super(Family.all(CursorComp.class, ZonePositionComp.class, PositionComp.class, SizeComp.class).get());
	}
	
	public void processEntity(Entity entity, float deltaTime) {
		ZonePositionComp zp = Mapper.ZonePositionComp.get(entity);
		ZoneComp z = Mapper.ZoneComp.get(zp);
		PositionComp p = Mapper.PositionComp.get(entity);
		SizeComp s = Mapper.SizeComp.get(entity);
		
		Entity target = Mapper.Entity.get(z.objects.get(zp.row * z.cols + zp.col));
		
		PositionComp tp = Mapper.PositionComp.get(target);
		SizeComp ts = Mapper.SizeComp.get(target);
		
		switch(zp.zoneType) {
			case HAND:
				p.position.x = tp.position.x + (ts.scaledWidth() - s.scaledWidth()) / 2;
				p.position.y = tp.position.y + ts.scaledHeight() - (int) (s.scaledHeight() * 0.25);
				break;
			case FRIEND:
				p.position.x = tp.position.x - s.scaledWidth();
				p.position.y = tp.position.y + (ts.scaledHeight() - s.scaledHeight()) / 2;
				break;
			case ENEMY:
				p.position.x = tp.position.x + ts.scaledWidth(); 
				p.position.y = tp.position.y + (ts.scaledHeight() - s.scaledHeight()) / 2;
				break;
			default:
				break;
		}
	}

}
