package com.jharter.game.ashley.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.jharter.game.ashley.components.Components.FocusComp;
import com.jharter.game.ashley.components.Components.IDComp;
import com.jharter.game.ashley.components.Components.InputComp;
import com.jharter.game.ashley.components.Mapper;
import com.jharter.game.server.GameClient;

public class ClientSendInputSystem extends IteratingSystem {

	private GameClient client;
	
	@SuppressWarnings("unchecked")
	public ClientSendInputSystem(GameClient client) {
		super(Family.all(FocusComp.class, InputComp.class, IDComp.class).get());
		this.client = client;
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		client.tick(deltaTime);
		
		IDComp id = Mapper.IDComp.get(entity);
		InputComp in = Mapper.InputComp.get(entity);
		
		// We only need to tick the input manager on the client since the server
		// will get input updates via messages.
		in.input.tick(deltaTime);
		
		// Send the input state to the server if there was any activity this tick.
		in.input.maybeSendInputState(client, id.id);
	}

}
