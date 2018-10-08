package com.jharter.game.ecs.entities;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.utils.Array;
import com.jharter.game.ecs.components.CompManager;
import com.jharter.game.tween.GameTweenManager;
import com.jharter.game.util.Sys;
import com.jharter.game.util.id.IDManager;

public class GameToolBox implements IEntityHandler {
	
	private PooledEngine engine;
	private CompManager compManager;
	private IDManager idManager;
	private GameTweenManager tweenManager;
	private Array<Class<? extends Component>> registeredEvents = new Array<Class<? extends Component>>();
	
	public GameToolBox(PooledEngine engine) {
		this.engine = engine;
		this.compManager = new CompManager(this);
		this.idManager = new IDManager();
		this.tweenManager = new GameTweenManager(this);
	}
	
	@Override
	public GameToolBox getToolBox() {
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
	
	public void registerEvent(Class<? extends Component> compClass) {
		if(registeredEvents.contains(compClass, false)) {
			Sys.err.println("Warning: Tried to register the same component as an event twice (" + compClass.getSimpleName() + ")");
			Sys.err.println("Make sure only one active system registerd this event, otherwise it won't make a roundtrip before being removed");
			return;
		}
		registeredEvents.add(compClass);
	}

}
