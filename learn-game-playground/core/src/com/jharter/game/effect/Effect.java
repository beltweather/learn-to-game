package com.jharter.game.effect;

import com.badlogic.ashley.core.Entity;
import com.jharter.game.ecs.components.Components.VitalsComp;
import com.jharter.game.ecs.components.subcomponents.TurnAction;
import com.jharter.game.ecs.entities.EntityHandler;

public abstract class Effect<T> extends EntityHandler {

	protected TurnAction turnAction;
	protected EffectProp prop;
	protected int targetIndex;
	
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
	
	public void perform() {
		applyResult();
		handleAudioVisual();
	}
	
	public void perform(Entity target) {
		applyResult(target);
		handleAudioVisual(target);
	}
	
	public void applyResult() {
		applyResult(getTarget());
	}
	
	public void applyResult(Entity target) {
		VitalsComp v = Comp.VitalsComp.get(target);
		switch(prop) {
			case DAMAGE:
				v.vitals.health -= (int) getResult(target);
				if(v.vitals.health < 0) {
					v.vitals.health = 0;
				}
				break;
			case HEAL:
				v.vitals.health += (int) getResult(target);
				if(v.vitals.health > v.vitals.maxHealth) {
					v.vitals.health = v.vitals.maxHealth;
				}
			default:
				break;
		}
	}

}
