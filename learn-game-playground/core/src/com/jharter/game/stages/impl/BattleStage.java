package com.jharter.game.stages.impl;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.jharter.game.ashley.components.Components.ActiveCardComp;
import com.jharter.game.ashley.components.Components.DescriptionComp;
import com.jharter.game.ashley.components.Components.PlayerComp;
import com.jharter.game.ashley.components.Components.VitalsComp;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.EntityBuilder;
import com.jharter.game.ashley.components.Mapper;
import com.jharter.game.ashley.components.subcomponents.CombatUtil;
import com.jharter.game.ashley.components.subcomponents.VoidCallback.CardCallback;
import com.jharter.game.ashley.components.subcomponents.VoidCallback.EnemyCallback;
import com.jharter.game.ashley.components.subcomponents.VoidCallback.FriendCallback;
import com.jharter.game.ashley.components.subcomponents.VoidCallback.FriendEnemyCallback;
import com.jharter.game.ashley.entities.EntityUtil;
import com.jharter.game.ashley.systems.AnimationSystem;
import com.jharter.game.ashley.systems.ApproachTargetSystem;
import com.jharter.game.ashley.systems.CleanupInputSystem;
import com.jharter.game.ashley.systems.CleanupTurnActionsSystem;
import com.jharter.game.ashley.systems.CursorInputSystem;
import com.jharter.game.ashley.systems.CursorMoveSystem;
import com.jharter.game.ashley.systems.CursorSelectSystem;
import com.jharter.game.ashley.systems.CursorTargetValidationSystem;
import com.jharter.game.ashley.systems.QueueTurnActionsSystem;
import com.jharter.game.ashley.systems.RemoveEntitiesSystem;
import com.jharter.game.ashley.systems.RenderEntitiesSystem;
import com.jharter.game.ashley.systems.RenderInitSystem;
import com.jharter.game.ashley.systems.RenderTimerSystem;
import com.jharter.game.ashley.systems.TurnPhaseEndBattleSystem;
import com.jharter.game.ashley.systems.TurnPhaseEndTurnSystem;
import com.jharter.game.ashley.systems.TurnPhasePerformEnemyActionsSystem;
import com.jharter.game.ashley.systems.TurnPhasePerformFriendActionsSystem;
import com.jharter.game.ashley.systems.TurnPhaseSelectEnemyActionsSystem;
import com.jharter.game.ashley.systems.TurnPhaseSelectFriendActionsSystem;
import com.jharter.game.ashley.systems.TurnPhaseStartBattleSystem;
import com.jharter.game.ashley.systems.TurnPhaseStartTurnSystem;
import com.jharter.game.ashley.systems.TweenSystem;
import com.jharter.game.ashley.systems.ZoneLayoutSystem;
import com.jharter.game.ashley.systems.ZonePositionSystem;
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
import com.jharter.game.layout.ActiveCardLayout;
import com.jharter.game.layout.CursorPositionSystem;
import com.jharter.game.layout.HandLayout;
import com.jharter.game.layout.IdentityLayout;
import com.jharter.game.network.endpoints.EndPointHelper;
import com.jharter.game.network.endpoints.GameClient;
import com.jharter.game.network.endpoints.GameServer;
import com.jharter.game.stages.GameStage;
import com.jharter.game.util.Sys;
import com.jharter.game.util.graphics.GraphicsUtil;
import com.jharter.game.util.id.ID;
import com.jharter.game.util.id.IDGenerator;

import uk.co.carelesslabs.Enums.EntityType;
import uk.co.carelesslabs.Enums.ZoneType;
import uk.co.carelesslabs.Media;

public class BattleStage extends GameStage {
	
	public static final int CARD_WIDTH = 72;
	public static final int CARD_HEIGHT = 100;
	
	private ID rogueCharacterID;
	
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
		
		// Turn timer
		b = EntityBuilder.create(engine);
		b.IDComp().id = Mapper.getTurnEntityID();
		b.TurnTimerComp().maxTurnTimeSec = 10f;
		b.TurnPhaseComp();
		b.TurnPhaseStartBattleComp();
		b.SpriteComp().position.x = 800;
		b.SpriteComp().position.y = -400;
		b.SpriteComp().width = 100;
		b.SpriteComp().height = 100;
		b.SpriteComp();
		engine.addEntity(b.Entity());
		b.free();
		
		// Player Zones
		b = EntityBuilder.create(engine);
		b.IDComp().id = Mapper.generateZoneID(Mapper.getPlayerEntityID(), ZoneType.HAND);
		b.ZoneComp().zoneID = b.IDComp().id;
		b.ZoneComp().zoneType = ZoneType.HAND;
		b.ZoneComp().layout = new HandLayout(b.ZoneComp());
		ZoneComp handZone = b.ZoneComp();
		engine.addEntity(b.Entity());
		b.free();
		
