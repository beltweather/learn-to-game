package com.jharter.game.effect;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;
import com.jharter.game.ecs.components.subcomponents.StatusEffects;
import com.jharter.game.primitives.Array_;

import uk.co.carelesslabs.Enums.StatusEffectType;

public class StatusEffect extends Effect {

	private Array<StatusEffectType> types;
	private Array<StatusEffectType> temp = new Array<>();

	public StatusEffect(StatusEffectType...types) {
		super();
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
	protected void apply(Entity performer, Entity target, boolean pending) {
		Array_<StatusEffectType> types = getStatusEffects(target).types.beginPending(pending);
		types.v().addAll(getStatusEffectsToApply(performer, target));
		types.endPending();
	}

	protected Array<StatusEffectType> getStatusEffectsToApply(Entity performer, Entity target) {
		StatusEffects e = getStatusEffects(target);
		if(e.weakToTypes.v().size == 0 && e.resistantToTypes.v().size == 0) {
			return types;
		}
		temp.clear();
		for(StatusEffectType type : types) {
			boolean weak = e.weakToTypes.v().contains(type, true);
			boolean resist = e.resistantToTypes.v().contains(type, true);
			boolean immune = e.immuneToTypes.v().contains(type, true);

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
	protected void handleAudioVisual(Entity performer, Entity target) {

	}

}
