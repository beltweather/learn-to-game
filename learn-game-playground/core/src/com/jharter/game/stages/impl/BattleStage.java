package com.jharter.game.stages.impl;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.jharter.game.debug.Debug;
import com.jharter.game.ecs.components.Components.ZoneComp;
import com.jharter.game.ecs.entities.EntityBuilder;
import com.jharter.game.ecs.helpers.ArrowHelper;
import com.jharter.game.ecs.helpers.BackgroundHelper;
import com.jharter.game.ecs.helpers.CardHelper;
import com.jharter.game.ecs.helpers.CursorHelper;
import com.jharter.game.ecs.helpers.EnemyHelper;
import com.jharter.game.ecs.helpers.PlayerHelper;
import com.jharter.game.ecs.helpers.TurnHelper;
import com.jharter.game.ecs.helpers.ZoneHelper;
import com.jharter.game.ecs.systems.AnimationSystem;
import com.jharter.game.ecs.systems.CleanupTurnActionsSystem;
import com.jharter.game.ecs.systems.DiscardZoneSystem;
import com.jharter.game.ecs.systems.QueueTurnActionsSystem;
import com.jharter.game.ecs.systems.RemoveEntitiesSystem;
import com.jharter.game.ecs.systems.RenderEntitiesSystem;
import com.jharter.game.ecs.systems.RenderWorldGridSystem;
import com.jharter.game.ecs.systems.TweenSystem;
import com.jharter.game.ecs.systems.ZoneChangeSystem;
import com.jharter.game.ecs.systems.ZoneLayoutSystem;
import com.jharter.game.ecs.systems.cursor.CursorAcceptSystem;
import com.jharter.game.ecs.systems.cursor.CursorCancelSystem;
import com.jharter.game.ecs.systems.cursor.CursorFinishSelectionSystem;
import com.jharter.game.ecs.systems.cursor.CursorInputSystem;
import com.jharter.game.ecs.systems.cursor.CursorMoveSystem;
import com.jharter.game.ecs.systems.cursor.CursorPrevNextSystem;
import com.jharter.game.ecs.systems.cursor.CursorTargetValidationSystem;
import com.jharter.game.ecs.systems.cursor.CursorTurnActionValidationSystem;
import com.jharter.game.ecs.systems.network.client.ClientAddPlayersPacketSystem;
import com.jharter.game.ecs.systems.network.client.ClientRandomMovementSystem;
import com.jharter.game.ecs.systems.network.client.ClientRemoveEntityPacketSystem;
import com.jharter.game.ecs.systems.network.client.ClientSendInputSystem;
import com.jharter.game.ecs.systems.network.client.ClientSnapshotPacketSystem;
import com.jharter.game.ecs.systems.network.offline.OfflineSelectInputSystem;
import com.jharter.game.ecs.systems.network.server.ServerInputPacketSystem;
import com.jharter.game.ecs.systems.network.server.ServerRegisterPlayerPacketSystem;
import com.jharter.game.ecs.systems.network.server.ServerRequestEntityPacketSystem;
import com.jharter.game.ecs.systems.network.server.ServerSendSnapshotSystem;
import com.jharter.game.ecs.systems.turnphase.ChangeTurnPhaseSystem;
import com.jharter.game.ecs.systems.turnphase.TurnPhaseEndBattleSystem;
import com.jharter.game.ecs.systems.turnphase.TurnPhaseEndTurnSystem;
import com.jharter.game.ecs.systems.turnphase.TurnPhasePerformEnemyActionsSystem;
import com.jharter.game.ecs.systems.turnphase.TurnPhasePerformFriendActionsSystem;
import com.jharter.game.ecs.systems.turnphase.TurnPhaseSelectEnemyActionsSystem;
import com.jharter.game.ecs.systems.turnphase.TurnPhaseSelectFriendActionsSystem;
import com.jharter.game.ecs.systems.turnphase.TurnPhaseStartBattleSystem;
import com.jharter.game.ecs.systems.turnphase.TurnPhaseStartTurnSystem;
import com.jharter.game.layout.ActiveCardLayout;
import com.jharter.game.layout.CursorLayout;
import com.jharter.game.layout.FriendLayout;
import com.jharter.game.layout.HandLayout;
import com.jharter.game.layout.HiddenLayout;
import com.jharter.game.layout.IdentityLayout;
import com.jharter.game.network.endpoints.EndPointHelper;
import com.jharter.game.network.endpoints.GameClient;
import com.jharter.game.network.endpoints.GameServer;
import com.jharter.game.stages.GameStage;
import com.jharter.game.util.id.ID;

