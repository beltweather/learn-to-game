package com.jharter.game.ecs.components.subcomponents;

public interface Pendable<T> {

	public T setFrom(T other);

	public T clear();

}
