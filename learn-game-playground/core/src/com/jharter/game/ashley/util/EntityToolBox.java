package com.jharter.game.ashley.util;

import com.badlogic.ashley.core.PooledEngine;
import com.jharter.game.ashley.components.CompManager;
import com.jharter.game.ashley.entities.IEntityHandler;
import com.jharter.game.tween.CustomTweenManager;
import com.jharter.game.util.id.IDManager;

public class EntityToolBox implements IEntityHandler {
	
	private PooledEngine engine;
	private CompManager compManager;
	private IDManager idManager;
	private CustomTweenManager tweenManager;
	
	public EntityToolBox(PooledEngine engine) {
		this.engine = engine;
		this.compManager = new CompManager();
		this.idManager = new IDManager();
		this.tweenManager = new CustomTweenManager(this);
	}
	
	@Override
	public EntityToolBox getToolBox() {
		return this;
	}
	
	@Override
	public PooledEngine getEngine() {
		return engine;
	}
	
	@Override
	public CompManager getCompManager() {
		return compManager;
	}

	@Override
	public IDManager getIDManager() {
		return idManager;
	}

	@Override
	public CustomTweenManager getTweenManager() {
		return tweenManager;
	}

}
