package com.jharter.game.game;

import com.badlogic.gdx.Game;
import com.jharter.game.ashley.entities.EntityBuilder;
import com.jharter.game.network.endpoints.EndPointHelper;
import com.jharter.game.network.endpoints.GameClient;
import com.jharter.game.network.endpoints.GameServer;
import com.jharter.game.network.packets.Packets.RegisterPlayerPacket;
import com.jharter.game.screens.GameScreen;
import com.jharter.game.screens.impl.TestStageScreen;
import com.jharter.game.stages.GameStage;
import com.jharter.game.util.Sys;
import com.jharter.game.util.id.IDUtil;

import uk.co.carelesslabs.Media;

public class OnlineGame extends Game {
	
	protected EndPointHelper endPointHelper;
	protected GameServer server;
	protected GameClient client;
	
	public OnlineGame(GameType type, boolean headless) {
		this.endPointHelper = new EndPointHelper(type, headless);
	}
	
	public EndPointHelper getEndPointHelper() {
		return endPointHelper;
	}
	
    @Override
    public void create() {
    	if(!endPointHelper.isHeadless()) {
    		Media.load_assets();
    	}
    
    	if(endPointHelper.isServer()) {
			initServer();
		} else if(endPointHelper.isClient()) {
			initClient();
		}
    	
    	setScreen(new TestStageScreen(endPointHelper));
    	
    	if(endPointHelper.isClient()) {
    		client.send(RegisterPlayerPacket.newInstance(client.getPlayerId()));
    	} else if(endPointHelper.isOffline()) {
    		EntityBuilder b = getStage().addPlayerEntity(IDUtil.newID(), getStage().getEntryPoint(), true);
    		if(b != null) {
    			b.free();
    		}
    		getStage().activate();
    	}
    	
    }
    
    public GameStage getStage() {
    	if(getScreen() instanceof GameScreen) {
    		return ((GameScreen) getScreen()).getStage();
    	}
    	return null;
    }
    
    @Override
    public void render() {
    	super.render();
    }
    
    private void initServer() {
    	Sys.out.println("Starting Server");
		server = new GameServer();
		endPointHelper.setEndPoint(server);
		server.start();
    }
    
    private void initClient() {
    	Sys.out.println("Starting Client");
		client = new GameClient(); //{

			/*@Override
			public void received(GameClient gameClient, Connection connection, Object object) {
				//if(object instanceof AddPlayers) {
				//	handleAddPlayers((AddPlayers) object);
				//} else {
					super.received(gameClient, connection, object);
				//}
			}*/
				
				/*} else if(object instanceof MoveUser) {
					serverMoveUser = (MoveUser) object;
					serverMoveUserTime = System.currentTimeMillis();
				} else if(object instanceof AddHeroes) {
					AddHeroes addHeroes = (AddHeroes) object;
					Sys.out.println("Client " + client.getID() + " received " + addHeroes.heroes.size + " heroes from server.");
					for(AddHero addHero : addHeroes.heroes) {
						if(heroes.containsKey(addHero.id)) {
							Sys.out.println("Client " + client.getID() + " skipping hero " + addHero.id);
							continue;
						}
						Sys.out.println("Client " + client.getID() + " adding new hero " + addHero.id);
    					Vector3 pos = new Vector3(addHero.x, addHero.y, addHero.z);
    					Hero hero = EntityFactory.newHero(addHero.id, pos, box2D);
    					addHero(hero);
					}
			        //box2D.populateEntityMap(island.entities);
				} else if(object instanceof RemoveEntities) {
					RemoveEntities removeEntities = (RemoveEntities) object;
					island.markEntitiesAsRemoved(box2D, removeEntities);
				}*/
			//}
			
		//};
		endPointHelper.setEndPoint(client);
		client.start();
    }

}
