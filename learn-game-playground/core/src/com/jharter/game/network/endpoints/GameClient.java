package com.jharter.game.network.endpoints;

import java.io.IOException;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage.Ping;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Listener.ThreadedListener;
import com.jharter.game.network.packets.Packet;
import com.jharter.game.util.Sys;
import com.jharter.game.util.id.ID;
import com.jharter.game.util.id.IDUtil;

public class GameClient extends GameEndPoint {
	
	public static final boolean DEBUG_SHOW_PING = false;
	
	protected Client client;
	protected float time = 0;
	
	protected ID playerId = IDUtil.newID();
	
	public GameClient() {
		super();
		client = new Client();
	}
	
	public ID getPlayerId() {
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
	
	public int getPing() {
		return client.getReturnTripTime();
	}
	
	public void ping() {
		client.updateReturnTripTime();
	}
	
	public void start() {
		client.start();
		GameNetwork.register(client);
		
		client.addListener(new ThreadedListener(new Listener() {
			public void connected (Connection connection) {
				connection.updateReturnTripTime();
			}

			public void received (Connection connection, Object object) {
				/*if(object instanceof Ping) {
					logPing((Ping) object);
				}*/ 
				GameClient.this.received(GameClient.this, connection, object);
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
		
	}
	
	private void logPing(Ping ping) {
		if(ping.isReply) {
			Sys.out.println("Client has been pinged! (" + client.getReturnTripTime() + " ms)");
		}
	}
	
	private void sendTCP(Object object) {
		client.sendTCP(object);
		maybeFree(object);
	}
	
	private void sendUDP(Object object) {
		client.sendUDP(object);
		maybeFree(object);
	}
	
	public void send(Packet packet) {
		if(packet.useTCP) {
			sendTCP(packet);
		} else {
			sendUDP(packet);
		}
	}
	
}
