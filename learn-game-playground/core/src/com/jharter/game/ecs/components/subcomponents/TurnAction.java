package com.jharter.game.ecs.components.subcomponents;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;
import com.jharter.game.ecs.components.Components.ZoneComp;
import com.jharter.game.ecs.components.Components.ZonePositionComp;
import com.jharter.game.ecs.entities.EntityHandler;
import com.jharter.game.ecs.entities.IEntityHandler;
import com.jharter.game.effect.Effect;
import com.jharter.game.util.id.ID;

import uk.co.carelesslabs.Enums.ZoneType;

public class TurnAction extends EntityHandler {

	public Array<ZoneType> targetZoneTypes = new Array<ZoneType>();
	public Array<ID> targetIDs = new Array<ID>();
	public int selectedCount = 0;
	public TargetValidator targetValidator = null;
	public TurnActionMods mods = new TurnActionMods();
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
		if(selectedCount < 0 || selectedCount >= targetZoneTypes.size) {
			return ZoneType.NONE;
		}
		return targetZoneTypes.get(selectedCount);
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
		return targetZoneTypes.size == targetIDs.size && selectedCount == targetZoneTypes.size;
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

		if(targetValidator != null) {
			return targetValidator.call(entity);
		}

		return true;
	}

	private Array<ID> temp = new Array<ID>();

	public Array<ID> getAllTargetIDs() {
		return getAllTargetIDs(true, mods.all.v());
	}

	public Array<ID> getAllTargetIDs(boolean inFinalZoneOnly, boolean isAll) {
		if(!isAll || targetIDs.size == 0) {
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

			if(!isValidTarget(Comp.Entity.get(id))) {
				continue;
			}

			if(!temp.contains(id, false)) {
				temp.add(id);
			}
		}
		return temp;
	}

	public void addEffect(Effect<?> effect) {
		addEffect(effect, effects.size);
	}

	public void addEffect(Effect<?> effect, int targetIndex) {
		effects.add(effect);
		effect.setTurnAction(this);
		if(!effect.isTargetIndexSet()) {
			effect.index(Math.min(this.targetZoneTypes.size-1, targetIndex));
		}
	}

	public void perform(boolean pending) {
		if(targetIDs.size == 0) {
			return;
		}
		boolean all = getAll(pending);
		for(int i = 0; i < getMultiplicity(pending); i++) {
			for(Effect<?> effect : effects) {
				if(all && effect.getTargetIndex() == targetIDs.size - 1) {
					for(ID targetID : getAllTargetIDs(true, all)) {
						effect.perform(Comp.Entity.get(targetID), pending);
					}
				} else {
					effect.perform(pending);
				}
			}
		}
	}

	public void applyResult(Entity target, int targetIndex, boolean pending) {
		for(int i = 0; i < getMultiplicity(pending); i++) {
			for(Effect<?> effect : effects) {
				if(effect.getTargetIndex() != targetIndex) {
					continue;
				}
				effect.applyResult(target, pending);
			}
		}
	}

	private int getMultiplicity(boolean pending) {
		return pending ? mods.multiplicity.p() : mods.multiplicity.v();
	}

	private boolean getAll(boolean pending) {
		return pending ? mods.all.p() : mods.all.v();
	}

	public void reset() {
		targetZoneTypes.clear();
		targetIDs.clear();
		targetValidator = null;
		mods.clear();
		priority = 0;
		makesTargetAll = false;
		makesTargetMultiplicity = 1;
		entityID = null;
		selectedCount = 0;
	}

}
