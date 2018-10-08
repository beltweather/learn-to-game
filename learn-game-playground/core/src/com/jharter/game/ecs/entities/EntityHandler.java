package com.jharter.game.ecs.entities;

import com.badlogic.ashley.core.PooledEngine;
import com.jharter.game.ecs.components.CompManager;
import com.jharter.game.tween.GameTweenManager;
import com.jharter.game.util.id.IDManager;

public class EntityHandler implements IEntityHandler {
	
	private GameToolBox toolBox;
	
	/**
	 * Special case here where instead of the method
	 * "getCompManager()" we just give access to the
	 * protected Comp variable. This is so we have a
	 * much faster short-hand for accessing this since
	 * it's so widely used by entity handlers.
	 */
	protected CompManager Comp;
	
	public EntityHandler(IEntityHandler handler) {
		setHandler(handler);
	}
	
	public void setHandler(IEntityHandler handler) {
		setToolBox(handler == null ? null : handler.getToolBox());
	}
	
	private void setToolBox(GameToolBox toolBox) {
		this.toolBox = toolBox;
		this.Comp = toolBox == null ? null : toolBox.getCompManager();
	}
	
	@Override
	public GameToolBox getToolBox() {
		return toolBox;
	}
	
	@Override
	public PooledEngine getEngine() {
		return toolBox == null ? null : toolBox.getEngine();
	}
	
	@Override
	public IDManager getIDManager() {
		return toolBox == null ? null : toolBox.getIDManager();
	}

	@Override
	public GameTweenManager getTweenManager() {
		return toolBox == null ? null : toolBox.getTweenManager();
	}

	@Override
	public CompManager getCompManager() {
		return toolBox == null ? null : toolBox.getCompManager();
	}
	
}