		b = EntityBuilder.create(engine);
		b.IDComp().id = Mapper.generateZoneID(Mapper.getPlayerEntityID(), ZoneType.DISCARD);
		b.ZoneComp().zoneID = b.IDComp().id;
		b.ZoneComp().zoneType = ZoneType.DISCARD;
		b.ZoneComp().layout = new IdentityLayout(b.ZoneComp());
		ZoneComp discardCardZone = b.ZoneComp();
		engine.addEntity(b.Entity());
		b.free();
		
		// Global Zones
		b = EntityBuilder.create(engine);
		b.IDComp().id = Mapper.generateZoneID(Mapper.getPlayerEntityID(), ZoneType.FRIEND);
		b.ZoneComp().zoneID = b.IDComp().id;
		b.ZoneComp().zoneType = ZoneType.FRIEND;
		b.ZoneComp().layout = new  IdentityLayout(b.ZoneComp());
		ZoneComp friendZone = b.ZoneComp();
		engine.addEntity(b.Entity());
		b.free();
		
		b = EntityBuilder.create(engine);
		b.IDComp().id = Mapper.generateZoneID(Mapper.getPlayerEntityID(), ZoneType.ACTIVE_CARD);
		b.ZoneComp().zoneID = b.IDComp().id;
		b.ZoneComp().zoneType = ZoneType.ACTIVE_CARD;
		b.ZoneComp().layout = new ActiveCardLayout(b.ZoneComp());
		ZoneComp activeCardZone = b.ZoneComp();
		engine.addEntity(b.Entity());
		b.free();
		
		b = EntityBuilder.create(engine);
		b.IDComp().id = Mapper.generateZoneID(Mapper.getPlayerEntityID(), ZoneType.ENEMY);
		b.ZoneComp().zoneID = b.IDComp().id;
		b.ZoneComp().zoneType = ZoneType.ENEMY;
		b.ZoneComp().layout = new IdentityLayout(b.ZoneComp());
		ZoneComp enemyZone = b.ZoneComp();
		engine.addEntity(b.Entity());
		b.free();
		
		// Other players
		b = EntityBuilder.create(engine);
		b.IDComp().id = IDGenerator.newID();
		PlayerComp warriorPlayer = b.PlayerComp();
		ID warriorID = b.IDComp().id;
		Entity warriorEntity = b.Entity();
		engine.addEntity(b.Entity());
		b.free();
		
		// We'll make this later in the "add player"
		ID rogueID = Mapper.getPlayerEntityID();
		
		b = EntityBuilder.create(engine);
		PlayerComp sorcererPlayer = b.PlayerComp();
		b.IDComp().id = IDGenerator.newID();
		ID sorcererID = b.IDComp().id;
		engine.addEntity(b.Entity());
		b.free();
		
		b = EntityBuilder.create(engine);
		b.PlayerComp();
		PlayerComp rangerPlayer = b.PlayerComp();
		b.IDComp().id = IDGenerator.newID();
		ID rangerID = b.IDComp().id;
		Entity rangerEntity = b.Entity();
		engine.addEntity(b.Entity());
		b.free();
		
		// CHARACTERS

		b = EntityUtil.buildBasicEntity(engine, 
				  EntityType.FRIEND, 
				  new Vector3(660,140,0), 
				  Media.warrior);
		b.CharacterComp();
		b.VitalsComp().maxHealth = 100;
		b.VitalsComp().weakHealth = 25;
		b.VitalsComp().health = 10;
		b.StatsComp().level = 1;
		b.StatsComp().power = 10;
		b.StatsComp().defense = 10;
		b.StatsComp().mPower = 2;
		b.StatsComp().mDefense = 2;
		b.DescriptionComp().name = "Warrior";
		b.SpriteComp();
		warriorPlayer.characterID = b.IDComp().id;
		friendZone.add(b);
		engine.addEntity(b.Entity());
		b.free();
		
		b = EntityUtil.buildBasicEntity(engine, 
				  EntityType.FRIEND, 
				  new Vector3(750,15,0), 
				  Media.rogue);
		ActiveCardComp rogueActiveCardComp = b.ActiveCardComp();
		b.CharacterComp();
		b.VitalsComp().maxHealth = 100;
		b.VitalsComp().weakHealth = 25;
		b.VitalsComp().health = 47;
		b.StatsComp().level = 1;
		b.StatsComp().power = 8;
		b.StatsComp().defense = 6;
		b.StatsComp().mPower = 5;
		b.StatsComp().mDefense = 6;
		b.DescriptionComp().name = "Rogue";
		b.SpriteComp();
		rogueCharacterID = b.IDComp().id;
		friendZone.add(b);
		engine.addEntity(b.Entity());
		b.free();
		
