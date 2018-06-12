package com.jharter.game.game;

import com.badlogic.gdx.Game;
import com.jharter.game.ashley.systems.packets.impl.Packets.RegisterPlayerPacket;
import com.jharter.game.network.GameClient;
import com.jharter.game.network.GameServer;
import com.jharter.game.screens.GameScreen;
import com.jharter.game.screens.TestStageScreen;
import com.jharter.game.stages.GameStage;
import com.jharter.game.util.id.IDGenerator;

import uk.co.carelesslabs.Media;

public class OnlineGame extends Game {
	
	protected GameDescription description;
	protected GameServer server;
	protected GameClient client;
	
	public OnlineGame(GameType type, boolean headless) {
		this.description = new GameDescription(type, headless);
	}
	
	public GameDescription getDescription() {
		return description;
	}
	
    @Override
    public void create() {
    	if(!description.isHeadless()) {
    		Media.load_assets();
    	}
    
    	if(description.isServer()) {
			initServer();
		} else if(description.isClient()) {
			initClient();
		}
    	
    	setScreen(new TestStageScreen(description));
    	
    	if(description.isClient()) {
    		client.sendTCP(RegisterPlayerPacket.newInstance(client.getPlayerId()));
    	} else if(description.isOffline()) {
    		getStage().addPlayerEntity(IDGenerator.newID(), getStage().getEntryPoint(), true);
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
    	System.out.println("Starting Server");
		server = new GameServer();
		description.setServer(server);
		server.start();
    }
    
    private void initClient() {
    	System.out.println("Starting Client");
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
					System.out.println("Client " + client.getID() + " received " + addHeroes.heroes.size + " heroes from server.");
					for(AddHero addHero : addHeroes.heroes) {
						if(heroes.containsKey(addHero.id)) {
							System.out.println("Client " + client.getID() + " skipping hero " + addHero.id);
							continue;
						}
						System.out.println("Client " + client.getID() + " adding new hero " + addHero.id);
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
		description.setClient(client);
		client.start();
    }

}
