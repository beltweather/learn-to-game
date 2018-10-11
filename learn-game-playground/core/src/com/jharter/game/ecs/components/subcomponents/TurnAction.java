package com.jharter.game.ecs.components.subcomponents;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;
import com.jharter.game.ecs.components.Components.CardComp;
import com.jharter.game.ecs.components.Components.ZoneComp;
import com.jharter.game.ecs.components.Components.ZonePositionComp;
import com.jharter.game.ecs.entities.EntityHandler;
import com.jharter.game.ecs.entities.IEntityHandler;
import com.jharter.game.effect.Effect;
import com.jharter.game.effect.EffectProp;
import com.jharter.game.util.id.ID;

import uk.co.carelesslabs.Enums.CardType;
import uk.co.carelesslabs.Enums.ZoneType;

public class TurnAction extends EntityHandler {
	
	public Array<ZoneType> targetZoneTypes = new Array<ZoneType>();
	public Array<ID> targetIDs = new Array<ID>();
	public VoidCallback<TurnAction> acceptCallback = null;
	public Callback<Entity, Boolean> validTargetCallback = null;
	public int defaultMultiplicity = 1;
	public int multiplicity = 1;
	public boolean defaultAll = false;
	public boolean all = false;
	public int priority = 0;
	public ID entityID = null;
	public ID ownerID = null;
	
	public boolean makesTargetAll = false;
	public int makesTargetMultiplicity = 1;
	
	public Array<Effect<?>> effects = new Array<Effect<?>>();
	
	public TurnAction(IEntityHandler handler) {
		super(handler);
	}
	
	public Entity getEntity() {
		if(entityID == null) {
			return null;
		}
		return Comp.Entity.get(entityID);
	}
	
	public Entity getOwnerEntity() {
		if(ownerID == null) {
			return null;
		}
		return Comp.Entity.get(ownerID);
	}
	
	public Entity getTargetEntity(int index) {
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
		
		// Special check for cards that modify other cards, we don't want them to be able to target each other
		if(isUntargetableCard(entity)) {
			return false;
		}
		
		if(validTargetCallback != null) {
			return validTargetCallback.call(entity);
		}
		
		return true;
	}
	
	public boolean isUntargetableCard(Entity entity) {
		CardComp c = Comp.CardComp.get(entity);
		ZonePositionComp zp = Comp.ZonePositionComp.get(entity);
		return c != null && 
			   zp != null && 
			   c.cardType == CardType.TARGET_CARD && 
			   Comp.ZoneComp.get(zp.zoneID).zoneType == ZoneType.FRIEND_ACTIVE_CARD;
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
	
	private Array<ID> temp = new Array<ID>();

	public Array<ID> getAllTargetIDs() {
		return getAllTargetIDs(true);
	}
	
	public Array<ID> getAllTargetIDs(boolean inFinalZoneOnly) {
		if(!all || targetIDs.size == 0) {
			return targetIDs;
		}
		
		temp.clear();
		temp.addAll(targetIDs);
		ZonePositionComp zp = Comp.ZonePositionComp.get(targetIDs.peek());
		ZoneComp z = Comp.ZoneComp.get(zp.zoneID);
		for(ID id : z.objectIDs) {
			if(inFinalZoneOnly && Comp.ZoneComp.get(Comp.ZonePositionComp.get(id).zoneID).zoneType != z.zoneType) {
				continue;
			}
			if(!temp.contains(id, false)) {
				temp.add(id);
			}
		}
		return temp;
	}
	
	public void addEffect(Effect<?> effect) {
		addEffect(effect, targetZoneTypes.size - 1);
	}
	
	public void addEffect(Effect<?> effect, int targetIndex) {
		effects.add(effect);
		effect.setTurnAction(this);
		effect.setTargetIndex(targetIndex);
	}
	
	public void perform() {
		for(int i = 0; i < multiplicity; i++) {
			for(Effect<?> effect : effects) {
				if(all && effect.getTargetIndex() == targetIDs.size - 1) {
					for(ID targetID : getAllTargetIDs(true)) {
						effect.perform(Comp.Entity.get(targetID));
					}
				} else {
					effect.perform();
				}
			}
		}
	}
	
	public void applyResult(Entity target, int targetIndex) {
		for(int i = 0; i < multiplicity; i++) {
			for(Effect<?> effect : effects) {
				if(effect.getTargetIndex() != targetIndex) {
					continue;
				}
				effect.applyResult(target);
			}
		}
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
		entityID = null;
	}
	
}
