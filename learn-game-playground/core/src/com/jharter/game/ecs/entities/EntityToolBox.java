package com.jharter.game.ecs.entities;

import com.badlogic.ashley.core.PooledEngine;
import com.jharter.game.ecs.components.CompManager;
import com.jharter.game.tween.GameTweenManager;
import com.jharter.game.util.id.IDManager;

public class EntityToolBox implements IEntityHandler {
	
	private PooledEngine engine;
	private CompManager compManager;
	private IDManager idManager;
	private GameTweenManager tweenManager;
	
	public EntityToolBox(PooledEngine engine) {
		this.engine = engine;
		this.compManager = new CompManager(this);
		this.idManager = new IDManager();
		this.tweenManager = new GameTweenManager(this);
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
	public GameTweenManager getTweenManager() {
		return tweenManager;
	}

}
