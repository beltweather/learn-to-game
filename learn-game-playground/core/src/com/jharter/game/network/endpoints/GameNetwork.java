package com.jharter.game.network.endpoints;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import com.jharter.game.control.GlobalInputState;
import com.jharter.game.network.packets.Packet;
import com.jharter.game.network.packets.Packets.AddPlayersPacket;
import com.jharter.game.network.packets.Packets.InputPacket;
import com.jharter.game.network.packets.Packets.RegisterPlayerPacket;
import com.jharter.game.network.packets.Packets.RemoveEntityPacket;
import com.jharter.game.network.packets.Packets.RequestEntityPacket;
import com.jharter.game.network.packets.Packets.SnapshotPacket;
import com.jharter.game.util.id.ID;

public class GameNetwork {

	public static final int TIMEOUT = 5000;
	public static final String HOST = "localhost";
	public static final int TCP_PORT = 54555;
	public static final int UDP_PORT = 54556;
	public static final int LAG_MS = 20;
	public static final int PING_FREQUENCY_SEC = 8;
	
	// This registers objects that are going to be sent over the network.
	public static void register(EndPoint endPoint) {
		Kryo kryo = endPoint.getKryo();
		kryo.register(Character.class);
		kryo.register(Vector2.class);
		kryo.register(ObjectMap.class);
		kryo.register(Array.class);
		kryo.register(Object[].class);
		kryo.register(ArrayList.class);
		
		kryo.register(EntityData.class);
		kryo.register(GlobalInputState.class);
		kryo.register(Packet.class);
		kryo.register(ID.class);
		
		kryo.register(AddPlayer.class);
		
		kryo.register(AddPlayersPacket.class);
		kryo.register(SnapshotPacket.class);
		kryo.register(RegisterPlayerPacket.class);
		kryo.register(RequestEntityPacket.class);
		kryo.register(InputPacket.class);
		kryo.register(RemoveEntityPacket.class);
	}
	
	public static class EntityData {
		public ID id;
		public float x, y;
		public GlobalInputState input;
	}
	
	public static class AddPlayer {
		public ID id;
		public float x, y, z;
	}
	
}
