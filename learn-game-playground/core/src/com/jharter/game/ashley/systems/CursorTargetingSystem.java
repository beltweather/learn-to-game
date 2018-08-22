package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.jharter.game.ashley.components.Components.ActiveCardComp;
import com.jharter.game.ashley.components.Components.CursorComp;
import com.jharter.game.ashley.components.Components.CursorInputComp;
import com.jharter.game.ashley.components.Components.TargetingComp;
import com.jharter.game.ashley.components.Components.TextureComp;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.Components.ZonePositionComp;
import com.jharter.game.ashley.components.Mapper;

import uk.co.carelesslabs.Enums.ZoneType;
import uk.co.carelesslabs.Media;

public class CursorTargetingSystem extends CursorMoveSystem {

	@SuppressWarnings("unchecked")
	public CursorTargetingSystem() {
		super();
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		CursorComp c = Mapper.CursorComp.get(entity);
		CursorInputComp ci = Mapper.CursorInputComp.get(entity);
		ZonePositionComp zp = Mapper.ZonePositionComp.get(entity);
		ZoneComp z = zp.getZoneComp();
		TextureComp te = Mapper.TextureComp.get(entity);

		if(ci.accept) {
			TargetingComp t = c.getTargetingComp();
			Entity acceptEntity = z.getEntity(zp);
			if(t == null) {
				t = Mapper.TargetingComp.get(acceptEntity);
				if(t != null) {
					c.targetingEntityID = Mapper.IDComp.get(acceptEntity).id;
				}
			}
				
			if(t != null) {
				if(!t.hasAllTargets()) {
					t.addTarget(acceptEntity);
				} 
				ZoneType nextZoneType = tryGetNextZoneType(c, zp, z, t);
				if(nextZoneType != ZoneType.NONE) {
					if(tryChangeZone(c, ci, zp, nextZoneType, t)) {
						te.region = getCursorForZone(zp.zoneType);
						if(zp.zoneType == ZoneType.HAND) {
							zp.clearHistory();
						}
					}
				}
			}
			
		} else if(ci.cancel) {

			TargetingComp t = c.getTargetingComp();
			if(zp.tryRevertToLastCheckpoint()) {
				te.region = getCursorForZone(zp.zoneType);
				entity.remove(ActiveCardComp.class);
				if(t != null) {
					t.targetIDs.pop();
					if(t.targetIDs.size == 0) {
						c.targetingEntityID = null;
					}
				}
			}			
		}
		
		ci.reset();
	}
	
	private ZoneType tryGetNextZoneType(CursorComp c, ZonePositionComp zp, ZoneComp z, TargetingComp t) {
		if(t == null || t.hasAllTargets()) {
			return ZoneType.HAND;
		}
		return t.getTargetZoneType();
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
	
	private boolean tryChangeZone(CursorComp c, CursorInputComp ci, ZonePositionComp zp, ZoneType zoneType, TargetingComp t) {
		zp.checkpoint();
		if(tryChangeZone(c, ci, zp, zoneType, t, 0, 0)) {
			return true;
		}
		zp.undoCheckpoint();
		return false;
	}
	
	private boolean tryChangeZone(CursorComp c, CursorInputComp ci, ZonePositionComp zp, ZoneType zoneType, TargetingComp t, int newRow, int newCol) {
		int origRow = zp.row;
		int origCol = zp.col;
		ZoneType origZoneType = zp.zoneType;
		ZoneComp z = Mapper.ZoneComp.get(zoneType);
		zp.zoneType = zoneType;
		zp.row = newRow;
		zp.col = newCol;
		
		if(t == null || t.hasAllTargets() || t.isValidTarget(z.getEntity(zp))) {
			return true;
		}
		
		int index = findNextValidIndex(zp, z, t, 1, 0);
		if(index < 0) {
			zp.row = origRow;
			zp.col = origCol;
			zp.zoneType = origZoneType;
			return false;
		} 

		zp.set(index);
		return true;
	}

}
