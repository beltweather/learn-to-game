package com.jharter.game.ashley.components.subcomponents;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;
import com.jharter.game.ashley.components.Comp;
import com.jharter.game.ashley.components.Components.CardComp;
import com.jharter.game.ashley.components.Components.ZonePositionComp;
import com.jharter.game.util.id.ID;

import uk.co.carelesslabs.Enums.CardType;
import uk.co.carelesslabs.Enums.ZoneType;

public class TurnAction {
	
	public Array<ZoneType> targetZoneTypes = new Array<ZoneType>();
	public Array<ID> targetIDs = new Array<ID>();
	public VoidCallback<TurnAction> acceptCallback = null;
	public Callback<Entity, Boolean> validTargetCallback = null;
	public int defaultMultiplicity = 1;
	public int multiplicity = 1;
	public boolean defaultAll = false;
	public boolean all = false;
	public int priority = 0;
	public ID performerID = null;
	
	public boolean makesTargetAll = false;
	public int makesTargetMultiplicity = 1;
	
	public TurnAction() {}
	
	public Entity getPerformer() {
		if(performerID == null) {
			return null;
		}
		return Comp.Entity.get(performerID);
	}
	
	public ID getPerformerID() {
		return performerID;
	}
	
	public void setPerformerID(ID performerID) {
		this.performerID = performerID;
	}
	
	public Entity getEntity(int index) {
		if(index < 0 || index >= targetIDs.size) {
			return null;
		}
		return Comp.Entity.get(targetIDs.get(index));
	}
	
	public ZoneType getTargetZoneType() {
		if(hasAllTargets()) {
			return ZoneType.NONE;
		}
		return targetZoneTypes.get(targetIDs.size);
	}
	
	public ZoneType getNextTargetZoneType() {
		return getNextTargetZoneType(0);
	}
	
	public ZoneType getNextTargetZoneType(int depth) {
		if(hasAllTargets()) {
			return ZoneType.NONE;
		}
		int index = targetIDs.size + depth;
		if(index >= targetZoneTypes.size) {
			return ZoneType.NONE;
		}
		return targetZoneTypes.get(index);
	}
	
	public boolean hasAllTargets() {
		return targetZoneTypes.size == targetIDs.size;
	}
	
	public void addTarget(Entity entity) {
		if(!hasAllTargets()) {
			targetIDs.add(Comp.IDComp.get(entity).id);
		}
	}
	
	public boolean isValidTarget(Entity entity) {
		if(entity == null) {
			return false;
		}
		
		// Special check for cards that modify other cards, we don't
		// want them to be able to target each other
		CardComp c = Comp.CardComp.get(entity);
		ZonePositionComp zp = Comp.ZonePositionComp.get(entity);
		if(c != null && zp != null && c.cardType == CardType.TARGET_CARD && zp.getZoneComp().zoneType == ZoneType.ACTIVE_CARD) {
			return false;
		}
		
		if(validTargetCallback != null) {
			return validTargetCallback.call(entity);
		}
		
		return true;
	}
	
	public void performAcceptCallback() {
		if(acceptCallback != null) {
			if(multiplicity == 1) {
				acceptCallback.call(this);
			} else {
				for(int i = 0; i < multiplicity; i++) {
					acceptCallback.call(this);
				}
				multiplicity = defaultMultiplicity;
			}
		}
	}
	
	public void cleanUp() {
		multiplicity = defaultMultiplicity;
		all = defaultAll;
		targetIDs.clear();
	}
	
	public void freshCopyTo(TurnAction t) {
		t.targetZoneTypes = new Array<ZoneType>(targetZoneTypes);
		t.acceptCallback = acceptCallback;
		t.validTargetCallback = validTargetCallback;
		t.defaultMultiplicity = defaultMultiplicity;
		t.multiplicity = defaultMultiplicity;
		t.defaultAll = defaultAll;
		t.all = defaultAll;
		t.priority = priority;
		t.makesTargetAll = makesTargetAll;
		t.makesTargetMultiplicity = makesTargetMultiplicity;
	}
	
	public TurnAction freshCopy() {
		TurnAction t = new TurnAction();
		freshCopyTo(t);
		return t;
	}
		
	public void reset() {
		targetZoneTypes.clear();
		targetIDs.clear();
		acceptCallback = null;
		validTargetCallback = null;
		multiplicity = 1;
		all = false;
		defaultMultiplicity = 1;
		defaultAll = false;
		priority = 0;
		makesTargetAll = false;
		makesTargetMultiplicity = 1;
		performerID = null;
	}
	
}
