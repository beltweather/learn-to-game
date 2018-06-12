package com.jharter.game.network;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Listener.LagListener;
import com.esotericsoftware.kryonet.Server;
import com.jharter.game.network.packets.Packet;
import com.jharter.game.util.id.ID;

public class GameServer extends GameEndPoint {
	
	protected Server server;
	protected Set<GameUser> loggedIn = new HashSet<GameUser>();
	
	public GameServer() {
		super();
		server = new Server() {
			protected Connection newConnection () {
				// By providing our own connection implementation, we can store per
				// connection state without a connection ID to state look up.
				return new GameConnection();
			}
		};
	}
	
	public void sendToTCP(int connectionID, Object object) {
		server.sendToTCP(connectionID, object);
		maybeFree(object);
	}
	
	public void sendToAllTCP(Object object) {
		server.sendToAllTCP(object);
		maybeFree(object);
	}
	
	public void sendToUDP(int connectionID, Object object) {
		server.sendToUDP(connectionID, object);
		maybeFree(object);
	}
	
	public void sendToAllUDP(Object object) {
		server.sendToAllUDP(object);
		maybeFree(object);
	}
	
	public void start() {
		GameNetwork.register(server);
		
		server.addListener(new LagListener(GameNetwork.LAG_MS, GameNetwork.LAG_MS, new Listener() {
			public void received (Connection c, Object object) {
				GameServer.this.received(GameServer.this, c, object);
				
				/*} else if(object instanceof RequestPlayer) {
					RequestPlayer request = (RequestPlayer) object;
					ID id = request.id;
					if(EntityUtil.findEntity(id) != null) {
						System.err.println("Requested new player with id " + id + " but they already exist.");
						return;
					}
					
					getStage().addPlayerEntity(id, getStage().getEntryPoint(), !hasFocus);
					if(!hasFocus) {
						getStage().activate();
					}
					hasFocus = true;
					
					AddPlayersPacket addPlayers = new AddPlayersPacket();
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
					server.sendToAllTCP(addPlayers);*/
					
				
				/* else if(object instanceof RemoveEntities) {
					RemoveEntities removeEntities = (RemoveEntities) object;
					island.markEntitiesAsRemoved(box2D, removeEntities);
					server.sendToAllTCP(removeEntities);
				}*/
				
				//GameServer.this.received(c, object, server);
			}
			
			private boolean isValid (ID value) {
				if (value == null) return false;
				return true;
			}

			public void disconnected (Connection c) {
				GameConnection connection = (GameConnection)c;
				if (connection.user != null) {
					loggedIn.remove(connection.user);
				}
			}
		}));
		
		try {
			server.bind(GameNetwork.TCP_PORT, GameNetwork.UDP_PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		server.start();
	}
	
	void loggedIn (GameConnection c, GameUser user) {
		c.user = user;
		loggedIn.add(user);
		server.sendToAllTCP(user);
	}
	
	// This holds per connection state.
	static class GameConnection extends Connection {
		public GameUser user;
	}
	
}
