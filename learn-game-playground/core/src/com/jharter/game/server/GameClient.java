package com.jharter.game.server;

import java.io.IOException;
import java.util.Comparator;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.badlogic.gdx.utils.TimeUtils;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Listener.ThreadedListener;
import com.jharter.game.ashley.entities.EntityUtil;
import com.jharter.game.server.GameNetwork.AddPlayer;
import com.jharter.game.server.GameNetwork.AddPlayers;
import com.jharter.game.server.GameNetwork.Login;
import com.jharter.game.server.GameNetwork.Ping;
import com.jharter.game.server.GameNetwork.SnapshotPacket;
import com.jharter.game.util.IDUtil;

public abstract class GameClient {
	
	public static final boolean DEBUG_SHOW_PING = false;
	
	protected String clientId;
	protected Client client;
	protected long pingMS = 0;
	protected long latencyMS = 0;
	protected float time = 0;
	protected DelayedRemovalArray<SnapshotPacket> snapshotPackets = new DelayedRemovalArray<SnapshotPacket>();
	protected AddPlayers addPlayers = null;
	protected String playerId = IDUtil.newID();
	
	public GameClient() {
		clientId = "ID:" + System.currentTimeMillis();
		client = new Client();
	}
	
	public String getPlayerId() {
		return playerId;
	}
	
	public void tick(float deltaTime) {
		time += deltaTime;
		if(time >= GameNetwork.PING_FREQUENCY_SEC) {
			ping();
			time = 0;
		}
	}
	
	public long getPing() {
		return pingMS;
	}
	
	public long getLatency() {
		return latencyMS;
	}
	
	public void ping() {
		Ping ping = new Ping();
		ping.time = TimeUtils.millis();
		client.sendTCP(ping);
	}
	
	private void setPing(Ping ping) {
		pingMS = TimeUtils.millis() - ping.time;
		latencyMS = Math.round(pingMS / 2.0);

		if(DEBUG_SHOW_PING) {
			System.out.println("ping: " + getPing() + " ms, latency: " + getLatency() + " ms");
		}
	}
	
	public String getClientId() {
		return clientId;
	}
	
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	
	public abstract void received(Connection connection, Object object, Client client);
	
	public void start() {
		client.start();
		GameNetwork.register(client);
		
		client.addListener(new ThreadedListener(new Listener() {
			public void connected (Connection connection) {
				
			}

			public void received (Connection connection, Object object) {
				if(object instanceof Ping) {
					setPing((Ping) object);
				} else {
					GameClient.this.received(connection, object, client);
				}
			}

			public void disconnected (Connection connection) {
				
			}
			
		}));
		
		try {
			client.connect(GameNetwork.TIMEOUT, GameNetwork.HOST, GameNetwork.PORT);
			// Server communication after connection can go here, or in Listener#connected().
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		login();
	}
	
	public void login() {
		Login login = new Login();
		login.id = clientId;
		client.sendTCP(login);
	}

	public void sendTCP(Object object) {
		client.sendTCP(object);
	}
	
	protected void handleSnapshotPacket(SnapshotPacket snapshotPacket) {
		snapshotPackets.begin();
		DelayedRemovalArray<SnapshotPacket> packets = new DelayedRemovalArray<SnapshotPacket>(snapshotPackets);
		packets.add(snapshotPacket);
		packets.sort(new Comparator<SnapshotPacket>() {

			@Override
			public int compare(SnapshotPacket arg0, SnapshotPacket arg1) {
				return (int) (arg0.time - arg1.time);
			}
			
		});
		DelayedRemovalArray<SnapshotPacket> temp = snapshotPackets;
		snapshotPackets = packets;
		temp.end();    				
	}
	
	public DelayedRemovalArray<SnapshotPacket> getSnapshotPackets() {
		return snapshotPackets;
	}
	
	protected void handleAddPlayers(AddPlayers addPlayers) {
		System.out.println("Client " + client.getID() + " received " + addPlayers.players.size + " players from server.");
		this.addPlayers = addPlayers;
	}
	
	public AddPlayers getAddPlayers() {
		return addPlayers;
	}
	
	public void clearAddPlayers() {
		this.addPlayers = null;
	}
	
}
