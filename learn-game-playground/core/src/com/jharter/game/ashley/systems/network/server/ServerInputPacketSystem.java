package com.jharter.game.ashley.systems.network.server;

import com.badlogic.ashley.core.Entity;
import com.jharter.game.ashley.components.Components.InputComp;
import com.jharter.game.ashley.systems.network.ConsumingPacketSystem;
import com.jharter.game.control.GlobalInputState;
import com.jharter.game.network.endpoints.GameServer;
import com.jharter.game.network.packets.Packets.InputPacket;
import com.jharter.game.stages.GameStage;
import com.jharter.game.util.id.ID;

public class ServerInputPacketSystem extends ConsumingPacketSystem<GameServer, InputPacket> {

	public ServerInputPacketSystem(GameStage stage, GameServer server) {
		super(stage, server);
	}

	@Override
	public void update(GameServer server, GameStage stage, float deltaTime, InputPacket packet) {
		GlobalInputState state = packet.inputState;
		ID entityId = state.id;
		Entity entity = Comp.Entity.get(entityId);
		if(entity != null) {
			InputComp in = Comp.InputComp.get(entity);
			if(in != null) {
				in.input.setInputState(state);
			}
		}
	}

	@Override
	public Class<InputPacket> getPacketClass() {
		return InputPacket.class;
	}

}
