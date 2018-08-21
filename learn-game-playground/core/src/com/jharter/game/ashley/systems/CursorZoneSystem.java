package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
		
		if(ci.accept) {
			
			ZoneType nextZoneType = getNextZoneType(ci, zp, z);
			if(nextZoneType != ZoneType.NONE) {
				if(tryChangeZone(ci, zp, nextZoneType)) {
					t.region = getCursorForZone(zp.zoneType);
				}
			}

		} else if(ci.cancel) {

			if(zp.tryRevertToLastCheckpoint()) {
				t.region = getCursorForZone(zp.zoneType);
			}			
		
		} else if(ci.move()) {
			
			tryChangePosition(ci, zp, z);
			
		}
		
		ci.reset();
	}
	
	private ZoneType getNextZoneType(CursorInputComp ci, ZonePositionComp zp, ZoneComp z) {
		switch(zp.zoneType) {
			case HAND:
				return ZoneType.FRIEND;
			case FRIEND:
				return ZoneType.ENEMY;
			case ENEMY:
				return ZoneType.HAND;
			default:
				return ZoneType.NONE;
		}
	}
	
	private TextureRegion getCursorForZone(ZoneType zoneType) {
		switch(zoneType) {
			case HAND:
				return Media.handPointDown;
			case FRIEND:
				 return Media.handPointRight;
			case ENEMY:
				return Media.handPointLeft;
			default:
				return Media.handPointDown;
		}
	}
	
	private boolean tryChangeZone(CursorInputComp ci, ZonePositionComp zp, ZoneType zoneType) {
		zp.checkpoint();
		if(tryChangeZone(ci, zp, zoneType, 0, 0)) {
			return true;
		}
		zp.undoCheckpoint();
		return false;
	}
	
	private boolean tryChangeZone(CursorInputComp ci, ZonePositionComp zp, ZoneType zoneType, int newRow, int newCol) {
		int origRow = zp.row;
		int origCol = zp.col;
		ZoneType origZoneType = zp.zoneType;
		
		ZoneComp z = ZoneUtil.ZoneComp(zoneType);
		zp.zoneType = zoneType;
		zp.row = newRow;
		zp.col = newCol;
		
		if(!isValidPosition(zp, z)) {
			ci.direction.set(1,0);
			if(!tryChangePosition(ci, zp, z)) {
				zp.row = origRow;
				zp.col = origCol;
				zp.zoneType = origZoneType;
				return false;
			}
		}
		return true;
	}
	
	private boolean tryChangePosition(CursorInputComp ci, ZonePositionComp zp, ZoneComp z) {
		return tryChangePosition(ci, zp, z, zp.row, zp.col);
	}
	
	private boolean tryChangePosition(CursorInputComp ci, ZonePositionComp zp, ZoneComp z, int origRow, int origCol) {
		
		if(ci.direction.x != 0) {
			zp.col += ci.direction.x;
		}
		
		if(ci.direction.y != 0) {
			zp.row += ci.direction.y;
		}
		
		if(zp.col >= z.cols) {
			zp.col = 0;
			zp.row++;
		} else if(zp.col < 0) {
			zp.col = z.cols - 1;
			zp.row--;
		}
		
		if(zp.row >= z.rows) {
			zp.row = 0;
		} else if(zp.row < 0) {
			zp.row = z.rows - 1;
		}
		
		if(origRow == zp.col && origCol == zp.row) {
			return false;
		}
		
		if(!isValidPosition(zp, z)) {
			return tryChangePosition(ci, zp, z, origRow, origCol);
		}
		
		return true;
	}
	
	private boolean isValidPosition(ZonePositionComp zp, ZoneComp z) {
		if(z.zoneType == ZoneType.FRIEND && zp.row == 0) {
			return false;
		}
		return true;
	}
	
}