import uk.co.carelesslabs.Enums.ZoneType;
import uk.co.carelesslabs.Media;

public class BattleStage extends GameStage {
	
	public BattleStage(EndPointHelper endPointHelper) {
		super(endPointHelper);
	}

	@Override
	public void addEntities(PooledEngine engine) {
		BackgroundHelper BackgroundHelper = new BackgroundHelper(this);
		ZoneHelper ZoneHelper = new ZoneHelper(this);
		CardHelper CardHelper = new CardHelper(this);
		TurnHelper TurnHelper = new TurnHelper(this);
		ArrowHelper ArrowHelper = new ArrowHelper(this);
		PlayerHelper PlayerHelper = new PlayerHelper(this);
		EnemyHelper EnemyHelper = new EnemyHelper(this);
		CursorHelper CursorHelper = new CursorHelper(this);
		
		BackgroundHelper.addBackground(Media.background);
		//BackgroundHelper.addBackground(Media.bgField2);
		//BackgroundHelper.addBackground(Media.bgLightYellow, 5f);
		//BackgroundHelper.addBackground(Media.bgField2bg);
		//BackgroundHelper.addBackground(Media.bgField2fg, 5f);

		// Player IDs
		ID warriorPlayerID = getIDManager().buildPlayerEntityID();
		ID sorcererPlayerID = getIDManager().buildPlayerEntityID();
		ID roguePlayerID = getIDManager().buildPlayerEntityID();
		ID rangerPlayerID = getIDManager().buildPlayerEntityID();
		ID globalPlayerID = getIDManager().getGlobalPlayerEntityID();
		
		// Battle entity
		//BattleHelper.addBattle(engine, warriorPlayerID);
		
		// Player Zones
		ZoneComp handZone = ZoneHelper.addZone(roguePlayerID, ZoneType.HAND, new HandLayout(this));
		ZoneHelper.addZone(roguePlayerID, ZoneType.DECK, new HiddenLayout(this));
		ZoneHelper.addZone(roguePlayerID, ZoneType.DISCARD, new HiddenLayout(this));
		
		ZoneComp warriorHandZone = ZoneHelper.addZone(warriorPlayerID, ZoneType.HAND, new HandLayout(this));
		ZoneComp warriorDeckZone = ZoneHelper.addZone(warriorPlayerID, ZoneType.DECK, new HiddenLayout(this));
		ZoneHelper.addZone(warriorPlayerID, ZoneType.DISCARD, new HiddenLayout(this));
		
		ZoneComp sorcererHandZone = ZoneHelper.addZone(sorcererPlayerID, ZoneType.HAND, new HandLayout(this));
		ZoneHelper.addZone(sorcererPlayerID, ZoneType.DECK, new HiddenLayout(this));
		ZoneHelper.addZone(sorcererPlayerID, ZoneType.DISCARD, new HiddenLayout(this));
		
		ZoneComp rangerHandZone = ZoneHelper.addZone(rangerPlayerID, ZoneType.HAND, new HandLayout(this));
		ZoneHelper.addZone(rangerPlayerID, ZoneType.DECK, new HiddenLayout(this));
		ZoneHelper.addZone(rangerPlayerID, ZoneType.DISCARD, new HiddenLayout(this));

		ZoneComp infoZone = ZoneHelper.addZone(globalPlayerID, ZoneType.INFO, new IdentityLayout(this));
		ZoneComp friendZone = ZoneHelper.addZone(globalPlayerID, ZoneType.FRIEND, new FriendLayout(this));
		ZoneComp enemyZone = ZoneHelper.addZone(globalPlayerID, ZoneType.ENEMY);
		ZoneHelper.addZone(globalPlayerID, ZoneType.ACTIVE_CARD, new ActiveCardLayout(this).setPriority(-1));
		ZoneComp cursorZone = ZoneHelper.addZone(globalPlayerID, ZoneType.CURSOR, new CursorLayout(this).setPriority(-2));
		
		// Turn timer
		TurnHelper.addTurnEntity(infoZone, 30f);
		
		// Arrow
		ArrowHelper.addArrow(infoZone);
		
		// Other players
		/*PlayerComp roguePlayer = PlayerHelper.addPlayer(engine, roguePlayerID);
		PlayerComp warriorPlayer = PlayerHelper.addPlayer(engine, warriorPlayerID);
		PlayerComp sorcererPlayer = PlayerHelper.addPlayer(engine, sorcererPlayerID);
		PlayerComp rangerPlayer = PlayerHelper.addPlayer(engine, rangerPlayerID);*/
		
		// CHARACTERS
		PlayerHelper.addPlayer(friendZone, infoZone, warriorPlayerID, Media.warrior, "Warrior");
		PlayerHelper.addPlayer(friendZone, infoZone, sorcererPlayerID, Media.sorcerer, "Sorcerer");
		PlayerHelper.addPlayer(friendZone, infoZone, roguePlayerID, Media.rogue, "Rogue");
		PlayerHelper.addPlayer(friendZone, infoZone, rangerPlayerID, Media.ranger, "Ranger");

		// ENEMIES
		ID atmaID = EnemyHelper.addAtma(enemyZone, infoZone);
		ID cactarID = EnemyHelper.addCactar(enemyZone, infoZone);
		
		ZoneHelper.addZone(atmaID, ZoneType.HAND, new HiddenLayout(this));
		ZoneHelper.addZone(atmaID, ZoneType.DECK, new HiddenLayout(this));
		ZoneHelper.addZone(atmaID, ZoneType.DISCARD, new HiddenLayout(this));
		
		ZoneHelper.addZone(cactarID, ZoneType.HAND, new HiddenLayout(this));
		ZoneHelper.addZone(cactarID, ZoneType.DECK, new HiddenLayout(this));
		ZoneHelper.addZone(cactarID, ZoneType.DISCARD, new HiddenLayout(this));
		
		// Cards
		CardHelper.setOwnerID(atmaID);
		for(int i = 0; i < 1; i++) { CardHelper.addEnemyAttackCard(); }
		
		CardHelper.setOwnerID(cactarID);
		for(int i = 0; i < 1; i++) { CardHelper.addEnemyAttackCard(); }
		
		CardHelper.setOwnerID(roguePlayerID);
		for(int i = 0; i < 6; i++) { CardHelper.addDrainCard(); }
		for(int i = 0; i < 3; i++) { CardHelper.addAttackCard(); }
		for(int i = 0; i < 2; i++) { CardHelper.addAttackAllCard(); }
		for(int i = 0; i < 2; i++) { CardHelper.addHealAllCard(); }
		for(int i = 0; i < 3; i++) { CardHelper.addX2Card(); }
		for(int i = 0; i < 2; i++) { CardHelper.addAllCard(); }
		
		CardHelper.setOwnerID(warriorPlayerID);
		for(int i = 0; i < 6; i++) { CardHelper.addDrainCard(); }
		for(int i = 0; i < 3; i++) { CardHelper.addAttackCard(); }
		for(int i = 0; i < 2; i++) { CardHelper.addAttackAllCard(); }
		for(int i = 0; i < 2; i++) { CardHelper.addHealAllCard(); }
		for(int i = 0; i < 3; i++) { CardHelper.addX2Card(); }
		for(int i = 0; i < 2; i++) { CardHelper.addAllCard(); }
		
		CardHelper.setOwnerID(sorcererPlayerID);
		for(int i = 0; i < 6; i++) { CardHelper.addDrainCard(); }
		for(int i = 0; i < 3; i++) { CardHelper.addAttackCard(); }
		for(int i = 0; i < 2; i++) { CardHelper.addAttackAllCard(); }
		for(int i = 0; i < 2; i++) { CardHelper.addHealAllCard(); }
		for(int i = 0; i < 3; i++) { CardHelper.addX2Card(); }
		for(int i = 0; i < 2; i++) { CardHelper.addAllCard(); }
		
		CardHelper.setOwnerID(rangerPlayerID);
		for(int i = 0; i < 6; i++) { CardHelper.addDrainCard(); }
		for(int i = 0; i < 3; i++) { CardHelper.addAttackCard(); }
		for(int i = 0; i < 2; i++) { CardHelper.addAttackAllCard(); }
		for(int i = 0; i < 2; i++) { CardHelper.addHealAllCard(); }
		for(int i = 0; i < 3; i++) { CardHelper.addX2Card(); }
		for(int i = 0; i < 2; i++) { CardHelper.addAllCard(); }
		
		EntityBuilder b = CursorHelper.buildCursor(cursorZone);
		b.FocusComp();
		b.InputComp().input = buildInput(true);
		getEngine().addEntity(b.Entity());
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
		// XXX Shouldn't have to seed this with zone info, should be taken care of at turn start
		/*EntityBuilder b = CursorHelper.buildCursor(engine, IDUtil.getCursorEntityID(), ZoneType.HAND);
		if(focus) {
			b.FocusComp();
		}
		b.InputComp().input = buildInput(focus);
		engine.addEntity(b.Entity());*/
		return null;
	}