		b = EntityUtil.buildBasicEntity(engine, 
				  EntityType.FRIEND, 
				  new Vector3(675,-120,0), 
				  Media.sorcerer);
		b.CharacterComp();
		b.VitalsComp().maxHealth = 100;
		b.VitalsComp().weakHealth = 25;
		b.VitalsComp().health = 67;
		b.StatsComp().level = 1;
		b.StatsComp().power = 2;
		b.StatsComp().defense = 2;
		b.StatsComp().mPower = 10;
		b.StatsComp().mDefense = 10;
		b.DescriptionComp().name = "Sorcerer";
		b.SpriteComp();
		sorcererPlayer.characterID = b.IDComp().id;
		friendZone.add(b);
		engine.addEntity(b.Entity());
		b.free();
		
		b = EntityUtil.buildBasicEntity(engine, 
				  EntityType.FRIEND, 
				  new Vector3(750,-255,0), 
				  Media.ranger);
		b.CharacterComp();
		b.VitalsComp().maxHealth = 100;
		b.VitalsComp().weakHealth = 25;
		b.VitalsComp().health = 80;
		b.StatsComp().level = 1;
		b.StatsComp().power = 9;
		b.StatsComp().defense = 8;
		b.StatsComp().mPower = 3;
		b.StatsComp().mDefense = 4;
		b.DescriptionComp().name = "Ranger";
		b.SpriteComp();
		rangerPlayer.characterID = b.IDComp().id;
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
		b.SpriteComp();
		b.StatsComp().level = 3;
		b.StatsComp().power = 20;
		b.StatsComp().defense = 15;
		b.StatsComp().mPower = 10;
		b.StatsComp().mDefense = 10;
		b.SpriteComp().scale = new Vector2(2f,2f);
		enemyZone.add(b);
		engine.addEntity(b.Entity());
		b.free();
		
		// Cards
		
		b = EntityUtil.buildDynamicSprite(engine, 
										  IDGenerator.newID(),
										  EntityType.CARD, 
										  new Vector3(-700,-475,0),
										  (int) Media.forest.getWidth(),
										  (int) Media.forest.getHeight(),
										  new TextureRegion(Media.forest),
										  getBox2DWorld(),
										  BodyType.DynamicBody,
										  100000);
		
		b.CardComp().ownerID = rogueID;
		b.DescriptionComp().name = "Forest";
		b.SpriteComp();
		b.VelocityComp();
		b.BodyComp();
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
				
				Sys.out.println("Deal damage and heal friend for amount:");
				Sys.out.println(nameEnemy + " received " + damaged + " damage.");
				Sys.out.println(nameFriend + " received " + healed + " health.");
				
				Sys.out.println(nameEnemy + " hp: " + vEnemy.health);
				Sys.out.println(nameFriend + " hp: " + vFriend.health);
				
