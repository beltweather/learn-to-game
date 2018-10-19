package com.jharter.game.effect;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;
import com.jharter.game.ecs.components.subcomponents.StatusEffects;

import uk.co.carelesslabs.Enums.StatusEffectType;

public class StatusEffect extends Effect<Array<StatusEffectType>>{

	private Array<StatusEffectType> types;
	private Array<StatusEffectType> temp = new Array<>();

	public StatusEffect(StatusEffectType...types) {
		super(EffectProp.STATUS);
		this.types = new Array<>(types);
	}

	public StatusEffect fire() {
		types.add(StatusEffectType.FIRE);
		return this;
	}

	public StatusEffect ice() {
		types.add(StatusEffectType.ICE);
		return this;
	}

	public StatusEffect lightning() {
		types.add(StatusEffectType.LIGHTNING);
		return this;
	}

	public StatusEffect poison() {
		types.add(StatusEffectType.POISON);
		return this;
	}

	@Override
	public Array<StatusEffectType> getResult(Entity performer, Entity target) {
		StatusEffects e = getStatusEffects(target);
		if(e.weakToTypes.size == 0 && e.resistantToTypes.size == 0) {
			return types;
		}
		temp.clear();
		for(StatusEffectType type : types) {
			boolean weak = e.weakToTypes.contains(type, true);
			boolean resist = e.resistantToTypes.contains(type, true);
			boolean immune = e.immuneToTypes.contains(type, true);

			if(weak && resist && immune) {
				continue;
			} else if(resist && immune) {
				continue;
			} else if(weak && immune) {
				temp.add(type);
			} else if(weak && resist) {
				temp.add(type);
			} else if(immune) {
				continue;
			} else if(resist) {
				if(!temp.contains(type, true)) {
					temp.add(type);
				}
			} else if(weak) {
				temp.add(type);
				temp.add(type);
			} else {
				temp.add(type);
			}

		}
		return temp;
	}

	@Override
	public void handleAudioVisual(Entity performer, Entity target) {

	}

	private StatusEffects getStatusEffects(Entity target) {
		if(Comp.PendingStatusEffectsComp.has(target)) {
			return Comp.PendingStatusEffectsComp.get(target).effects;
		}
		if(Comp.StatusEffectsComp.has(target)) {
			return Comp.StatusEffectsComp.get(target).effects;
		}
		return null;
	}

}
