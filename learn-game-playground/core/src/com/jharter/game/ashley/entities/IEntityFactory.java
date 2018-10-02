package com.jharter.game.ashley.entities;

import com.badlogic.ashley.core.PooledEngine;
import com.jharter.game.ashley.util.ToolBox;

public interface IEntityFactory {
	
	public PooledEngine getEngine();
	public ToolBox getToolBox();
	
}
