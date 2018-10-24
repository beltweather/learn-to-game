package com.jharter.game.effect;

import com.badlogic.ashley.core.Entity;
import com.jharter.game.ecs.components.subcomponents.StatusEffects;
import com.jharter.game.ecs.components.subcomponents.TurnAction;
import com.jharter.game.ecs.components.subcomponents.TurnActionMods;
import com.jharter.game.ecs.components.subcomponents.Vitals;
import com.jharter.game.ecs.entities.EntityHandler;

public abstract class Effect extends EntityHandler {

	protected TurnAction turnAction;
	protected int targetIndex;
	protected boolean pending;
	protected boolean targetIndexSet = false;

	public Effect() {
		super(null);
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

	public Effect index(int targetIndex) {
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

	public void handleAudioVisual() {
		handleAudioVisual(getTarget());
	}

	public void handleAudioVisual(Entity target) {
		handleAudioVisual(getPerformer(), target);
	}

	public void perform(boolean pending) {
		perform(getTarget(), pending);
	}

	public void perform(Entity target, boolean pending) {
		if(target == null) {
			return;
		}
		apply(getPerformer(), target, pending);
		if(!pending) {
			handleAudioVisual(target);
		}
	}

	/**
	 * Override this method to handle a special way of applying a result,
	 * as opposed to the standard "damage", "heal", "all", ect.
	 */
	protected abstract void apply(Entity performer, Entity target, boolean pending);

	protected abstract void handleAudioVisual(Entity performer, Entity target);

	protected Vitals getVitals(Entity target) {
		return Comp.VitalsComp.get(target).vitals;
	}

	protected StatusEffects getStatusEffects(Entity target) {
		return Comp.StatusEffectsComp.get(target).effects;
	}

	protected TurnActionMods getMods(Entity target) {
		return Comp.TurnActionComp.get(target).turnAction.mods;
	}

}
