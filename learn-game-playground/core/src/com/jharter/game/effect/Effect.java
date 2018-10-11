package com.jharter.game.effect;

import com.badlogic.ashley.core.Entity;
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
	
	public void setTargetIndex(int targetIndex) {
		this.targetIndex = targetIndex;
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
	
	public T perform() {
		return perform(getTarget());
	}
	
	public T perform(Entity target) {
		return perform(getPerformer(), target);
	}
 	
	public abstract T perform(Entity performer, Entity target);
	
}
