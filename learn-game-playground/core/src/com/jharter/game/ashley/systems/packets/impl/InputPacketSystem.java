package com.jharter.game.ashley.systems.packets.impl;

import com.badlogic.ashley.core.Entity;
import com.jharter.game.ashley.components.Components.InputComp;
import com.jharter.game.ashley.components.Mapper;
import com.jharter.game.ashley.entities.EntityUtil;
import com.jharter.game.ashley.systems.packets.ConsumingPacketSystem;
import com.jharter.game.ashley.systems.packets.impl.Packets.InputPacket;
import com.jharter.game.control.GlobalInputState;
import com.jharter.game.network.GameClient;
import com.jharter.game.network.GameEndPoint;
import com.jharter.game.network.GameServer;
import com.jharter.game.stages.GameStage;
import com.jharter.game.util.id.ID;

public class InputPacketSystem extends ConsumingPacketSystem<InputPacket> {

	public InputPacketSystem(GameStage stage, GameEndPoint endPoint) {
		super(stage, endPoint);
	}

	@Override
	public void update(GameServer server, GameStage stage, float deltaTime, InputPacket packet) {
		GlobalInputState state = packet.inputState;
		ID entityId = state.id;
		Entity entity = EntityUtil.findEntity(entityId);
		if(entity != null) {
			InputComp in = Mapper.InputComp.get(entity);
			if(in != null) {
				in.input.setInputState(state);
			}
		}
	}

	@Override
	public void update(GameClient client, GameStage stage, float deltaTime, InputPacket packet) {
		
	}

	@Override
	public Class<InputPacket> getPacketClass() {
		return InputPacket.class;
	}

}
