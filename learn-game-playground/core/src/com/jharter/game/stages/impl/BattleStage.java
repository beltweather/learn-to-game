package com.jharter.game.stages.impl;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.jharter.game.ashley.components.Components.ActiveCardComp;
import com.jharter.game.ashley.components.Components.TargetingComp;
import com.jharter.game.ashley.components.Components.VitalsComp;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.EntityBuilder;
import com.jharter.game.ashley.components.Mapper;
import com.jharter.game.ashley.components.subcomponents.Callback.EnemyCallback;
import com.jharter.game.ashley.components.subcomponents.Callback.FriendCallback;
import com.jharter.game.ashley.components.subcomponents.Callback.FriendEnemyCallback;
import com.jharter.game.ashley.components.subcomponents.CombatUtil;
import com.jharter.game.ashley.entities.EntityUtil;
import com.jharter.game.ashley.systems.AnimationSystem;
import com.jharter.game.ashley.systems.ApproachTargetSystem;
import com.jharter.game.ashley.systems.CleanupInputSystem;
import com.jharter.game.ashley.systems.CollisionSystem;
import com.jharter.game.ashley.systems.CursorActionSystem;
import com.jharter.game.ashley.systems.CursorInputSystem;
import com.jharter.game.ashley.systems.CursorMoveSystem;
import com.jharter.game.ashley.systems.CursorPositionSystem;
import com.jharter.game.ashley.systems.CursorTargetingSystem;
import com.jharter.game.ashley.systems.InteractSystem;
import com.jharter.game.ashley.systems.RemoveEntitiesSystem;
import com.jharter.game.ashley.systems.RenderEntitiesSystem;
import com.jharter.game.ashley.systems.RenderInitSystem;
import com.jharter.game.ashley.systems.RenderTilesSystem;
import com.jharter.game.ashley.systems.UpdatePhysicsSystem;
import com.jharter.game.ashley.systems.VelocityMovementSystem;
import com.jharter.game.ashley.systems.network.client.ClientAddPlayersPacketSystem;
import com.jharter.game.ashley.systems.network.client.ClientRandomMovementSystem;
import com.jharter.game.ashley.systems.network.client.ClientRemoveEntityPacketSystem;
import com.jharter.game.ashley.systems.network.client.ClientSendInputSystem;
import com.jharter.game.ashley.systems.network.client.ClientSnapshotPacketSystem;
import com.jharter.game.ashley.systems.network.offline.OfflineSelectInputSystem;
import com.jharter.game.ashley.systems.network.server.ServerInputPacketSystem;
import com.jharter.game.ashley.systems.network.server.ServerRegisterPlayerPacketSystem;
import com.jharter.game.ashley.systems.network.server.ServerRequestEntityPacketSystem;
import com.jharter.game.ashley.systems.network.server.ServerSendSnapshotSystem;
import com.jharter.game.debug.Debug;
import com.jharter.game.network.endpoints.EndPointHelper;
import com.jharter.game.network.endpoints.GameClient;
import com.jharter.game.network.endpoints.GameServer;
import com.jharter.game.stages.GameStage;
import com.jharter.game.util.id.ID;

import uk.co.carelesslabs.Enums.EntityType;
import uk.co.carelesslabs.Enums.ZoneType;
import uk.co.carelesslabs.Media;

public class BattleStage extends GameStage {
	
	public static final int CARD_WIDTH = 72;
	public static final int CARD_HEIGHT = 100;
	
	public BattleStage(EndPointHelper endPointHelper) {
		super(endPointHelper);
	}

