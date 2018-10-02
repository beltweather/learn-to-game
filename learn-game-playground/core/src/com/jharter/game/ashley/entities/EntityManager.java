package com.jharter.game.ashley.entities;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.utils.ObjectMap;
import com.jharter.game.ashley.components.CompManager;
import com.jharter.game.ashley.components.Components;
import com.jharter.game.ashley.components.Components.BodyComp;
import com.jharter.game.ashley.components.Components.IDComp;
import com.jharter.game.ashley.components.Components.RemoveComp;
import com.jharter.game.ashley.components.Components.SensorComp;
import com.jharter.game.util.id.ID;

import uk.co.carelesslabs.box2d.Box2DWorld;

/**
 * Used to retrieve entities directly, the sibling of "C" the mapper and "L" the linker
 */
public class EntityManager {
	
	private CompManager Comp;
	
	public EntityManager(CompManager Comp) {
		this.Comp = Comp;
	}

	private final ObjectMap<ID, Entity> entitiesById = new ObjectMap<ID, Entity>();
	
	public void addIdListener(Engine engine, final Box2DWorld box2D) {
		engine.addEntityListener(Family.all(IDComp.class).get(), new EntityListener() {
			
			@Override
			public void entityAdded(Entity entity) {
				IDComp idComp = Comp.IDComp.get(entity);
				if(idComp.id != null) {
					entitiesById.put(idComp.id, entity);
				}
			}

			@Override
			public void entityRemoved(Entity entity) {
				IDComp idComp = Comp.IDComp.get(entity);
				BodyComp b = Comp.BodyComp.get(entity);
				SensorComp s = Comp.SensorComp.get(entity);
				if(b != null && b.body != null) {
					box2D.world.destroyBody(b.body);
				}
				if(s != null && s.sensor != null) {
					box2D.world.destroyBody(s.sensor);
				}
				if(idComp.id != null) {
					entitiesById.remove(idComp.id);
				}
			}
			
		});
	}
	
	public Entity get(ID id) {
		if(id == null) {
			return null;
		}
		if(entitiesById.containsKey(id)) {
			return entitiesById.get(id);
		}
		return null;
	}
	
	public void remove(Engine engine, ID id) {
		remove(engine, get(id));
	}
	
	public void remove(Engine engine, Entity entity) {
		if(entity == null) {
			return;
		}
		Comp.getOrAdd(engine, RemoveComp.class, entity);
	}
		
}
