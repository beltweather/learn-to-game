package com.jharter.game.network;

import java.io.IOException;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.TimeUtils;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Listener.ThreadedListener;
import com.jharter.game.ashley.systems.PacketSystem;
import com.jharter.game.network.GameNetwork.AddPlayers;
import com.jharter.game.network.GameNetwork.Login;
import com.jharter.game.network.GameNetwork.Ping;
import com.jharter.game.network.GameNetwork.SnapshotPacket;
import com.jharter.game.network.packets.Packet;
import com.jharter.game.network.packets.PacketManager;
import com.jharter.game.network.packets.impl.SnapshotPacketManager;
import com.jharter.game.util.IDUtil;

public class GameClient {
	
	public static final boolean DEBUG_SHOW_PING = false;
	
	protected String clientId;
	protected Client client;
	protected long pingMS = 0;
	protected long latencyMS = 0;
	protected float time = 0;
	protected ObjectMap<Class, PacketManager> packetManagers = new ObjectMap();
	
	protected AddPlayers addPlayers = null;
	protected String playerId = IDUtil.newID();
	
	public GameClient() {
		clientId = "ID:" + System.currentTimeMillis();
		client = new Client();
		addPacketManagers();
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
	
	public Client getKryoClient() {
		return client;
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
		client.sendUDP(ping);
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
	
	@SuppressWarnings("unchecked")
	public void received(GameClient client, Connection connection, Object object) {
		if(object instanceof Package) {
			Packet<?> packet = (Packet<?>) object;
			getPacketManager(packet.getClass()).received(this, connection, packet);
		}
	}
	
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
					GameClient.this.received(GameClient.this, connection, object);
				}
			}

			public void disconnected (Connection connection) {
				
			}
			
		}));
		
		try {
			client.connect(GameNetwork.TIMEOUT, GameNetwork.HOST, GameNetwork.TCP_PORT, GameNetwork.UDP_PORT);
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
	
	public void sendUDP(Object object) {
		client.sendUDP(object);
	}
	
	public <T extends Packet<T>> void addPacketManager(Class<T> packetClass, PacketManager<?> packetManager) {
		packetManagers.put(packetClass, packetManager);
	}
	
	public void addPacketManagers() {
		addPacketManager(SnapshotPacket.class, new SnapshotPacketManager());
	}
	
	public <T extends Packet<T>> PacketManager<T> getPacketManager(Class<T> packetClass) {
		if(packetManagers.containsKey(packetClass)) {
			return (PacketManager<T>) packetManagers.get(packetClass);
		}
		return null;
	}
	
	public <T extends Packet<T>> PacketSystem<T> buildPacketSystem(Class<T> klass) {
		return getPacketManager(klass).buildSystem(this);
	}
	
	public void addPacketSystemsToEngine(Engine engine) {
		for(Class klass : packetManagers.keys()) {
			engine.addSystem(buildPacketSystem(klass));
		}
	}
	
	protected void handleSnapshotPacket(SnapshotPacket snapshotPacket) {
		getPacketManager(SnapshotPacket.class).getPackets().add(snapshotPacket);
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
