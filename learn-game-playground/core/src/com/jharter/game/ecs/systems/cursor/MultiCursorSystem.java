package com.jharter.game.ecs.systems.cursor;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.jharter.game.ecs.components.Components.CursorComp;

@SuppressWarnings("unchecked")
public class MultiCursorSystem extends CursorSystem {

	public MultiCursorSystem(Class<? extends Component>... cursorComps) {
		super();
	}
	
	@Override
	protected void processEntity(Entity cursor, CursorComp c, float deltaTime) {
		
	}

}
