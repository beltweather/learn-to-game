package com.jharter.game.ecs.systems.boilerplate;

import java.util.Comparator;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.jharter.game.ecs.components.CompManager;
import com.jharter.game.ecs.components.Components.ZoneComp;
import com.jharter.game.ecs.entities.GameToolBox;
import com.jharter.game.ecs.entities.IEntityHandler;
import com.jharter.game.tween.GameTweenManager;
import com.jharter.game.util.id.ID;
import com.jharter.game.util.id.IDManager;

import uk.co.carelesslabs.Enums.ZoneType;

public abstract class GameEntitySystem extends EntitySystem implements IEntityHandler {
	
	public static Class<? extends Component>[] combine(Class<? extends Component>[] additionalComps, Class<? extends Component>...defaultComps) {
		Class<?>[] combined = new Class<?>[additionalComps.length + defaultComps.length];
		for(int i = 0; i < additionalComps.length; i++) {
			combined[i] = additionalComps[i];
		}
		for(int i = additionalComps.length; i < combined.length; i++) {
			combined[i] = defaultComps[i - additionalComps.length];
		}
		return (Class<? extends Component>[]) combined;
	}
	
	private static final Array<Entity> empty = new Array<Entity>();
	private static final ImmutableArray<Entity> emptyImm = new ImmutableArray<Entity>(new Array<Entity>());
	
	private boolean hasEvents = false;
	private boolean shouldSort = false;
	private ObjectMap<Object, Family> familiesByKey = new ObjectMap<Object, Family>();
	private ObjectMap<Object, ImmutableArray<Entity>> entityArraysByKey = new ObjectMap<Object, ImmutableArray<Entity>>();
	private ObjectMap<Object, Comparator<Entity>> comparatorsByKey = new ObjectMap<Object, Comparator<Entity>>();
	private ObjectMap<Object, Array<Entity>> sortedEntityArraysByKey = new ObjectMap<Object, Array<Entity>>();
	private Array<Class<? extends Component>> events = new Array<Class<? extends Component>>();
	private GameToolBox toolBox;
	protected CompManager Comp;
	
	public GameEntitySystem() {
		this(0);
	}

	public GameEntitySystem(int priority) {
		super(priority);
	}
	
	@Override
	public void update(float deltaTime) {
		shouldSort = true;
		clearEvents();
		performUpdate(deltaTime);
	}
	
	public abstract void performUpdate(float deltaTime);
	
	@Override
	public GameToolBox getToolBox() {
		return toolBox;
	}
	
	@Override
	public PooledEngine getEngine() {
		return (PooledEngine) super.getEngine();
	}
	
	public void setToolBox(GameToolBox toolBox) {
		this.toolBox = toolBox;
		Comp = toolBox.getCompManager();
		for(Class<? extends Component> event : events) {
			toolBox.registerEvent(event);
		}
	}
	
	@Override
	public CompManager getCompManager() {
		return toolBox.getCompManager();
	}
	
	@Override
	public IDManager getIDManager() {
		return toolBox.getIDManager();
	}
	
	@Override
	public GameTweenManager getTweenManager() {
		return toolBox.getTweenManager();
	}
	
	protected void add(Class<? extends Component> componentClass) {
		add(componentClass, Family.all(componentClass).get());
	}

	protected void add(Object key, Family family) {
		add(key, family, null);
	}
	
	protected void add(Object key, Family family, Comparator<Entity> comparator) {
		familiesByKey.put(key, family);
		if(comparator != null) {
			comparatorsByKey.put(key, comparator);
		}
	}
	
	protected ImmutableArray<Entity> entities(Object key) {
		if(entityArraysByKey.containsKey(key)) {
			return entityArraysByKey.get(key);
		}
		return emptyImm;
	}
	
	protected Array<Entity> entitiesSorted(Object key) {
		if(shouldSort) {
			sortAddedEntities();
			shouldSort = false;
		}
		if(sortedEntityArraysByKey.containsKey(key)) {
			return sortedEntityArraysByKey.get(key);
		}
		return empty;
	}
	
