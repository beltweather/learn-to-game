package com.jharter.game.effect;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;
import com.jharter.game.ecs.components.subcomponents.StatusEffects;
import com.jharter.game.ecs.components.subcomponents.TurnAction;
import com.jharter.game.ecs.components.subcomponents.TurnActionMods;
import com.jharter.game.ecs.components.subcomponents.Vitals;
import com.jharter.game.ecs.entities.EntityHandler;
import com.jharter.game.primitives.Array_;
import com.jharter.game.primitives.boolean_;
import com.jharter.game.primitives.int_;

import uk.co.carelesslabs.Enums.StatusEffectType;

public abstract class Effect<T> extends EntityHandler {

	protected TurnAction turnAction;
	protected EffectProp prop;
	protected int targetIndex;
	protected boolean pending;
	protected boolean targetIndexSet = false;

	public Effect(EffectProp prop) {
		super(null);
		this.prop = prop;
	}

	public EffectProp getProp() {
		return prop;
	}

	public TurnAction getTurnAction() {
		return turnAction;
	}

	public void setTurnAction(TurnAction turnAction) {
		this.turnAction = turnAction;
		setHandler(turnAction);
	}

	public boolean isTargetIndexSet() {
		return targetIndexSet;
	}

	public int getTargetIndex() {
		return targetIndex;
	}

	public Effect<T> index(int targetIndex) {
		this.targetIndex = targetIndex;
		targetIndexSet = true;
		return this;
	}

	public Entity getPerformer() {
		return turnAction.getOwnerEntity();
	}

	public Entity getTarget() {
		return getTarget(targetIndex);
	}

	public Entity getTarget(int targetIndex) {
		return turnAction.getTargetEntity(targetIndex);
	}

	public T getResult() {
		return getResult(getTarget());
	}

	public T getResult(Entity target) {
		return getResult(getPerformer(), target);
	}

	public abstract T getResult(Entity performer, Entity target);

	public void handleAudioVisual() {
		handleAudioVisual(getTarget());
	}

	public void handleAudioVisual(Entity target) {
		handleAudioVisual(getPerformer(), target);
	}

	public abstract void handleAudioVisual(Entity performer, Entity target);

	public void perform(boolean pending) {
		applyResult(pending);
		if(!pending) {
			handleAudioVisual();
		}
	}

	public void perform(Entity target, boolean pending) {
		applyResult(target, pending);
		if(!pending) {
			handleAudioVisual(target);
		}
	}

	public void applyResult(boolean pending) {
		applyResult(getTarget(), pending);
	}

	@SuppressWarnings("unchecked")
	public void applyResult(Entity target, boolean pending) {
		if(target == null) {
			return;
		}

		int_ health, maxHealth, multiplicity;
		boolean_ all;
		Array_<StatusEffectType> types;
		switch(prop) {
			case DAMAGE:
				health = getVitals(target).health.beginPending(pending);
				health.decr((int) getResult(target));
				if(health.v() < 0) {
					health.v(0);
				}
				health.endPending();
				break;
			case HEAL:
				maxHealth = getVitals(target).maxHealth;
				health = getVitals(target).health.beginPending(pending);
				health.incr((int) getResult(target));
				if(health.v() > maxHealth.v()) {
					health.v(maxHealth.v());
				}
				health.endPending();
				break;
			case ALL:
				all = getMods(target).all.beginPending(pending);
				all.v(all.v() || (boolean) getResult(target));
				all.endPending();
				break;
			case MULTIPLICITY:
				multiplicity = getMods(target).multiplicity.beginPending(pending);
				multiplicity.mult((int) getResult(target));
				multiplicity.endPending();
				break;
			case STATUS:
				types = getStatusEffects(target).types.beginPending(pending);
				types.v().addAll((Array<StatusEffectType>) getResult(target));
				types.endPending();
				break;
			default:
				break;
		}
	}

	private Vitals getVitals(Entity target) {
		return Comp.VitalsComp.get(target).vitals;
	}

	private StatusEffects getStatusEffects(Entity target) {
		return Comp.StatusEffectsComp.get(target).effects;
	}

	private TurnActionMods getMods(Entity target) {
		return Comp.TurnActionComp.get(target).turnAction.mods;
	}

}
