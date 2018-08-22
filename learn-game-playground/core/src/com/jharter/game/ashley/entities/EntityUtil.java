package com.jharter.game.ashley.entities;

import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.jharter.game.ashley.components.Components.InvisibleComp;
import com.jharter.game.ashley.components.Components.PositionComp;
import com.jharter.game.ashley.components.Components.TextureComp;
import com.jharter.game.ashley.components.EntityBuilder;
import com.jharter.game.control.GameInput;
import com.jharter.game.util.id.ID;
import com.jharter.game.util.id.IDGenerator;

import uk.co.carelesslabs.Enums.EntityType;
import uk.co.carelesslabs.box2d.Box2DHelper;
import uk.co.carelesslabs.box2d.Box2DWorld;

public class EntityUtil {
	
	private EntityUtil() {}
	
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
	
	public static EntityBuilder buildBasicEntity(PooledEngine engine, EntityType type, Vector3 position, Texture texture) {
		return buildBasicEntity(engine, IDGenerator.newID(), type, position, new TextureRegion(texture));
	}
	
	public static EntityBuilder buildBasicEntity(PooledEngine engine, EntityType type, Vector3 position, TextureRegion texture) {
		return buildBasicEntity(engine, IDGenerator.newID(), type, position, texture.getRegionWidth(), texture.getRegionHeight(), texture);
	}
	
	public static EntityBuilder buildBasicEntity(PooledEngine engine, ID id, EntityType type, Vector3 position, TextureRegion texture) {
		return buildBasicEntity(engine, id, type, position, texture.getRegionWidth(), texture.getRegionHeight(), texture);
	}
	
	public static EntityBuilder buildBasicEntity(PooledEngine engine, ID id, EntityType type, Vector3 position, float width, float height, TextureRegion texture) {
		EntityBuilder b = buildBasicEntity(engine);
		
		b.IDComp().id = id;
		b.TypeComp().type = type;
		b.PositionComp().position = position;
		b.SizeComp().width = width;
		b.SizeComp().height = height;
		b.TextureComp().defaultRegion = texture;
		
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
	
	public static EntityBuilder buildPlayerSprite(PooledEngine engine, ID id, EntityType type, Vector3 position, float width, float height, TextureRegion texture, Box2DWorld world, float speed, GameInput input) {
		EntityBuilder b = buildDynamicSprite(engine, id, type, position, width, height, texture, world, BodyType.DynamicBody, speed);
		
		b.InputComp().input = input;
		b.InteractComp();
		b.PlayerComp();
		
		return b;
	}
	
	private static final Family renderFamily = Family.all(PositionComp.class, TextureComp.class).exclude(InvisibleComp.class).get();
	
}