	@Override
	public Vector3 getEntryPoint() {
		return new Vector3(0,0,0);
	}
	
	private void addNetworkSystems(PooledEngine engine) {
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
	}
	
	private void addDependencySystems(PooledEngine engine) {
		engine.addSystem(new TweenSystem());
	}
	
	private void addTurnPhaseSystems(PooledEngine engine) {
		engine.addSystem(new ChangeTurnPhaseSystem());
		engine.addSystem(new TurnPhaseStartBattleSystem());
			engine.addSystem(new TurnPhaseStartTurnSystem());
				engine.addSystem(new TurnPhaseSelectEnemyActionsSystem());
				engine.addSystem(new TurnPhaseSelectFriendActionsSystem());
				engine.addSystem(new TurnPhasePerformFriendActionsSystem()); // XXX The only turn phase that iterates over a non-turn entity
				engine.addSystem(new TurnPhasePerformEnemyActionsSystem());
			engine.addSystem(new TurnPhaseEndTurnSystem());
		engine.addSystem(new TurnPhaseEndBattleSystem());
	}
	
	private void addCursorSystems(PooledEngine engine) {
		engine.addSystem(new CursorInputSystem());
		engine.addSystem(new CursorTargetValidationSystem());
		engine.addSystem(new CursorMoveSystem());
		engine.addSystem(new CursorPrevNextSystem());
		engine.addSystem(new CursorAcceptSystem());
		engine.addSystem(new CursorCancelSystem());
		engine.addSystem(new CursorTurnActionValidationSystem());
		engine.addSystem(new CursorFinishSelectionSystem());
	}
	