				if(vEnemy.isNearDeath()) {
					Sys.out.println(nameEnemy + " is near death.");
				} else if(vEnemy.isDead()) {
					Sys.out.println(nameEnemy + " is dead.");
				}
			}
			
		};
		handZone.add(b);
		engine.addEntity(b.Entity());
		b.free();
		
		//TextureRegion swampTexture = GraphicsUtil.combineWithText(Media.swamp, "Hello World!", 20, 0, 0);
		TextureRegion swampTexture = GraphicsUtil.buildCardTexture(Media.swamp, Media.warrior, "Damage Enemy Very Badly");
		
		b = EntityUtil.buildBasicEntity(engine, 
				EntityType.CARD, 
				new Vector3(-450,-475,0), 
				swampTexture);
		ID swampId = b.IDComp().id;
		b.CardComp().ownerID = rogueID;
		b.DescriptionComp().name = "Swamp";
		b.SpriteComp();
		b.VelocityComp();
		b.BodyComp();
		new EnemyCallback(b) {

			@Override
			public void call(Entity owner, Entity card, Entity enemy) {
				int damage = CombatUtil.getDamage(owner, enemy, 20);
				Mapper.VitalsComp.get(enemy).damage(damage);
				Sys.out.println("Dealt " + damage + " damage.");
			}
			
		};
		handZone.add(b);
		engine.addEntity(b.Entity());
		b.free();
		
		b = EntityUtil.buildBasicEntity(engine, 
				EntityType.CARD, 
				new Vector3(-200,-475,0), 
				Media.island);
		b.CardComp().ownerID = rogueID;
		b.DescriptionComp().name = "Island";
		b.SpriteComp();
		b.VelocityComp();
		b.BodyComp();
		new CardCallback(b) {

			@Override
			public void call(Entity owner, Entity card, Entity activeCard) {
				DescriptionComp d = Mapper.DescriptionComp.get(activeCard);
				Sys.out.println("Increasing multiplicity for: " + d.name);
				Mapper.TurnActionComp.get(activeCard).turnAction.multiplicity++;
			}
			
		};
		handZone.add(b);
		engine.addEntity(b.Entity());
		b.free();
		
		b = EntityUtil.buildBasicEntity(engine, 
				EntityType.CARD, 
				new Vector3(-200,-475,0), 
				Media.island);
		b.CardComp().ownerID = rogueID;
		b.DescriptionComp().name = "Island";
		b.SpriteComp();
		b.VelocityComp();
		b.BodyComp();
		new CardCallback(b) {

			@Override
			public void call(Entity owner, Entity card, Entity activeCard) {
				DescriptionComp d = Mapper.DescriptionComp.get(activeCard);
				Sys.out.println("Increasing multiplicity for: " + d.name);
				Mapper.TurnActionComp.get(activeCard).turnAction.multiplicity++;
			}
			
		};
		handZone.add(b);
		engine.addEntity(b.Entity());
		b.free();
		
		b = EntityUtil.buildBasicEntity(engine, 
				EntityType.CARD, 
				new Vector3(50,-475,0), 
				Media.mountain);
		b.CardComp().ownerID = rogueID;
		b.DescriptionComp().name = "Mountain";
		b.SpriteComp();
		b.TurnActionComp().turnAction.defaultAll = true;
		b.TurnActionComp().turnAction.all = true;
		b.VelocityComp();
		b.BodyComp();
		new FriendCallback(b) {

			@Override
			public void call(Entity owner, Entity card, Entity friend) {
				int hp = 50;
				Mapper.VitalsComp.get(friend).heal(hp);
				Sys.out.println("Healed " + hp + " hp to " + Mapper.DescriptionComp.get(friend).name);
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
				  EntityType.CURSOR, 
				  new Vector3(-550,-100,1), 
				  Media.handPointDown);
		b.IDComp().id = Mapper.getPlayerEntityID();
		b.PlayerComp().characterID = rogueCharacterID;
		b.PlayerComp().cursorID = b.IDComp().id;
		b.CursorComp().ownerID = Mapper.getPlayerEntityID(); 
		b.ChangeZoneComp().newZoneID = Mapper.ZoneComp.getID(b.CursorComp().ownerID, ZoneType.HAND);
		b.ChangeZoneComp().newIndex = 0;
		b.CursorInputRegulatorComp();
		b.CursorInputComp();
		b.InputComp().input = buildInput(focus);
		b.ZonePositionComp().zoneID = b.ChangeZoneComp().newZoneID;
		b.ZonePositionComp().index = 0;
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
		
		// START OF ACTIVE SYSTEMS
		engine.addSystem(new ZoneLayoutSystem());
		engine.addSystem(new ZonePositionSystem());
		engine.addSystem(new CursorPositionSystem());
		engine.addSystem(new TweenSystem());
		
		engine.addSystem(new TurnPhaseStartBattleSystem());
		engine.addSystem(new TurnPhaseStartTurnSystem());
		engine.addSystem(new TurnPhaseSelectEnemyActionsSystem());
		engine.addSystem(new TurnPhaseSelectFriendActionsSystem());
		engine.addSystem(new TurnPhasePerformFriendActionsSystem());
		engine.addSystem(new TurnPhasePerformEnemyActionsSystem());
		engine.addSystem(new TurnPhaseEndTurnSystem());
		engine.addSystem(new TurnPhaseEndBattleSystem());
		
		engine.addSystem(new CursorInputSystem());
		engine.addSystem(new CursorTargetValidationSystem());
		engine.addSystem(new CursorMoveSystem());
		engine.addSystem(new CursorSelectSystem());
		engine.addSystem(new QueueTurnActionsSystem());
		
		engine.addSystem(new ApproachTargetSystem());
		
		if(!endPointHelper.isHeadless()) {
			engine.addSystem(new AnimationSystem());
			engine.addSystem(new RenderInitSystem());
			engine.addSystem(new RenderEntitiesSystem(getCamera()));
			engine.addSystem(new RenderTimerSystem(getCamera()));
		}
		
		engine.addSystem(new CleanupTurnActionsSystem());
		engine.addSystem(new RemoveEntitiesSystem(engine, endPointHelper.getClient()));
		
		/*if(endPointHelper.isClient()) {
			engine.addSystem(new AddEntitiesSystem(this, endPointHelper.getClient()));
		}*/
		
		engine.addSystem(new CleanupInputSystem(this));
		
		return engine;
    }

}
