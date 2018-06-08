package com.jharter.game.game;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Game;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import com.jharter.game.ashley.components.Components.IDComp;
import com.jharter.game.ashley.components.Components.InputComp;
import com.jharter.game.ashley.components.Components.PlayerComp;
import com.jharter.game.ashley.components.Components.PositionComp;
import com.jharter.game.ashley.components.Mapper;
import com.jharter.game.ashley.entities.EntityUtil;
import com.jharter.game.control.GlobalInputState;
import com.jharter.game.network.GameClient;
import com.jharter.game.network.GameNetwork.AddPlayer;
import com.jharter.game.network.GameNetwork.AddPlayers;
import com.jharter.game.network.GameNetwork.RequestPlayer;
import com.jharter.game.network.GameServer;
import com.jharter.game.network.packets.Packet;
import com.jharter.game.screens.StageScreen;
import com.jharter.game.screens.TestStageScreen;
import com.jharter.game.stages.GameStage;
import com.jharter.game.util.IDUtil;

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
    		RequestPlayer requestHero = new RequestPlayer();
    		requestHero.id = client.getPlayerId();
    		client.sendTCP(requestHero);
    	} else if(description.isOffline()) {
    		getStage().addPlayerEntity(IDUtil.newID(), getStage().getEntryPoint(), true);
    		getStage().activate();
    	}
    	
    }
    
    public GameStage getStage() {
    	if(getScreen() instanceof StageScreen) {
    		return ((StageScreen) getScreen()).getStage();
    	}
    	return null;
    }
    
    @Override
    public void render() {
    	super.render();
    }
    
    private void initServer() {
    	System.out.println("Starting Server");
		server = new GameServer() {
			
			private boolean hasFocus = false;
			
			@Override
			public void received(Connection c, Object object, Server server) {
				if(object instanceof GlobalInputState) {
					GlobalInputState state = (GlobalInputState) object;
					String entityId = state.id;
					Entity entity = EntityUtil.findEntity(entityId);
					if(entity != null) {
						InputComp in = Mapper.InputComp.get(entity);
						if(in != null) {
							in.input.setInputState(state);
						}
					}
				} else if(object instanceof RequestPlayer) {
					RequestPlayer request = (RequestPlayer) object;
					String id = request.id;
					if(EntityUtil.findEntity(id) != null) {
						System.err.println("Requested new player with id " + id + " but they already exist.");
						return;
					}
					
					getStage().addPlayerEntity(id, getStage().getEntryPoint(), !hasFocus);
					if(!hasFocus) {
						getStage().activate();
					}
					hasFocus = true;
					
					AddPlayers addPlayers = new AddPlayers();
					for(Entity player : getStage().getEngine().getEntitiesFor(Family.all(PlayerComp.class, IDComp.class, PositionComp.class).get())) {
						AddPlayer addPlayer = new AddPlayer();
						PositionComp p = Mapper.PositionComp.get(player);
						addPlayer.id = Mapper.IDComp.get(player).id;
						addPlayer.x = p.position.x;
						addPlayer.y = p.position.y;
						addPlayer.z = p.position.z;
						addPlayers.players.add(addPlayer);
					}
					System.out.println("Server sending " + addPlayers.players.size + " players to all clients.");
					server.sendToAllTCP(addPlayers);
				} 
				
				/* else if(object instanceof RemoveEntities) {
					RemoveEntities removeEntities = (RemoveEntities) object;
					island.markEntitiesAsRemoved(box2D, removeEntities);
					server.sendToAllTCP(removeEntities);
				}*/
			} 
			
		};
		description.setServer(server);
		server.start();
    }
    
    private void initClient() {
    	System.out.println("Starting Client");
		client = new GameClient() {

			@Override
			public void received(GameClient gameClient, Connection connection, Object object) {
				if(object instanceof AddPlayers) {
					handleAddPlayers((AddPlayers) object);
				} else {
					super.received(gameClient, connection, object);
				}
			}
				
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
			
		};
		description.setClient(client);
		client.start();
    }

}
