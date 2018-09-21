package com.jharter.game.ashley.systems.boilerplate;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.jharter.game.ashley.components.Comp;

public class CustomEntitySystem extends EntitySystem {

	private static final ImmutableArray<Entity> empty = new ImmutableArray<Entity>(new Array<Entity>());
	
	private ObjectMap<Object, Family> familiesByKey = new ObjectMap<Object, Family>();
	private ObjectMap<Object, ImmutableArray<Entity>> entityArraysByKey = new ObjectMap<Object, ImmutableArray<Entity>>();
	
	public CustomEntitySystem() {
		this(0);
	}

	public CustomEntitySystem(int priority) {
		super(priority);
	}
	
	protected void add(Object key, Family family) {
		familiesByKey.put(key, family);
	}
	
	protected ImmutableArray<Entity> getEntities(Object key) {
		if(entityArraysByKey.containsKey(key)) {
			return entityArraysByKey.get(key);
		}
		return empty;
	}
	
	protected Entity getFirstEntity(Object key) {
		ImmutableArray<Entity> entityArray = getEntities(key);
		return entityArray.size() == 0 ? null : entityArray.first();
	}
	
	/**
	 * Special case where we assume the component class doubles as the key
	 */
	protected <T extends Component> T getFirstComponent(Class<T> componentClass) {
		return getFirstComponent(componentClass, componentClass);
	}
	
	protected <T extends Component> T getFirstComponent(Object key, Class<T> componentClass) {
		Entity entity = getFirstEntity(key);
		return entity == null ? null : Comp.getFor(componentClass).get(entity);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		for(Object key : familiesByKey.keys()) {
			entityArraysByKey.put(key, engine.getEntitiesFor(familiesByKey.get(key)));
		}
	}
	
	@Override
	public void removedFromEngine(Engine engine) {
		entityArraysByKey.clear();
	}
	
}
