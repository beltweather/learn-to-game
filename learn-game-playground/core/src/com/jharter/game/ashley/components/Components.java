package com.jharter.game.ashley.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.jharter.game.ashley.interactions.Interaction;
import com.jharter.game.control.Input;
import com.jharter.game.util.id.ID;

import uk.co.carelesslabs.Enums.EntityType;
import uk.co.carelesslabs.Enums.TileType;

public final class Components {

	private Components() {}
	
	public static final class PlayerComp implements Component, Poolable {
		private PlayerComp() {}
		
		@Override
		public void reset() {}
	}
	
	public static final class FocusComp implements Component, Poolable {
		private FocusComp() {}
		
		@Override
		public void reset() {}
	}
	
	public static final class IDComp implements Component, Poolable {
		public ID id;
		
		private IDComp() {}

		@Override
		public void reset() {
			id = null;
		}
	}
	
	public static final class PositionComp implements Component, Poolable {
		public Vector3 position = new Vector3();
		public Vector2 direction = new Vector2();

		private PositionComp() {}
		
		@Override
		public void reset() {
			position = new Vector3();
			direction = new Vector2();
		}
	}
	
	public static final class SizeComp implements Component, Poolable {
		public float width;
		public float height;

		private SizeComp() {}
		
		@Override
		public void reset() {
			width = 0f;
			height = 0f;
		}
	}
	
	public static final class TypeComp implements Component, Poolable {
		public EntityType type;

		private TypeComp() {}
		
		@Override
		public void reset() {
			type = null;
		}
	}
	
	public static final class TileComp implements Component, Poolable {
		public TileType type;
		public int size, row, col;
		public String code;
		public TextureRegion secondaryTexture;

		private TileComp() {}
		
		@Override
		public void reset() {
			type = null;
			size = row = col = 0;
			code = null;
			secondaryTexture = null;
		}
	}
	
	public static final class VisualComp implements Component, Poolable {
		public TextureRegion defaultRegion;
		public TextureRegion region;

		private VisualComp() {}
		
		@Override
		public void reset() {
			defaultRegion = null;
			region = null;
		}
	}
	
	public static final class AnimationComp implements Component, Poolable {
		public Animation animation;
		public boolean looping = true;
		public float time = 0;

		private AnimationComp() {}
		
		@Override
		public void reset() {
			animation = null;
			looping = true;
			time = 0;
		}
	}
	
	public static final class BodyComp implements Component, Poolable {
		public Body body;

		private BodyComp() {}
		
		@Override
		public void reset() {
			body = null;
		}
	}
	
	public static final class SensorComp implements Component, Poolable {
		public Body sensor;
		
		private SensorComp() {}

		@Override
		public void reset() {
			sensor = null;
		}
	}
	
	public static final class TargetPositionComp implements Component, Poolable {
		public Vector3 position;

		private TargetPositionComp() {}
		
		@Override
		public void reset() {
			position = null;
		}
	}
	
	public static final class VelocityComp implements Component, Poolable {
		public float speed = 0.0f;
		public Vector2 velocity = new Vector2();

		private VelocityComp() {}
		
		@Override
		public void reset() {
			speed = 0f;
			velocity = new Vector2();
		}
	}
	
	public static final class CollisionComp implements Component, Poolable {
		public ID id;
		public boolean begin;
		
		private CollisionComp() {}
	
		@Override
		public void reset() {
			id = null;
			begin = false;
		}
	}
	
	public static final class RemoveComp implements Component, Poolable {
		public boolean requestRemove = false;
		public boolean remove = false;
		
		private RemoveComp() {}

		@Override
		public void reset() {
			requestRemove = false;
			remove = false;
		}
	}
	
	public static final class InputComp implements Component, Poolable {
		public Input input;

		private InputComp() {}
		
		@Override
		public void reset() {
			input = null;
		}
	}
	
	
	public static final class InteractComp implements Component, Poolable {
		public Array<ID> interactables = new Array<ID>();
		public Interaction interaction;

		private InteractComp() {}
		
		@Override
		public void reset() {
			interactables.clear();
			interaction = null;
		}
	}
	
	public static final class InvisibleComp implements Component, Poolable {

		private InvisibleComp() {}
		
		@Override
		public void reset() {
			
		}
	
	}
	
}
