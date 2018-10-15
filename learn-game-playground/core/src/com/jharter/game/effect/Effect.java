package com.jharter.game.effect;

import com.badlogic.ashley.core.Entity;
import com.jharter.game.ecs.components.subcomponents.TurnAction;
import com.jharter.game.ecs.entities.EntityHandler;
import com.jharter.game.vitals.Vitals;

public abstract class Effect<T> extends EntityHandler {

	protected TurnAction turnAction;
	protected EffectProp prop;
	protected int targetIndex;
	protected boolean pending;

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

	public int getTargetIndex() {
		return targetIndex;
	}

	public Effect<T> setTargetIndex(int targetIndex) {
		this.targetIndex = targetIndex;
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

	public void applyResult(Entity target, boolean pending) {
		if(target == null || !Comp.VitalsComp.has(target)) {
			return;
		}

		Vitals v;
		if(pending) {
			if(!Comp.PendingVitalsComp.has(target)) {
				v = Comp.PendingVitalsComp.add(target).vitals.setFrom(Comp.VitalsComp.get(target).vitals);
			} else {
				v = Comp.PendingVitalsComp.get(target).vitals;
			}
		} else {
			v = Comp.VitalsComp.get(target).vitals;
		}

		switch(prop) {
			case DAMAGE:
				v.health -= (int) getResult(target);
				if(v.health < 0) {
					v.health = 0;
				}
				break;
			case HEAL:
				v.health += (int) getResult(target);
				if(v.health > v.maxHealth) {
					v.health = v.maxHealth;
				}
			default:
				break;
		}
	}

}
