package com.jharter.game.ashley.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;
import com.jharter.game.ashley.components.CompWrappers.CompWrapper;

public class CompContext {
	
	private Array<Class<? extends Component>> compClasses;
	
	public CompContext(Class<? extends Component>... compClasses) {
		this.compClasses = new Array<Class<? extends Component>>(compClasses);
	}
	
	public <T extends Component> CompWrapper<T> Wrap(T comp) {
		if(!compClasses.contains(comp.getClass(), true)) {
			return null;
		}
		return null;
		//return Comp.Wrap(comp);
	}

}
