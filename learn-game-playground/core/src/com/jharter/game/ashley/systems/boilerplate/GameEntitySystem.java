package com.jharter.game.ashley.systems.boilerplate;

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
import com.jharter.game.ashley.components.CompManager;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.entities.EntityToolBox;
import com.jharter.game.ashley.entities.IEntityHandler;
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
	
	private boolean shouldSort = false;
	private ObjectMap<Object, Family> familiesByKey = new ObjectMap<Object, Family>();
	private ObjectMap<Object, ImmutableArray<Entity>> entityArraysByKey = new ObjectMap<Object, ImmutableArray<Entity>>();
	private ObjectMap<Object, Comparator<Entity>> comparatorsByKey = new ObjectMap<Object, Comparator<Entity>>();
	private ObjectMap<Object, Array<Entity>> sortedEntityArraysByKey = new ObjectMap<Object, Array<Entity>>();
	private EntityToolBox toolBox;
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
		performUpdate(deltaTime);
	}
	
	public abstract void performUpdate(float deltaTime);
	
	@Override
	public EntityToolBox getToolBox() {
		return toolBox;
	}
	
	@Override
	public PooledEngine getEngine() {
		return (PooledEngine) super.getEngine();
	}
	
	public void setToolBox(EntityToolBox toolBox) {
		this.toolBox = toolBox;
		Comp = toolBox.getCompManager();
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
	
	protected ImmutableArray<Entity> getEntities(Object key) {
		if(entityArraysByKey.containsKey(key)) {
			return entityArraysByKey.get(key);
		}
		return emptyImm;
	}
	
	protected Array<Entity> getSortedEntities(Object key) {
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
		return getEntities(key).size();
	}
	
	protected boolean hasEntities(Object key) {
		return getEntities(key).size() > 0;
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
	protected <T extends Component> ImmutableArray<T> getComponents(Class<T> componentClass) {
		return getComponents(componentClass, componentClass);
	}
	
	protected <T extends Component> ImmutableArray<T> getComponents(Object key, Class<T> componentClass) {
		ImmutableArray<Entity> entities = getEntities(key);
		Array<T> comps = new Array<T>(false, entities.size());
		for(int i = 0; i < entities.size(); i++) {
			comps.add(Comp.getFor(componentClass).get(entities.get(i)));
		}
		return new ImmutableArray<T>(comps);
	}
	
	protected Entity getFirstEntity(Object key) {
		ImmutableArray<Entity> entityArray = getEntities(key);
		return entityArray.size() == 0 ? null : entityArray.first();
	}
	
	protected Entity getFirstSortedEntity(Object key) {
		Array<Entity> entityArray = getSortedEntities(key);
		return entityArray.size == 0 ? null : entityArray.first();
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
	
	protected <T extends Component> T getFirstSortedComponent(Class<T> componentClass) {
		return getFirstSortedComponent(componentClass, componentClass);
	}
	
	protected <T extends Component> T getFirstSortedComponent(Object key, Class<T> componentClass) {
		Entity entity = getFirstSortedEntity(key);
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
