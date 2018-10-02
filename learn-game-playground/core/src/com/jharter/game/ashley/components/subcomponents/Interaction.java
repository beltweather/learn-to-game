package com.jharter.game.ashley.components.subcomponents;

import com.badlogic.ashley.core.Entity;

public abstract class Interaction {
	
	public abstract void interact(Entity interactor, Entity target);

}
