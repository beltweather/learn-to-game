package com.jharter.game.effect;

import com.badlogic.ashley.core.Entity;

public class AllEffect extends Effect<Boolean> {

	public AllEffect() {
		super(EffectProp.ALL);
	}

	@Override
	public Boolean getResult(Entity performer, Entity activeCard) {
		//Comp.TurnActionComp.get(activeCard).turnAction.all = true;
		return true;
	}

	@Override
	public void handleAudioVisual(Entity performer, Entity target) {

	}

}
