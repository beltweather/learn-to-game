package com.jharter.game.network;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import com.jharter.game.control.GlobalInputState;
import com.jharter.game.network.packets.Packet;
import com.jharter.game.util.id.ID;

public class GameNetwork {

	public static final int TIMEOUT = 5000;
	public static final String HOST = "localhost";
	public static final int TCP_PORT = 54555;
	public static final int UDP_PORT = 54556;
	public static final int LAG_MS = 20;
	public static final int PING_FREQUENCY_SEC = 5;
	
	// This registers objects that are going to be sent over the network.
	static public void register (EndPoint endPoint) {
		Kryo kryo = endPoint.getKryo();
		kryo.register(Login.class);
		kryo.register(RegistrationRequired.class);
		kryo.register(Register.class);
		kryo.register(AddUser.class);
		kryo.register(UpdateUser.class);
		kryo.register(RemoveUser.class);
		kryo.register(Character.class);
		kryo.register(MoveUser.class);
		kryo.register(NewScene.class);
		kryo.register(RequestScene.class);
		kryo.register(ControlMessage.class);
		kryo.register(SnapshotPacket.class);
		kryo.register(EntityData.class);
		kryo.register(Ping.class);
		kryo.register(GlobalInputState.class);
		kryo.register(Vector2.class);
		kryo.register(RequestPlayer.class);
		kryo.register(AddPlayer.class);
		kryo.register(AddPlayers.class);
		kryo.register(RemoveEntities.class);
		kryo.register(Array.class);
		kryo.register(ObjectMap.class);
		kryo.register(Object[].class);
		kryo.register(ArrayList.class);
		kryo.register(Packet.class);
		kryo.register(ID.class);
	}

	static public class Login {
		public ID id;
	}

	static public class RegistrationRequired {
	}

	static public class Register {
		public ID id;
		public String otherStuff;
	}

	static public class UpdateUser {
		public ID id;
		public String x, y;
	}

	static public class AddUser {
		public GameUser user;
	}

	static public class RemoveUser {
		public ID id;
	}

	static public class MoveUser {
		public float x, y, vx, vy;
		public long time;
	}
	
	static public class NewScene {
		public String imgFile;
		public int x, y;
	}
	
	static public class RequestScene {
		
	}
	
	static public class ControlMessage {
		public boolean up, down, left, right;
		public boolean interact;
		public long time;
	}
	
	static public boolean isControlling(ControlMessage controlMessage) {
		return controlMessage.up || controlMessage.down || controlMessage.left || controlMessage.right || controlMessage.interact;
	}
	
	static public class SnapshotPacket extends Packet<SnapshotPacket> {
		public List<EntityData> entityDatas = new ArrayList<EntityData>();
	}

	static public class EntityData {
		public ID id;
		public float x, y;
		public GlobalInputState input;
	}
	
	static public class Ping {
		public long time;
	}
	
	static public class RequestPlayer {
		public ID id;
	}
	
	static public class AddPlayer {
		public ID id;
		public float x, y, z;
	}
	
	static public class AddPlayers {
		public Array<AddPlayer> players = new Array<AddPlayer>();
	}
	
	static public class RemoveEntities {
		public Array<ID> ids = new Array<ID>();
	}
}
