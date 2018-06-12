package com.jharter.game.ashley.entities;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.ObjectMap;
import com.jharter.game.ashley.components.Components.BodyComp;
import com.jharter.game.ashley.components.Components.IDComp;
import com.jharter.game.ashley.components.Components.InvisibleComp;
import com.jharter.game.ashley.components.Components.PositionComp;
import com.jharter.game.ashley.components.Components.SensorComp;
import com.jharter.game.ashley.components.Components.TextureComp;
import com.jharter.game.ashley.components.EntityBuilder;
import com.jharter.game.ashley.components.Mapper;
import com.jharter.game.control.Input;
import com.jharter.game.util.id.ID;

import uk.co.carelesslabs.Enums.EntityType;
import uk.co.carelesslabs.box2d.Box2DHelper;
import uk.co.carelesslabs.box2d.Box2DWorld;

public class EntityUtil {
	
	private EntityUtil() {}
	
	private static final ObjectMap<ID, Entity> entitiesById = new ObjectMap<ID, Entity>();
	
	public static void addIdListener(PooledEngine engine, final Box2DWorld box2D) {
		engine.addEntityListener(Family.all(IDComp.class).get(), new EntityListener() {
			
			private ComponentMapper<IDComp> im = ComponentMapper.getFor(IDComp.class);
			
			@Override
			public void entityAdded(Entity entity) {
				IDComp idComp = im.get(entity);
				if(idComp.id != null) {
					entitiesById.put(idComp.id, entity);
				}
			}

			@Override
			public void entityRemoved(Entity entity) {
				IDComp idComp = im.get(entity);
				BodyComp b = Mapper.BodyComp.get(entity);
				SensorComp s = Mapper.SensorComp.get(entity);
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
	
	public static Entity findEntity(ID id) {
		if(entitiesById.containsKey(id)) {
			return entitiesById.get(id);
		}
		return null;
	}
	
	private static Body buildBody(Vector3 position, float width, float height, Box2DWorld world, BodyType bodyType) {
		return Box2DHelper.createBody(world.world, width/2, height/2, width/4, 0, position, bodyType);  
	}
	
	public static EntityBuilder buildBasicEntity(PooledEngine engine) {
		EntityBuilder b = EntityBuilder.create(engine);
		
		b.IDComp();
		b.PositionComp();
		b.SizeComp();
		b.TypeComp();
		b.RemoveComp();
		
		return b;
	}
	
	public static EntityBuilder buildBasicEntity(PooledEngine engine, ID id, EntityType type, Vector3 position, float width, float height, TextureRegion texture) {
		EntityBuilder b = buildBasicEntity(engine);
		
		b.IDComp().id = id;
		b.TypeComp().type = type;
		b.PositionComp().position = position;
		b.SizeComp().width = width;
		b.SizeComp().height = height;
		b.VisualComp().defaultRegion = texture;
		
		return b;
	}
	
	public static EntityBuilder buildStaticSprite(PooledEngine engine, ID id, EntityType type, Vector3 position, float width, float height, TextureRegion texture, Box2DWorld world, BodyType bodyType) {
		EntityBuilder b = buildBasicEntity(engine, id, type, position, width, height, texture);
		
		b.BodyComp().body = buildBody(position, width, height, world, bodyType);
		b.BodyComp().body.setUserData(id);
		b.CollisionComp();
		
		return b;
	}
	
	public static EntityBuilder buildDynamicSprite(PooledEngine engine, ID id, EntityType type, Vector3 position, float width, float height, TextureRegion texture, Box2DWorld world, BodyType bodyType, float speed) {
		EntityBuilder b = buildStaticSprite(engine, id, type, position, width, height, texture, world, bodyType);
		
		b.VelocityComp().speed = speed;
		b.TargetPositionComp();

		return b;
	}
	
	public static EntityBuilder buildPlayerSprite(PooledEngine engine, ID id, EntityType type, Vector3 position, float width, float height, TextureRegion texture, Box2DWorld world, float speed, Input input) {
		EntityBuilder b = buildDynamicSprite(engine, id, type, position, width, height, texture, world, BodyType.DynamicBody, speed);
		
		b.InputComp().input = input;
		b.InteractComp();
		b.PlayerComp();
		
		return b;
	}
	
	private static final Family renderFamily = Family.all(PositionComp.class, TextureComp.class).exclude(InvisibleComp.class).get();
	
}
