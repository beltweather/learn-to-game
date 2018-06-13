package com.jharter.game.ashley.systems.network;

import com.badlogic.gdx.utils.TimeUtils;
import com.jharter.game.network.endpoints.GameClient;
import com.jharter.game.network.packets.Packet;
import com.jharter.game.network.packets.PacketManager.PacketPair;
import com.jharter.game.stages.GameStage;

public abstract class InterpolatingPacketSystem<T extends Packet<T>> extends PacketSystem<GameClient, T> {

	public InterpolatingPacketSystem(GameStage stage, GameClient client) {
		super(stage, client);
	}

	@Override
	public void update(GameClient client, GameStage stage, float deltaTime) {
		long renderTime = getRenderTime(client, TimeUtils.millis());
		PacketPair<T> pair = getPacketsBeforeAndAfter(renderTime);
    	if(pair != null) {
			update(client, stage, deltaTime, pair, renderTime);
		}
	}
	
	private long getRenderTime(GameClient client, long currentTime) {
    	return currentTime - (client.getPing() + 100);
    }
	
	protected float getInterpolatedValue(PacketPair<T> pair, float valuePast, float valueFuture) {
		return getInterpolatedValue(pair.pastPacket.sendTime, valuePast, pair.futurePacket.sendTime, valueFuture, pair.currentTime);
	}
	
	protected float getInterpolatedValue(long timePast, float valuePast, long timeFuture, float valueFuture, long timeCurrent) {
    	return (timeCurrent - timePast) / (timeFuture - timePast) * (valueFuture - valuePast) + valuePast;
    }

	public abstract void update(GameClient client, GameStage stage, float deltaTime, PacketPair<T> packet, long renderTime);

}