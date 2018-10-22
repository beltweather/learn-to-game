package com.jharter.game.ecs.components.subcomponents;

public interface Pendable<T> {

	public void setToDefault();

	public void resetPending();

	public void clear();

}
