package com.jharter.game.ashley.systems.network.client;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.jharter.game.ashley.components.Components.FocusComp;
import com.jharter.game.ashley.components.Components.IDComp;
import com.jharter.game.ashley.components.Components.InputComp;
import com.jharter.game.ashley.systems.boilerplate.CustomIteratingSystem;
import com.jharter.game.network.endpoints.GameClient;

public class ClientSendInputSystem extends CustomIteratingSystem {

	private GameClient client;
	
	@SuppressWarnings("unchecked")
	public ClientSendInputSystem(GameClient client) {
		super(Family.all(FocusComp.class, InputComp.class, IDComp.class).get());
		this.client = client;
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		client.tick(deltaTime);
		
		IDComp id = Comp.IDComp.get(entity);
		InputComp in = Comp.InputComp.get(entity);
		
		// We only need to tick the input manager on the client since the server
		// will get input updates via messages.
		in.input.tick(deltaTime);
		
		// Send the input state to the server if there was any activity this tick.
		in.input.maybeSendInputState(client, id.id);
	}

}
