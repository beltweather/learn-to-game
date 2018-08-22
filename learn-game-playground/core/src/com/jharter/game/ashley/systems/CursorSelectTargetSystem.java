package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.jharter.game.ashley.components.Components.ActionQueueableComp;
import com.jharter.game.ashley.components.Components.ActiveCardComp;
import com.jharter.game.ashley.components.Components.CursorComp;
import com.jharter.game.ashley.components.Components.CursorInputComp;
import com.jharter.game.ashley.components.Components.TargetingComp;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.Components.ZonePositionComp;
import com.jharter.game.ashley.components.Mapper;

import uk.co.carelesslabs.Enums.ZoneType;

public class CursorSelectTargetSystem extends CursorMoveSystem {

	@SuppressWarnings("unchecked")
	public CursorSelectTargetSystem() {
		super();
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		CursorComp c = Mapper.CursorComp.get(entity);
		CursorInputComp ci = Mapper.CursorInputComp.get(entity);
		ZonePositionComp zp = Mapper.ZonePositionComp.get(entity);
		ZoneComp z = zp.getZoneComp();
		
		// A little failsafe here for when zones are empty
		if(!z.hasIndex(zp)) {
			ci.reset();
			return;
		}

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
						
						// XXX Not sure if this should be here
						if(zp.zoneType() == ZoneType.HAND) {
							zp.clearHistory();
						}
						
					} else {
						
						t.targetIDs.pop();
						if(t.targetIDs.size == 0) {
							c.targetingEntityID = null;
						}
						
					}
				}
				
				if(t.hasAllTargets() && c.targetingEntityID != null) {
					Entity targetingEntity = Mapper.Entity.get(c.targetingEntityID);
					targetingEntity.add(Mapper.Comp.get(ActionQueueableComp.class));
					c.targetingEntityID = null;
				}
			}
			
		} else if(ci.cancel) {

			TargetingComp t = c.getTargetingComp();
			if(zp.tryRevertToLastCheckpoint()) {
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
		if(t == null) {
			return ZoneType.HAND;
		} else if(t.hasAllTargets()) {
			return ZoneType.HAND;
		}
		return t.getTargetZoneType();
	}
	
	private boolean tryChangeZone(CursorComp c, CursorInputComp ci, ZonePositionComp zp, ZoneType zoneType, TargetingComp t) {
		zp.checkpoint();
		if(tryChangeZone(c, ci, zp, zoneType, t, 0)) {
			return true;
		}
		zp.undoCheckpoint();
		return false;
	}
	
	private boolean tryChangeZone(CursorComp c, CursorInputComp ci, ZonePositionComp zp, ZoneType zoneType, TargetingComp t, int newIndex) {
		int origIndex = zp.index();
		ZoneType origZoneType = zp.zoneType();
		ZoneComp z = Mapper.ZoneComp.get(zoneType);
		zp.zoneType(zoneType);
		zp.index(newIndex);
		
		if(t == null || t.hasAllTargets() || t.isValidTarget(z.getEntity(zp))) {
			return true;
		}
		
		int index = findNextValidIndex(zp, z, t, 1);
		if(!z.hasIndex(index)) {
			zp.index(origIndex);
			zp.zoneType(origZoneType);
			return false;
		} 

		zp.index(index);
		return true;
	}

}
