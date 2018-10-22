package com.jharter.game.ecs.components.subcomponents;

import com.jharter.game.primitives.int_;

public class Vitals implements Pendable<Vitals> {

	public int_ maxHealth = new int_();
	public int_ health = new int_();

	public Vitals() {

	}

	@Override
	public void setToDefault() {
		maxHealth.setToDefault();
		health.setToDefault();
	}

	@Override
	public void resetPending() {
		maxHealth.resetPending();
		health.resetPending();
	}

	@Override
	public void clear() {
		maxHealth.clear();
		health.clear();
	}

}
