package com.jharter.game.network.packets;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pools;
import com.jharter.game.control.GlobalInputState;
import com.jharter.game.network.endpoints.GameNetwork.AddPlayer;
import com.jharter.game.network.endpoints.GameNetwork.EntityData;
import com.jharter.game.util.id.ID;

public class Packets {
	
	private Packets() {}
	
	private static <T extends Packet> T get(Class<T> klass) {
		return Pools.get(klass).obtain();
	}
	
	public static class AddPlayersPacket extends Packet<AddPlayersPacket> {
		public static AddPlayersPacket newInstance() {
			return get(AddPlayersPacket.class);
		}
		
		public Array<AddPlayer> players = new Array<AddPlayer>();
		
		private AddPlayersPacket() {}
		
		@Override
		public void reset() {
			players.clear();
		}
	}

	public static class InputPacket extends Packet<InputPacket> {
		public static InputPacket newInstance() {
			return get(InputPacket.class);
		}
		
		public static InputPacket newInstance(GlobalInputState inputState) {
			InputPacket packet = newInstance();
			packet.inputState = inputState;
			return packet;
		}
		
		public GlobalInputState inputState;
	
		private InputPacket() {}

		@Override
		public void reset() {
			inputState = null;
		}
	}
	
	public static class RegisterPlayerPacket extends Packet<RegisterPlayerPacket> {
		public static RegisterPlayerPacket newInstance() {
			return get(RegisterPlayerPacket.class);
		}
		
		public static RegisterPlayerPacket newInstance(ID id) {
			RegisterPlayerPacket packet = get(RegisterPlayerPacket.class);
			packet.id = id;
			return packet;
		}
		
		public ID id;
		
		private RegisterPlayerPacket() {}
		
		@Override
		public void reset() {
			id = null;
		}
	}
	
	public static class RequestEntityPacket extends Packet<RequestEntityPacket> {
		public static RequestEntityPacket newInstance() {
			return get(RequestEntityPacket.class);
		}
		
		public static RequestEntityPacket newInstance(ID id) {
			RequestEntityPacket packet = get(RequestEntityPacket.class);
			packet.id = id;
			return packet;
		}
		
		public ID id;
		
		private RequestEntityPacket() {}

		@Override
		public void reset() {
			id = null;
		}
	}
	
	public static class SnapshotPacket extends Packet<SnapshotPacket> {
		public static SnapshotPacket newInstance() {
			return get(SnapshotPacket.class);
		}
		
		public ObjectMap<ID, EntityData> entityDatas = new ObjectMap<ID, EntityData>();
		
		private SnapshotPacket() {}

		@Override
		public void reset() {
			entityDatas.clear();
		}
	}
	
	public static class RemoveEntityPacket extends Packet<RemoveEntityPacket> {
		public static RemoveEntityPacket newInstance() {
			return get(RemoveEntityPacket.class);
		}
		
		public static RemoveEntityPacket newInstance(ID id) {
			RemoveEntityPacket packet = get(RemoveEntityPacket.class);
			packet.id = id;
			return packet;
		}
		
		public ID id; // Used for singleton ids
		public Array<ID> ids; // Lazily initialized for multiple ids
		
		private RemoveEntityPacket() {}
		
		@Override
		public void reset() {
			id = null;
			ids = null;
		}
	}

}
