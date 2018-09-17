package com.jharter.game.stages.battlestage;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.jharter.game.ashley.components.Comp;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.EntityBuilder;
import com.jharter.game.ashley.systems.AnimationSystem;
import com.jharter.game.ashley.systems.DiscardZoneSystem;
import com.jharter.game.ashley.systems.CleanupTurnActionsSystem;
import com.jharter.game.ashley.systems.CursorInputSystem;
import com.jharter.game.ashley.systems.CursorMoveSystem;
import com.jharter.game.ashley.systems.CursorSelectSystem;
import com.jharter.game.ashley.systems.CursorTargetValidationSystem;
import com.jharter.game.ashley.systems.QueueTurnActionsSystem;
import com.jharter.game.ashley.systems.RemoveEntitiesSystem;
import com.jharter.game.ashley.systems.RenderEntitiesSystem;
import com.jharter.game.ashley.systems.RenderWorldGridSystem;
import com.jharter.game.ashley.systems.TurnPhaseEndBattleSystem;
import com.jharter.game.ashley.systems.TurnPhaseEndTurnSystem;
import com.jharter.game.ashley.systems.TurnPhasePerformEnemyActionsSystem;
import com.jharter.game.ashley.systems.TurnPhasePerformFriendActionsSystem;
import com.jharter.game.ashley.systems.TurnPhaseSelectEnemyActionsSystem;
import com.jharter.game.ashley.systems.TurnPhaseSelectFriendActionsSystem;
import com.jharter.game.ashley.systems.TurnPhaseStartBattleSystem;
import com.jharter.game.ashley.systems.TurnPhaseStartTurnSystem;
import com.jharter.game.ashley.systems.TweenSystem;
import com.jharter.game.ashley.systems.ZoneChangeSystem;
import com.jharter.game.ashley.systems.ZoneLayoutSystem;
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
import com.jharter.game.layout.CursorLayout;
import com.jharter.game.layout.FriendLayout;
import com.jharter.game.layout.HandLayout;
import com.jharter.game.layout.IdentityLayout;
import com.jharter.game.network.endpoints.EndPointHelper;
import com.jharter.game.network.endpoints.GameClient;
import com.jharter.game.network.endpoints.GameServer;
import com.jharter.game.stages.GameStage;
import com.jharter.game.util.id.ID;
import com.jharter.game.util.id.IDUtil;

import uk.co.carelesslabs.Enums.ZoneType;
import uk.co.carelesslabs.Media;

public class BattleStage extends GameStage {
	
	public BattleStage(EndPointHelper endPointHelper) {
		super(endPointHelper);
	}

