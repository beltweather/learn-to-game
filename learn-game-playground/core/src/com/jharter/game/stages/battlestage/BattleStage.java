package com.jharter.game.stages.battlestage;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.jharter.game.ashley.components.Components.PlayerComp;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.EntityBuilder;
import com.jharter.game.ashley.components.Mapper;
import com.jharter.game.ashley.systems.AnimationSystem;
import com.jharter.game.ashley.systems.CleanupInputSystem;
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
import com.jharter.game.layout.CursorPositionSystem;
import com.jharter.game.layout.FriendArrowLayout;
import com.jharter.game.layout.FriendLayout;
import com.jharter.game.layout.HandLayout;
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
		BackgroundHelper.addBackground(engine, Media.background);
		//BackgroundHelper.addBackground(engine, Media.bgField);
		//BackgroundHelper.addBackground(engine, Media.bgLightYellow, 0.5f);
		
		// Player IDs
		ID warriorPlayerID = Mapper.buildPlayerEntityID();
		ID sorcererPlayerID = Mapper.buildPlayerEntityID();
		ID roguePlayerID = Mapper.buildPlayerEntityID();
		ID rangerPlayerID = Mapper.buildPlayerEntityID();
		ID globalPlayerID = Mapper.getGlobalPlayerEntityID();
		
		// Battle entity
		BattleHelper.addBattle(engine, warriorPlayerID);
		
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
		ZoneHelper.addZone(engine, globalPlayerID, ZoneType.ACTIVE_CARD, new ActiveCardLayout());
		
		//ZoneHelper.addZone(engine, globalPlayerID, ZoneType.FRIEND_ARROW, new FriendArrowLayout());
		
		// Turn timer
		TurnTimerHelper.addTurnTimer(engine, infoZone, 30f);
		
		// Arrow
		ArrowHelper.addArrow(engine, infoZone);
		
		// Other players
		PlayerComp roguePlayer = PlayerHelper.addPlayer(engine, roguePlayerID);
		PlayerComp warriorPlayer = PlayerHelper.addPlayer(engine, warriorPlayerID);
		PlayerComp sorcererPlayer = PlayerHelper.addPlayer(engine, sorcererPlayerID);
		PlayerComp rangerPlayer = PlayerHelper.addPlayer(engine, rangerPlayerID);
		
		// CHARACTERS
		warriorPlayer.battleAvatarID = FriendHelper.addFriend(engine, friendZone, infoZone, warriorPlayerID, Media.warrior, "Warrior");
		sorcererPlayer.battleAvatarID = FriendHelper.addFriend(engine, friendZone, infoZone, sorcererPlayerID, Media.sorcerer, "Sorcerer");
		roguePlayer.battleAvatarID = FriendHelper.addFriend(engine, friendZone, infoZone, roguePlayerID, Media.rogue, "Rogue");
		rangerPlayer.battleAvatarID = FriendHelper.addFriend(engine, friendZone, infoZone, rangerPlayerID, Media.ranger, "Ranger");

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
		EntityBuilder b = CursorHelper.buildCursor(engine, Mapper.getCursorEntityID(), Mapper.getPlayerEntityID(), ZoneType.HAND);
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
		
		// Supporting engine systems
		engine.addSystem(new TweenSystem());
		
		// Turn phase systems
		engine.addSystem(new TurnPhaseStartBattleSystem());
			engine.addSystem(new TurnPhaseStartTurnSystem());
				engine.addSystem(new TurnPhaseSelectEnemyActionsSystem());
				engine.addSystem(new TurnPhaseSelectFriendActionsSystem());
					// ADD - Turn phase select new player
				engine.addSystem(new TurnPhasePerformFriendActionsSystem());
				engine.addSystem(new TurnPhasePerformEnemyActionsSystem());
			engine.addSystem(new TurnPhaseEndTurnSystem());
		engine.addSystem(new TurnPhaseEndBattleSystem());
		
		// Cursor systems
		engine.addSystem(new CursorInputSystem());
		engine.addSystem(new CursorTargetValidationSystem());
		engine.addSystem(new CursorMoveSystem());
		engine.addSystem(new CursorSelectSystem());
		
		// Turn action systems
		engine.addSystem(new QueueTurnActionsSystem());
		engine.addSystem(new CleanupTurnActionsSystem());
		
		// Zone entity systems
		engine.addSystem(new ZoneChangeSystem());
		
		// General cleanup systems
		engine.addSystem(new RemoveEntitiesSystem(engine, endPointHelper.getClient()));
		
		/*if(endPointHelper.isClient()) {
			engine.addSystem(new AddEntitiesSystem(this, endPointHelper.getClient()));
		}*/
		
		engine.addSystem(new CleanupInputSystem(this));
		
		// Heady systems
		if(!endPointHelper.isHeadless()) {
			engine.addSystem(new ZoneLayoutSystem());
			engine.addSystem(new CursorPositionSystem());
			engine.addSystem(new AnimationSystem());
			//engine.addSystem(new RenderInitSystem());
			engine.addSystem(new RenderEntitiesSystem(getCamera()));
			//engine.addSystem(new RenderTimerSystem(getCamera()));
			engine.addSystem(new RenderWorldGridSystem(getCamera()));
		}
		
		return engine;
    }

}