	private void addOtherSystems(PooledEngine engine) {
		engine.addSystem(new QueueTurnActionsSystem());
		engine.addSystem(new CleanupTurnActionsSystem());
		engine.addSystem(new DiscardZoneSystem());
		engine.addSystem(new ZoneChangeSystem());
		engine.addSystem(new RemoveEntitiesSystem(endPointHelper.getClient()));
	}
	
	private void addVisualSystems(PooledEngine engine) {
		engine.addSystem(new ZoneLayoutSystem());
		engine.addSystem(new AnimationSystem());
		engine.addSystem(new RenderEntitiesSystem(getCamera()));
		engine.addSystem(new RenderWorldGridSystem(getCamera()));
	}
	
	@Override
	protected PooledEngine buildEngine() {
    	PooledEngine engine = new PooledEngine();
    	
		addNetworkSystems(engine);
		addDependencySystems(engine);
		addTurnPhaseSystems(engine);
		addOtherSystems(engine);
		addCursorSystems(engine);
		
		/*if(endPointHelper.isClient()) {
			engine.addSystem(new AddEntitiesSystem(this, endPointHelper.getClient()));
		}*/
		
		// Add visual systems
		if(!endPointHelper.isHeadless()) {
			addVisualSystems(engine);
		}
		
		return engine;
    }

}