	protected int countEntities(Object key) {
		return entities(key).size();
	}
	
	protected boolean hasEntities(Object key) {
		return entities(key).size() > 0;
	}
	
	protected void clearComps(Class<? extends Component> compClass) {
		for(Entity entity : entities(compClass)) {
			Comp.remove(compClass, entity);
		}
	}
	
	/**
	 * All component classes in this list will be treated like events with this
	 * system as the source and sink. The way events work is they are boolean
	 * components that are added to entities in this system and removed from
	 * entities the next time this system is invoked. This means they live for
	 * exactly one tick.
	 */
	protected void event(Class<? extends Component> eventClass) {
		events.add(eventClass);
		add(eventClass);
		hasEvents = true;
	}
	
	protected void clearEvents() {
		if(!hasEvents) {
			return;
		}
		for(Class<? extends Component> eventClass : events) {
			clearComps(eventClass);
		}
	}
	
	protected ID getZoneID(ID ownerID, ZoneType type) {
		return getIDManager().getZoneID(ownerID, type);
 	}
	
	protected ZoneComp getZone(ID ownerID, ZoneType zoneType) {
		ID zoneID = getZoneID(ownerID, zoneType);
		if(zoneID == null) {
			return null;
		}
		Entity zone = Comp.Entity.get(zoneID);
		if(zone == null) {
			return null;
		}
		return Comp.ZoneComp.get(zone);
	}
	
	protected ImmutableArray<ID> getPlayerIDs() {
		return getIDManager().getPlayerIDs();
	}
	
	/**
	 * Special case where we assume the component class doubles as the key
	 */
	protected <T extends Component> ImmutableArray<T> comps(Class<T> componentClass) {
		return comps(componentClass, componentClass);
	}
	
	protected <T extends Component> ImmutableArray<T> comps(Object key, Class<T> componentClass) {
		ImmutableArray<Entity> entities = entities(key);
		Array<T> comps = new Array<T>(false, entities.size());
		for(int i = 0; i < entities.size(); i++) {
			comps.add(Comp.getFor(componentClass).get(entities.get(i)));
		}
		return new ImmutableArray<T>(comps);
	}
	
	protected Entity entity(Object key) {
		ImmutableArray<Entity> entityArray = entities(key);
		return entityArray.size() == 0 ? null : entityArray.first();
	}
	
	protected Entity entitySorted(Object key) {
		Array<Entity> entityArray = entitiesSorted(key);
		return entityArray.size == 0 ? null : entityArray.first();
	}
	
	/**
	 * Special case where we assume the component class doubles as the key
	 */
	protected <T extends Component> T comp(Class<T> componentClass) {
		return comp(componentClass, componentClass);
	}
	
	protected <T extends Component> T comp(Object key, Class<T> componentClass) {
		Entity entity = entity(key);
		return entity == null ? null : Comp.getFor(componentClass).get(entity);
	}
	
	protected <T extends Component> T compSorted(Class<T> componentClass) {
		return compSorted(componentClass, componentClass);
	}
	
	protected <T extends Component> T compSorted(Object key, Class<T> componentClass) {
		Entity entity = entitySorted(key);
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
		sortedEntityArraysByKey.clear();
	}
	
	private void sortAddedEntities() {
		for(Object key : comparatorsByKey.keys()) {
			if(!entityArraysByKey.containsKey(key)) {
				continue;
			}
			if(!sortedEntityArraysByKey.containsKey(key)) {
				sortedEntityArraysByKey.put(key, new Array<Entity>(false, 16));
			}
			Array<Entity> sortedArray = sortedEntityArraysByKey.get(key);
			sortedArray.clear();
			for(Entity entity : entityArraysByKey.get(key)) {
				sortedArray.add(entity);
			}
			sortedArray.sort(comparatorsByKey.get(key));
		}
	}
}
