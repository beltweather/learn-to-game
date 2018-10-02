package com.jharter.game.ashley.util;

import com.badlogic.ashley.core.PooledEngine;
import com.jharter.game.ashley.components.CompManager;
import com.jharter.game.ashley.entities.IEntityFactory;
import com.jharter.game.tween.CustomTweenManager;
import com.jharter.game.util.id.IDManager;

public class ToolBox implements IEntityFactory {
	
	private PooledEngine engine;
	private CompManager compManager;
	private IDManager idManager;
	private CustomTweenManager tweenManager;
	
	public ToolBox(PooledEngine engine) {
		this.engine = engine;
		this.compManager = new CompManager();
		this.idManager = new IDManager();
		this.tweenManager = new CustomTweenManager(this);
	}
	
	public CompManager getCompManager() {
		return compManager;
	}
	
	public IDManager getIDManager() {
		return idManager;
	}
	
	public CustomTweenManager getTweenManager() {
		return tweenManager;
	}

	@Override
	public PooledEngine getEngine() {
		return engine;
	}

	@Override
	public ToolBox getToolBox() {
		return this;
	}
	
}