	@Override
	public void addEntities(PooledEngine engine) {
		EntityBuilder b = EntityUtil.buildBasicEntity(engine, 
													  EntityType.BACKGROUND, 
													  new Vector3(-1920/2,-1080/2,-1), 
													  Media.background);
		engine.addEntity(b.Entity());
		b.free();
		
		// Zones
		b = EntityBuilder.create(engine);
		b.IDComp().id = Mapper.ZoneComp.getID(ZoneType.HAND);
		b.ZoneComp().zoneType = ZoneType.HAND;
		b.ZoneComp().rows = 1;
		b.ZoneComp().cols = 4;
		ZoneComp handZone = b.ZoneComp();
		engine.addEntity(b.Entity());
		b.free();

		b = EntityBuilder.create(engine);
		b.IDComp().id = Mapper.ZoneComp.getID(ZoneType.FRIEND);
		b.ZoneComp().zoneType = ZoneType.FRIEND;
		b.ZoneComp().rows = 4;
		b.ZoneComp().cols = 1;
		ZoneComp friendZone = b.ZoneComp();
		engine.addEntity(b.Entity());
		b.free();
		
		b = EntityBuilder.create(engine);
		b.IDComp().id = Mapper.ZoneComp.getID(ZoneType.ENEMY);
		b.ZoneComp().zoneType = ZoneType.ENEMY;
		b.ZoneComp().rows = 1;
		b.ZoneComp().cols = 1;
		ZoneComp enemyZone = b.ZoneComp();
		engine.addEntity(b.Entity());
		b.free();
		
		// CHARACTERS

		b = EntityUtil.buildBasicEntity(engine, 
				  EntityType.CHARACTER, 
				  new Vector3(660,140,0), 
				  Media.warrior);
		ID warriorID = b.IDComp().id;
		b.VitalsComp().maxHealth = 100;
		b.VitalsComp().weakHealth = 25;
		b.VitalsComp().health = 10;
		b.StatsComp().level = 1;
		b.StatsComp().power = 10;
		b.StatsComp().defense = 10;
		b.StatsComp().mPower = 2;
		b.StatsComp().mDefense = 2;
		b.DescriptionComp().name = "Warrior";
		friendZone.add(b);
		engine.addEntity(b.Entity());
		b.free();
		
		b = EntityUtil.buildBasicEntity(engine, 
				  EntityType.CHARACTER, 
				  new Vector3(750,15,0), 
				  Media.rogue);
		ID rogueID = b.IDComp().id;
		ActiveCardComp rogueActiveCardComp = b.ActiveCardComp();
		b.VitalsComp().maxHealth = 100;
		b.VitalsComp().weakHealth = 25;
		b.VitalsComp().health = 47;
		b.StatsComp().level = 1;
		b.StatsComp().power = 8;
		b.StatsComp().defense = 6;
		b.StatsComp().mPower = 5;
		b.StatsComp().mDefense = 6;
		b.DescriptionComp().name = "Rogue";
		friendZone.add(b);
		engine.addEntity(b.Entity());
		b.free();
		
		b = EntityUtil.buildBasicEntity(engine, 
				  EntityType.CHARACTER, 
				  new Vector3(675,-120,0), 
				  Media.sorcerer);
		ID sorcererID = b.IDComp().id;
		b.VitalsComp().maxHealth = 100;
		b.VitalsComp().weakHealth = 25;
		b.VitalsComp().health = 67;
		b.StatsComp().level = 1;
		b.StatsComp().power = 2;
		b.StatsComp().defense = 2;
		b.StatsComp().mPower = 10;
		b.StatsComp().mDefense = 10;
		b.DescriptionComp().name = "Sorcerer";
		friendZone.add(b);
		engine.addEntity(b.Entity());
		b.free();
		
		b = EntityUtil.buildBasicEntity(engine, 
				  EntityType.CHARACTER, 
				  new Vector3(750,-255,0), 
				  Media.ranger);
		ID rangerID = b.IDComp().id;
		b.VitalsComp().maxHealth = 100;
		b.VitalsComp().weakHealth = 25;
		b.VitalsComp().health = 80;
		b.StatsComp().level = 1;
		b.StatsComp().power = 9;
		b.StatsComp().defense = 8;
		b.StatsComp().mPower = 3;
		b.StatsComp().mDefense = 4;
		b.DescriptionComp().name = "Ranger";
		friendZone.add(b);
		engine.addEntity(b.Entity());
		b.free();
		
		// ENEMIES
		
		b = EntityUtil.buildBasicEntity(engine, 
				  EntityType.ENEMY, 
				  new Vector3(-750,-100,0), 
				  Media.atma);
		b.VitalsComp().maxHealth = 500;
		b.VitalsComp().weakHealth = 50;
		b.VitalsComp().health = 500;
		b.DescriptionComp().name = "Atma";
		b.StatsComp().level = 3;
		b.StatsComp().power = 20;
		b.StatsComp().defense = 15;
		b.StatsComp().mPower = 10;
		b.StatsComp().mDefense = 10;
		b.SizeComp().scale = new Vector2(2f,2f);
		enemyZone.add(b);
		engine.addEntity(b.Entity());
		b.free();
		
		// Cards
		
		b = EntityUtil.buildBasicEntity(engine, 
										EntityType.CARD, 
										new Vector3(-700,-475,0), 
										Media.plains);
		b.CardComp().ownerID = warriorID;
		b.DescriptionComp().name = "Plains";
		new FriendEnemyCallback(b) {

			@Override
			public void call(Entity owner, Entity card, Entity friend, Entity enemy) {
				int damage = CombatUtil.getDamage(owner, enemy, 13);
				
				VitalsComp vEnemy = Mapper.VitalsComp.get(enemy);
				VitalsComp vFriend = Mapper.VitalsComp.get(friend);
				int origHealthFriend = vFriend.health;
				int origHealthEnemy = vEnemy.health;
				
				vEnemy.damage(damage);
				vFriend.heal(damage);
				
				// DEBUG PRINTING
				int healed = vFriend.health - origHealthFriend;
				int damaged = origHealthEnemy - vEnemy.health; 
				
				String nameFriend = Mapper.DescriptionComp.get(friend).name;
				String nameEnemy = Mapper.DescriptionComp.get(enemy).name;
				
				System.out.println("Deal damage and heal friend for amount:");
				System.out.println(nameEnemy + " received " + damaged + " damage.");
				System.out.println(nameFriend + " received " + healed + " health.");
				
				System.out.println(nameEnemy + " hp: " + vEnemy.health);
				System.out.println(nameFriend + " hp: " + vFriend.health);
				
				if(vEnemy.isNearDeath()) {
					System.out.println(nameEnemy + " is near death.");
				} else if(vEnemy.isDead()) {
					System.out.println(nameEnemy + " is dead.");
				}
			}
			
		};
		handZone.add(b);
		engine.addEntity(b.Entity());
		b.free();
		
		b = EntityUtil.buildBasicEntity(engine, 
				EntityType.CARD, 
				new Vector3(-450,-475,0), 
				Media.swamp);
		ID swampId = b.IDComp().id;
		b.CardComp().ownerID = warriorID;
		b.DescriptionComp().name = "Swamp";
		new EnemyCallback(b) {

			@Override
			public void call(Entity owner, Entity card, Entity enemy) {
				int damage = CombatUtil.getDamage(owner, enemy, 20);
				Mapper.VitalsComp.get(enemy).damage(damage);
				System.out.println("Dealt " + damage + " damage.");
			}
			
		};
		handZone.add(b);
		engine.addEntity(b.Entity());
		b.free();
		
		rogueActiveCardComp.activeCardID = swampId;
		
		b = EntityUtil.buildBasicEntity(engine, 
				EntityType.CARD, 
				new Vector3(-200,-475,0), 
				Media.island);
		b.CardComp().ownerID = warriorID;
		b.DescriptionComp().name = "Island";
		new FriendCallback(b) {

			@Override
			public void call(Entity owner, Entity card, Entity friend) {
				ActiveCardComp aFriend = Mapper.ActiveCardComp.get(friend);
				if(aFriend != null && aFriend.activeCardID != null) {
					Entity friendCard = Mapper.Entity.get(aFriend.activeCardID);
					TargetingComp tFriend = Mapper.TargetingComp.get(friendCard);
					tFriend.multiplicity++;
				}
			}
			
		};
		b.TargetingComp().debugMustHaveCard = true;
		handZone.add(b);
		engine.addEntity(b.Entity());
		b.free();
		
		b = EntityUtil.buildBasicEntity(engine, 
				EntityType.CARD, 
				new Vector3(50,-475,0), 
				Media.mountain);
		b.CardComp().ownerID = warriorID;
		b.DescriptionComp().name = "Mountain";
		b.TargetingComp().defaultAll = true;
		b.TargetingComp().all = true;
		new FriendCallback(b) {

			@Override
			public void call(Entity owner, Entity card, Entity friend) {
				int hp = 50;
				Mapper.VitalsComp.get(friend).heal(hp);
				System.out.println("Healed " + hp + " hp to " + Mapper.DescriptionComp.get(friend).name);
			}
			
		};
		handZone.add(b);
		engine.addEntity(b.Entity());
		b.free();
		
	}
	
