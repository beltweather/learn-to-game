package com.jharter.game.ecs.components;

import com.badlogic.ashley.core.Component;
import com.jharter.game.ecs.entities.EntityHandler;

/**
 * Wraps a component, allowing for utility methods to
 * be called upon it. This is a way of augmenting components
 * which typically should not have logic methods placed on them.
 */
public class CompUtil<T extends Component> extends EntityHandler {
	
	protected T c;
	
	public CompUtil() {
		super(null);
	}
	
	protected void setComponent(T comp) {
		this.c = comp;
	}
	
	/*protected T comp() {
		return comp;
	}*/
	
}