	@Override
	public void addEntities(PooledEngine engine) {
		BackgroundHelper.addBackground(engine, Media.background);
		//BackgroundHelper.addBackground(engine, Media.bgField2);
		//BackgroundHelper.addBackground(engine, Media.bgLightYellow, 5f);
		//BackgroundHelper.addBackground(engine, Media.bgField2bg);
		//BackgroundHelper.addBackground(engine, Media.bgField2fg, 5f);

		// Player IDs
		ID warriorPlayerID = IDUtil.buildPlayerEntityID();
		ID sorcererPlayerID = IDUtil.buildPlayerEntityID();
		ID roguePlayerID = IDUtil.buildPlayerEntityID();
		ID rangerPlayerID = IDUtil.buildPlayerEntityID();
		ID globalPlayerID = IDUtil.getGlobalPlayerEntityID();
		
		// Battle entity
		//BattleHelper.addBattle(engine, warriorPlayerID);
		
		// Player Zones
		ZoneComp handZone = ZoneHelper.addZone(engine, roguePlayerID, ZoneType.HAND, new HandLayout());
		ZoneHelper.addZone(engine, roguePlayerID, ZoneType.DECK);
		ZoneHelper.addZone(engine, roguePlayerID, ZoneType.DISCARD);
		
		ZoneComp warriorHandZone = ZoneHelper.addZone(engine, warriorPlayerID, ZoneType.HAND, new HandLayout());
		ZoneHelper.addZone(engine, warriorPlayerID, ZoneType.DECK);
		ZoneHelper.addZone(engine, warriorPlayerID, ZoneType.DISCARD);
		
		ZoneComp sorcererHandZone = ZoneHelper.addZone(engine, sorcererPlayerID, ZoneType.HAND, new HandLayout());
		ZoneHelper.addZone(engine, sorcererPlayerID, ZoneType.DECK);
		ZoneHelper.addZone(engine, sorcererPlayerID, ZoneType.DISCARD);
		
		ZoneComp rangerHandZone = ZoneHelper.addZone(engine, rangerPlayerID, ZoneType.HAND, new HandLayout());
		ZoneHelper.addZone(engine, rangerPlayerID, ZoneType.DECK);
		ZoneHelper.addZone(engine, rangerPlayerID, ZoneType.DISCARD);

		ZoneComp infoZone = ZoneHelper.addZone(engine, globalPlayerID, ZoneType.INFO, new IdentityLayout());
		ZoneComp friendZone = ZoneHelper.addZone(engine, globalPlayerID, ZoneType.FRIEND, new FriendLayout());
		ZoneComp enemyZone = ZoneHelper.addZone(engine, globalPlayerID, ZoneType.ENEMY);
		ZoneHelper.addZone(engine, globalPlayerID, ZoneType.ACTIVE_CARD, new ActiveCardLayout().setPriority(-1));
		ZoneHelper.addZone(engine, globalPlayerID, ZoneType.CURSOR, new CursorLayout().setPriority(-2));
		
		// Turn timer
		TurnHelper.addTurnEntity(engine, infoZone, 30f);
		
		// Arrow
		ArrowHelper.addArrow(engine, infoZone);
		
		// Other players
		/*PlayerComp roguePlayer = PlayerHelper.addPlayer(engine, roguePlayerID);
		PlayerComp warriorPlayer = PlayerHelper.addPlayer(engine, warriorPlayerID);
		PlayerComp sorcererPlayer = PlayerHelper.addPlayer(engine, sorcererPlayerID);
		PlayerComp rangerPlayer = PlayerHelper.addPlayer(engine, rangerPlayerID);*/
		
		// CHARACTERS
		PlayerHelper.addPlayer(engine, friendZone, infoZone, warriorPlayerID, Media.warrior, "Warrior");
		PlayerHelper.addPlayer(engine, friendZone, infoZone, sorcererPlayerID, Media.sorcerer, "Sorcerer");
		PlayerHelper.addPlayer(engine, friendZone, infoZone, roguePlayerID, Media.rogue, "Rogue");
		PlayerHelper.addPlayer(engine, friendZone, infoZone, rangerPlayerID, Media.ranger, "Ranger");

		// ENEMIES
		EnemyHelper.addAtma(engine, enemyZone, infoZone);
		EnemyHelper.addCactar(engine, enemyZone, infoZone);
		
		// Cards
		CardHelper.addDrainCard(engine, roguePlayerID, handZone);
		CardHelper.addX2Card(engine, roguePlayerID, handZone);
		CardHelper.addAllCard(engine, roguePlayerID, handZone);
		
		CardHelper.addAttackCard(engine, warriorPlayerID, warriorHandZone);
		CardHelper.addX2Card(engine, warriorPlayerID, warriorHandZone);
		CardHelper.addAllCard(engine, warriorPlayerID, warriorHandZone);

		CardHelper.addHealAllCard(engine, sorcererPlayerID, sorcererHandZone);
		CardHelper.addX2Card(engine, sorcererPlayerID, sorcererHandZone);
		CardHelper.addAllCard(engine, sorcererPlayerID, sorcererHandZone);
		
		CardHelper.addAttackAllCard(engine, rangerPlayerID, rangerHandZone);
		CardHelper.addX2Card(engine, rangerPlayerID, rangerHandZone);
		CardHelper.addAllCard(engine, rangerPlayerID, rangerHandZone);
		
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
		EntityBuilder b = CursorHelper.buildCursor(engine, IDUtil.getCursorEntityID(), ZoneType.HAND);
		if(focus) {
			b.FocusComp();
		}
		b.InputComp().input = buildInput(focus);
		engine.addEntity(b.Entity());
		return b;
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
		engine.addSystem(new TurnPhaseStartBattleSystem());
			engine.addSystem(new TurnPhaseStartTurnSystem());
				engine.addSystem(new TurnPhaseSelectEnemyActionsSystem());
				engine.addSystem(new TurnPhaseSelectFriendActionsSystem());
				engine.addSystem(new TurnPhasePerformFriendActionsSystem());
				engine.addSystem(new TurnPhasePerformEnemyActionsSystem());
			engine.addSystem(new TurnPhaseEndTurnSystem());
		engine.addSystem(new TurnPhaseEndBattleSystem());
	}
	
	private void addCursorSystems(PooledEngine engine) {
		engine.addSystem(new CursorInputSystem());
		engine.addSystem(new CursorTargetValidationSystem());
		engine.addSystem(new CursorMoveSystem());
		engine.addSystem(new CursorSelectSystem());
	}
	
	private void addOtherSystems(PooledEngine engine) {
		engine.addSystem(new QueueTurnActionsSystem());
		engine.addSystem(new CleanupTurnActionsSystem());
		engine.addSystem(new DiscardZoneSystem());
		engine.addSystem(new ZoneChangeSystem());
		engine.addSystem(new RemoveEntitiesSystem(engine, endPointHelper.getClient()));
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
    	
		Comp.Entity.addIdListener(engine, getBox2DWorld());
		
		addNetworkSystems(engine);
		addDependencySystems(engine);
		addTurnPhaseSystems(engine);
		addCursorSystems(engine);
		addOtherSystems(engine);
		
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
