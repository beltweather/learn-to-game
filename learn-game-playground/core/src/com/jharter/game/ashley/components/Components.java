package com.jharter.game.ashley.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.Pools;
import com.jharter.game.ashley.interactions.Interaction;
import com.jharter.game.control.GameInput;
import com.jharter.game.util.id.ID;

import uk.co.carelesslabs.Enums.EntityType;
import uk.co.carelesslabs.Enums.TileType;
import uk.co.carelesslabs.Enums.ZoneType;

public final class Components {

	private Components() {}
	
	// ------------------- NORMAL COMPONENTS ---------------------------
	
	public static final class IDComp implements Component, Poolable {
		public ID id;
		
		private IDComp() {}

		@Override
		public void reset() {
			id = null;
		}
	}
	
	public static final class PositionComp implements Component, Poolable {
		public Vector3 position = new Vector3(0, 0, 0);
		public Vector2 direction = new Vector2(0, 0);
		public float angleDegrees = 0.0f;
		
		private PositionComp() {}
		
		@Override
		public void reset() {
			position.set(0, 0, 0);
			direction.set(0, 0);
			angleDegrees = 0.0f;
		}
	}
	
	public static final class SizeComp implements Component, Poolable {
		public float width;
		public float height;
		public Vector2 scale = new Vector2(1.0f, 1.0f);
		
		private SizeComp() {}
		
		public float scaledWidth() {
			if(scale.x == 1) {
				return width;
			}
			return scale.x * width;
		}
		
		public float scaledHeight() {
			if(scale.y == 1) {
				return height;
			}
			return scale.y * height;
		}
		
		@Override
		public void reset() {
			width = 0f;
			height = 0f;
			scale.set(1.0f, 1.0f);
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
		public Vector2 velocity = new Vector2(0, 0);

		private VelocityComp() {}
		
		@Override
		public void reset() {
			speed = 0f;
			velocity.set(0, 0);
		}
	}
	
	public static final class CollisionComp implements Component, Poolable {
		public ID collisionWithId;
		public boolean begin;
		
		private CollisionComp() {}
	
		@Override
		public void reset() {
			collisionWithId = null;
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
	
	// ------------------- BOOLEAN COMPONENTS -------------------------
	
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
	
	public static final class InvisibleComp implements Component, Poolable {
		private InvisibleComp() {}
		
		@Override
		public void reset() {}
	}
	
	public static final class CursorComp implements Component, Poolable {
		private CursorComp() {}
		
		@Override
		public void reset() {}
	}
	
	// ---------------- UNSERIALIZABLE COMPONENTS ------------------------------
	
	public static final class CursorInputRegulatorComp implements Component, Poolable {
		private boolean processedMove = false;
		private float processedMoveDelta = 0;
		private float maxProcessedMoveDelta = 0.2f;
		
		private CursorInputRegulatorComp() {}
		
		public boolean ignoreMovement(boolean moved, float deltaTime) {
			if(!moved) {
				processedMove = false;
				processedMoveDelta = 0;
				maxProcessedMoveDelta = 0.2f;
				return true;
			} else if(moved && processedMove) {
				processedMoveDelta += deltaTime;
				if(processedMoveDelta < maxProcessedMoveDelta) {
					return true;
				} else if(maxProcessedMoveDelta > 0.005f){
					maxProcessedMoveDelta /= 1.5f;
				}
			}
			processedMove = true;
			processedMoveDelta = 0;
			return false;
		}
		
		@Override
		public void reset() {
			processedMove = false;
			processedMoveDelta = 0;
			maxProcessedMoveDelta = 0.2f;
		}
	}
	
	public static final class CursorInputComp implements Component, Poolable {
		public Vector2 direction = new Vector2(0, 0);
		public boolean accept = false;
		public boolean cancel = false;
		
		private CursorInputComp() {}
		
		public boolean move() {
			return direction.x != 0 || direction.y != 0;
		}
		
		@Override
		public void reset() {
			direction.set(0, 0);
			accept = false;
			cancel = false;
		}
	}
	
	public static final class ZoneComp implements Component, Poolable {
		public ZoneType zoneType = ZoneType.NONE;
		public int rows = -1;
		public int cols = -1;
		public Array<ID> objects = new Array<ID>();
		
		public void add(EntityBuilder b) {
			add(b.IDComp(), b.ZonePositionComp());
		}
		
		public void add(IDComp id, ZonePositionComp zp) {
			int index = objects.size;
			objects.add(id.id);
			int row = index / cols;
			int col = index % cols;
			zp.zoneType = zoneType;
			zp.row = row;
			zp.col = col;
		}
		
		private ZoneComp() {}
		
		@Override
		public void reset() {
			zoneType = ZoneType.NONE;
			rows = -1;
			cols = -1;
			objects.clear();
		}
		
	}
	
	public static final class ZonePositionComp implements Component, Poolable {
		
		public ZoneType zoneType = ZoneType.NONE;
		public int row = -1;
		public int col = -1;
		private Array<ZonePositionComp> history = new Array<ZonePositionComp>();
		
		private ZonePositionComp() {}
		
		private ZonePositionComp copyForHistory() {
			ZonePositionComp zp = Pools.get(ZonePositionComp.class).obtain();
			zp.zoneType = zoneType;
			zp.row = row;
			zp.col = col;
			// Intentionally ignoring history for copies since we don't use it
			return zp;
		}
		
		public void checkpoint() {
			history.add(copyForHistory());
		}
		
		public void undoCheckpoint() {
			if(history.size == 0) {
				return;
			}
			history.pop();
		}
		
		public boolean tryRevertToLastCheckpoint() {
			if(history.size == 0) {
				return false;
			}
			ZonePositionComp copy = history.pop();
			zoneType = copy.zoneType;
			row = copy.row;
			col = copy.col;
			return true;
		}
		
		public void clearHistory() {
			history.clear();
		}
		
		@Override
		public void reset() {
			zoneType = ZoneType.NONE;
			row = -1;
			col = -1;
			history.clear();
		}
		
	}
	
	public static final class InputComp implements Component, Poolable {
		public GameInput input; // Can't serialize

		private InputComp() {}
		
		@Override
		public void reset() {
			input = null;
		}
	}
	
	public static final class TileComp implements Component, Poolable {
		public TileType type;
		public int size, row, col;
		public String code;
		public TextureRegion secondaryTexture; // Can't serialize

		private TileComp() {}
		
		@Override
		public void reset() {
			type = null;
			size = row = col = 0;
			code = null;
			secondaryTexture = null;
		}
	}
	
	public static final class TextureComp implements Component, Poolable {
		public TextureRegion defaultRegion; // Can't serialize
		public TextureRegion region; // Can't serialize

		private TextureComp() {}
		
		@Override
		public void reset() {
			defaultRegion = null;
			region = null;
		}
	}
	
	public static final class AnimationComp implements Component, Poolable {
		public Animation animation; // Can't serialize
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
		public Body body; // Can't serialize

		private BodyComp() {}
		
		@Override
		public void reset() {
			body = null;
		}
	}
	
	public static final class SensorComp implements Component, Poolable {
		public Body sensor; // Can't serialize
		
		private SensorComp() {}

		@Override
		public void reset() {
			sensor = null;
		}
	}
	
}
