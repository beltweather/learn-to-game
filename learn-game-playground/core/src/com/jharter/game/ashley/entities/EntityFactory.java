package com.jharter.game.ashley.entities;

import com.badlogic.ashley.core.PooledEngine;
import com.jharter.game.ashley.components.CompManager;
import com.jharter.game.ashley.util.ToolBox;
import com.jharter.game.tween.CustomTweenManager;
import com.jharter.game.util.id.IDManager;

public class EntityFactory implements IEntityFactory {
	
	private ToolBox toolBox;
	private PooledEngine engine;
	protected CompManager Comp;
	
	public EntityFactory(IEntityFactory factory) {
		setFactory(factory);
	}
	
	public void setFactory(IEntityFactory factory) {
		this.engine = factory == null ? null : factory.getEngine();
		setToolBox(factory == null ? null : factory.getToolBox());
	}
	
	public PooledEngine getEngine() {
		return engine;
	}
	
	public void setEngine(PooledEngine engine) {
		this.engine = engine;
	}

	public ToolBox getToolBox() {
		return toolBox;
	}
	
	public void setToolBox(ToolBox toolBox) {
		this.toolBox = toolBox;
		this.Comp = toolBox == null ? null : toolBox.getCompManager();
	}
	
	public IDManager getIDManager() {
		return toolBox == null ? null : toolBox.getIDManager();
	}
	
	public CustomTweenManager getTweenManager() {
		return toolBox == null ? null : toolBox.getTweenManager();
	}
	
}
