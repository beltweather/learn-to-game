package com.jharter.game.ashley.components.subcomponents;

public abstract class Callback<T> {

	public Callback() {}
	
	public abstract void call(T t);
	
}
