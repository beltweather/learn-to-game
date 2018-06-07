package com.jharter.game.server;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Listener.LagListener;
import com.jharter.game.server.GameNetwork.Login;
import com.jharter.game.server.GameNetwork.Ping;
import com.jharter.game.server.GameNetwork.Register;
import com.jharter.game.server.GameNetwork.RegistrationRequired;
import com.esotericsoftware.kryonet.Server;

public abstract class GameServer {
	
	protected Server server;
	protected Set<GameUser> loggedIn = new HashSet<GameUser>();
	
	public GameServer() {
		server = new Server() {
			protected Connection newConnection () {
				// By providing our own connection implementation, we can store per
				// connection state without a connection ID to state look up.
				return new GameConnection();
			}
		};
	}
	
	public void sendToAllTCP(Object object) {
		server.sendToAllTCP(object);
	}
	
	public abstract void received(Connection c, Object object, Server server);
	
	public void start() {
		GameNetwork.register(server);
		
		server.addListener(new LagListener(GameNetwork.LAG_MS, GameNetwork.LAG_MS, new Listener() {
			public void received (Connection c, Object object) {
				
				if(object instanceof Ping) {
					server.sendToTCP(c.getID(), object);
					return;
				}
				
				// We know all connections for this server are actually CharacterConnections.
				GameConnection connection = (GameConnection)c;
				GameUser user = connection.user;

				if (object instanceof Login) {
					// Ignore if already logged in.
					if (user != null) return;

					// Reject if the name is invalid.
					String id = ((Login)object).id;
					if (!isValid(id)) {
						c.close();
						return;
					}

					// Reject if already logged in.
					for (GameUser u : loggedIn) {
						if (u.id.equals(id)) {
							c.close();
							return;
						}
					}

					//user = loadUser(id);

					// Reject if couldn't load character.
					if (user == null) {
						c.sendTCP(new RegistrationRequired());
						return;
					}

					loggedIn(connection, user);
					return;
				}

				if (object instanceof Register) {
					// Ignore if already logged in.
					if (user != null) return;

					Register register = (Register)object;

					// Reject if the login is invalid.
					if (!isValid(register.id)) {
						c.close();
						return;
					}
					if (!isValid(register.otherStuff)) {
						c.close();
						return;
					}

					// Reject if character already exists.
					/*if (loadUser(register.id) != null) {
						c.close();
						return;
					}*/

					user = new GameUser();
					user.id = register.id;
					
					/*if (!saveCharacter(user)) {
						c.close();
						return;
					}*/

					loggedIn(connection, user);
					return;
				}
				
				
				GameServer.this.received(c, object, server);
			}
			
			private boolean isValid (String value) {
				if (value == null) return false;
				value = value.trim();
				if (value.length() == 0) return false;
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
			server.bind(GameNetwork.PORT);
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
