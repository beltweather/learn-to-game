package com.jharter.game.network;

import java.io.IOException;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

public class TestServer {
	
	public static final int port = 54555;

	private TestServer() {}
	
	public static void init() {
		
	}
	
	public static void startServer() {
		
		Server server = new Server();
		try {
			server.bind(port, 54777);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		server.addListener(new Listener() {
	       public void received (Connection connection, Object object) {
	          if (object instanceof SomeRequest) {
	             SomeRequest request = (SomeRequest)object;
	             System.out.println(request.text);
	    
	             SomeResponse response = new SomeResponse();
	             response.text = "Thanks";
	             connection.sendTCP(response);
	          }
	       }
	    });
		
		Kryo kryo = server.getKryo();
		kryo.register(SomeRequest.class);
		kryo.register(SomeResponse.class);
	}
	
	public static void startClient() {
		
		Client client = new Client();
		client.start();
		try {
			client.connect(5000, "192.168.0.4", port, 54777);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		client.addListener(new Listener() {
	       public void received (Connection connection, Object object) {
	          if (object instanceof SomeResponse) {
	             SomeResponse response = (SomeResponse)object;
	             System.out.println(response.text);
	          }
	       }
	    });
		
		Kryo kryo = client.getKryo();
	    kryo.register(SomeRequest.class);
	    kryo.register(SomeResponse.class);
		
		SomeRequest request = new SomeRequest();
		request.text = "Here is the request";
		client.sendTCP(request);
	}
	
	private static class SomeRequest {
		public String text;
	}
	
	private static class SomeResponse {
		public String text;
	}
	
	public static void main(String[] args) {
		startServer();
		startClient();
	}
}
