package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.jharter.game.ashley.components.Components.CursorInputComp;
import com.jharter.game.ashley.components.Components.TextureComp;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.Components.ZonePositionComp;
import com.jharter.game.ashley.components.Mapper;
import com.jharter.game.ashley.components.ZoneUtil;

import uk.co.carelesslabs.Enums.ZoneType;
import uk.co.carelesslabs.Media;

public class CursorZoneSystem extends IteratingSystem {

	@SuppressWarnings("unchecked")
	public CursorZoneSystem() {
		super(Family.all(TextureComp.class,
						 CursorInputComp.class,
						 ZonePositionComp.class).get());
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		CursorInputComp ci = Mapper.CursorInputComp.get(entity);
		ZonePositionComp zp = Mapper.ZonePositionComp.get(entity);
		ZoneComp z = ZoneUtil.ZoneComp(zp);
		TextureComp t = Mapper.TextureComp.get(entity);
		
		if(ci.direction.x != 0) {
			zp.col += ci.direction.x;
		}
		
		if(ci.direction.y != 0) {
			zp.row += ci.direction.y;
		}
		
		if(zp.col >= z.cols) {
			zp.col = 0;
		} else if(zp.col < 0) {
			zp.col = z.cols - 1;
		}
		
		if(zp.row >= z.rows) {
			zp.row = 0;
		} else if(zp.row < 0) {
			zp.row = z.rows - 1;
		}
		
		if(ci.accept) {
			switch(zp.zoneType) {
				case HAND:
					zp.zoneType = ZoneType.FRIEND;
					zp.row = 0;
					zp.col = 0;
					t.region = Media.handPointRight;
					break;
				case FRIEND:
					zp.zoneType = ZoneType.ENEMY;
					zp.row = 0;
					zp.col = 0;
					t.region = Media.handPointLeft;
					break;
				case ENEMY:
					zp.zoneType = ZoneType.HAND;
					zp.row = 0;
					zp.col = 0;
					t.region = Media.handPointDown;
					break;
				default:
					break;
			}
		}
		
		ci.reset();
	}
	
}