	@Override
	protected OrthographicCamera buildCamera() {
		OrthographicCamera camera = super.buildCamera();
		camera.zoom = 1f;
		return camera;
	}
	
	@Override
	public EntityBuilder addPlayerEntity(ID id, Vector3 position, boolean focus) {
		EntityBuilder b = EntityUtil.buildBasicEntity(engine, 
				  EntityType.HAND, 
				  new Vector3(-550,-100,1), 
				  Media.handPointDown);
		b.CursorComp();
		b.CursorInputRegulatorComp();
		b.CursorInputComp();
		b.InputComp().input = buildInput(focus);
		b.ZonePositionComp().zoneType = ZoneType.HAND;
		b.ZonePositionComp().row = 0;
		b.ZonePositionComp().col = 0;
		if(focus) {
			b.FocusComp();
		}
		engine.addEntity(b.Entity());
		
		return b;
	}

	@Override
	public Vector3 getEntryPoint() {
		return new Vector3(0,0,0);
	}
	
	@Override
	protected PooledEngine buildEngine() {
    	PooledEngine engine = new PooledEngine();
		Mapper.addIdListener(engine, getBox2DWorld());
		
		if(endPointHelper.isOffline()) {
			engine.addSystem(new OfflineSelectInputSystem());
		}
		
		if(endPointHelper.isServer()) {
			
			GameServer server = endPointHelper.getServer();
			engine.addSystem(new ServerSendSnapshotSystem(server));
			engine.addSystem(new ServerInputPacketSystem(this, server));
			engine.addSystem(new ServerRegisterPlayerPacketSystem(this, server));
			engine.addSystem(new ServerRequestEntityPacketSystem(this, server));
			
		} else if(endPointHelper.isClient()){

			GameClient client = endPointHelper.getClient();
			if(Debug.RANDOM_MOVEMENT) engine.addSystem(new ClientRandomMovementSystem());
			engine.addSystem(new ClientSendInputSystem(client));
			engine.addSystem(new ClientSnapshotPacketSystem(this, client));
			engine.addSystem(new ClientAddPlayersPacketSystem(this, client));
			engine.addSystem(new ClientRemoveEntityPacketSystem(this, client));
		
		}
		
		engine.addSystem(new UpdatePhysicsSystem(this));
		
		// Used in battle demo
		engine.addSystem(new CollisionSystem()); 
		engine.addSystem(new CursorInputSystem());
		engine.addSystem(new CursorMoveSystem());
		engine.addSystem(new CursorTargetingSystem());
		engine.addSystem(new CursorActionSystem());
		engine.addSystem(new CursorPositionSystem());
		
		// Used in movement demo
		//engine.addSystem(new InputMovementSystem());
		
		engine.addSystem(new VelocityMovementSystem());
		engine.addSystem(new ApproachTargetSystem());
		engine.addSystem(new InteractSystem());
		
		if(!endPointHelper.isHeadless()) {
			engine.addSystem(new AnimationSystem());
			
			// Used in movement demo
			//engine.addSystem(new CameraSystem(getCamera()));
			
			engine.addSystem(new RenderInitSystem());
			engine.addSystem(new RenderTilesSystem(getCamera()));
			engine.addSystem(new RenderEntitiesSystem(getCamera()));
		}
		
		engine.addSystem(new RemoveEntitiesSystem(engine, endPointHelper.getClient()));
		
		/*if(endPointHelper.isClient()) {
			engine.addSystem(new AddEntitiesSystem(this, endPointHelper.getClient()));
		}*/
		
		engine.addSystem(new CleanupInputSystem(this));
		
		return engine;
    }

}
