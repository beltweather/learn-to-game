package com.jharter.game.layout;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.jharter.game.ashley.components.Components.AlphaComp;
import com.jharter.game.ashley.components.Components.PositionComp;
import com.jharter.game.ashley.components.Components.SizeComp;
import com.jharter.game.ashley.components.Mapper;

public class LayoutTarget implements Poolable {
	
	public Vector3 position = new Vector3(0, 0, 0);
	public Vector2 scale = new Vector2(0, 0);
	public float alpha = 1f;
	public float angleDegrees = 0f;
	
	private LayoutTarget() {}
	
	public void setFromEntity(Entity entity) {
		PositionComp p = Mapper.PositionComp.get(entity);
		SizeComp s = Mapper.SizeComp.get(entity);
		AlphaComp a = Mapper.AlphaComp.get(entity);
		
		position.set(p.position);
		scale.set(s.scale);
		alpha = a == null ? 1f : a.alpha;
		angleDegrees = p.angleDegrees;
	}

	@Override
	public void reset() {
		position.set(0, 0, 0);
		scale.set(0, 0);
		alpha = 1f;
		angleDegrees = 0f;
	}
	
}
