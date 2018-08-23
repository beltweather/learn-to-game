package com.jharter.game.ashley.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.Pools;
import com.jharter.game.ashley.components.subcomponents.Callback;
import com.jharter.game.ashley.components.subcomponents.VoidCallback;
import com.jharter.game.ashley.interactions.Interaction;
import com.jharter.game.control.GameInput;
import com.jharter.game.util.id.ID;

import uk.co.carelesslabs.Enums.CardType;
import uk.co.carelesslabs.Enums.EntityType;
import uk.co.carelesslabs.Enums.TileType;
import uk.co.carelesslabs.Enums.ZoneType;

public final class Components {

	private Components() {}
	
	public static interface Comp extends Component, Poolable {
		
	}
	
	// ------------------- NORMAL COMPONENTS ---------------------------
	
	public static final class IDComp implements Comp {
		public ID id;
		
		private IDComp() {}

		@Override
		public void reset() {
			id = null;
		}
	}
	
	public static final class PositionComp implements Comp {
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
	
	public static final class MultiPositionComp implements Comp {
		public Array<Vector3> positions = new Array<Vector3>();
		
		private MultiPositionComp() {}
		
		@Override
		public void reset() {
			positions.clear();
		}
	}
	
	public static final class SizeComp implements Comp {
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
	
	public static final class TypeComp implements Comp {
		public EntityType type;

		private TypeComp() {}
		
		@Override
		public void reset() {
			type = null;
		}
	}
	
	public static final class TargetPositionComp implements Comp {
		public Vector3 position;

		private TargetPositionComp() {}
		
		@Override
		public void reset() {
			position = null;
		}
	}
	
	public static final class VelocityComp implements Comp {
		public float speed = 0.0f;
		public Vector2 velocity = new Vector2(0, 0);

		private VelocityComp() {}
		
		@Override
		public void reset() {
			speed = 0f;
			velocity.set(0, 0);
		}
	}
	
	public static final class CollisionComp implements Comp {
		public ID collisionWithId;
		public boolean begin;
		
		private CollisionComp() {}
	
		@Override
		public void reset() {
			collisionWithId = null;
			begin = false;
		}
	}
	
	public static final class RemoveComp implements Comp {
		public boolean requestRemove = false;
		public boolean remove = false;
		
		private RemoveComp() {}

		@Override
		public void reset() {
			requestRemove = false;
			remove = false;
		}
	}
	
	public static final class InteractComp implements Comp {
		public Array<ID> interactables = new Array<ID>();
		public Interaction interaction;

		private InteractComp() {}
		
		@Override
		public void reset() {
			interactables.clear();
			interaction = null;
		}
	}
	
	public static final class DescriptionComp implements Comp {
		
		public String name = null;
		
		private DescriptionComp() {}
		
		@Override
		public void reset() {
			name = null;
		}
	}
	
	public static final class CardComp implements Comp {
		
		public ID ownerID;
		public CardType cardType = CardType.NONE;
		
		private CardComp() {}
		
		@Override
		public void reset() {
			ownerID = null;
			cardType = CardType.NONE;
		}
		
	}
	
	public static final class StatsComp implements Comp {
		
		public int level = 0;
		public int experience = 0;
		public int power = 0;
		public int mPower = 0;
		public int defense = 0;
		public int mDefense = 0;
		public int evasion = 0;
		public int mEvasion = 0;
		public int stamina = 0;
		
		private StatsComp() {}
		
		@Override
		public void reset() {
			level = 0;
			experience = 0;
			power = 0;
			mPower = 0;
			defense = 0;
			mDefense = 0;
			evasion = 0;
			mEvasion = 0;
			stamina = 0;
		}
	}
	
	public static final class VitalsComp implements Comp {
		
		public int maxHealth = 0;
		public int weakHealth = 0;
		public int health = 0;
		
		private VitalsComp() {}
		
		public void heal(int hp) {
			health += hp;
			if(health > maxHealth) {
				health = maxHealth;
			}
		}
		
		public void damage(int hp) {
			health -= hp;
			if(health < 0) {
				health = 0;
			}
		}
		
		public boolean isDead() {
			return health == 0;
		}
		
		public boolean isNearDeath() {
			return health <= weakHealth && !isDead();
		}
		
		@Override
		public void reset() {
			maxHealth = 0;
			weakHealth = 0;
			health = 0;
		}
		
	}

	public static final class ActionQueueableComp implements Comp {
		private ActionQueueableComp() {}
		@Override public void reset() {}
	}
	
	public static final class ActionQueuedComp implements Comp {
		private ActionQueuedComp() {}
		@Override public void reset() {}
	}
	
	public static final class ActionReadyComp implements Comp {
		private ActionReadyComp() {}
		@Override public void reset() {}
	}
	
	public static final class ActionSpentComp implements Comp {
		private ActionSpentComp() {}
		@Override public void reset() {}
	}
	
	public static final class TurnActionComp implements Comp {
		
		public Array<ZoneType> targetZoneTypes = new Array<ZoneType>();
		public Array<ID> targetIDs = new Array<ID>();
		public VoidCallback<TurnActionComp> acceptCallback = null;
		public Callback<Entity, Boolean> validTargetCallback = null;
		public int defaultMultiplicity = 1;
		public int multiplicity = 1;
		public boolean defaultAll = false;
		public boolean all = false;
		public int priority = 0;
		
		private TurnActionComp() {}
		
		public Entity getEntity(int index) {
			if(index < 0 || index >= targetIDs.size) {
				return null;
			}
			return Mapper.Entity.get(targetIDs.get(index));
		}
		
		public ZoneType getTargetZoneType() {
			if(hasAllTargets()) {
				return ZoneType.NONE;
			}
			return targetZoneTypes.get(targetIDs.size-1);
		}
		
		public ZoneType getNextTargetZoneType() {
			return getNextTargetZoneType(0);
		}
		
		public ZoneType getNextTargetZoneType(int depth) {
			if(hasAllTargets()) {
				return ZoneType.NONE;
			}
			int index = targetIDs.size + depth;
			if(index >= targetZoneTypes.size) {
				return ZoneType.NONE;
			}
			return targetZoneTypes.get(index);
		}
		
		public boolean hasAllTargets() {
			return targetZoneTypes.size == targetIDs.size - 1;
		}
		
		public void addTarget(Entity entity) {
			targetIDs.add(Mapper.IDComp.get(entity).id);
		}
		
		public boolean isValidTarget(Entity entity) {
			if(entity == null) {
				return false;
			}
			
			if(validTargetCallback != null) {
				return validTargetCallback.call(entity);
			}
			
			// Special check for cards that modify other cards
			CardComp c = Mapper.CardComp.get(entity);
			ZonePositionComp zp = Mapper.ZonePositionComp.get(entity);
			if(c != null && zp != null) {
				return c.cardType != CardType.TARGET_CARD || zp.zoneType != ZoneType.ACTIVE_CARD;
			}
			
			return true;
		}
		
		public void performAcceptCallback() {
			if(acceptCallback != null) {
				if(multiplicity == 1) {
					acceptCallback.call(this);
				} else {
					for(int i = 0; i < multiplicity; i++) {
						acceptCallback.call(this);
					}
					multiplicity = defaultMultiplicity;
				}
			}
		}	
			
		@Override
		public void reset() {
			targetZoneTypes.clear();
			targetIDs.clear();
			acceptCallback = null;
			validTargetCallback = null;
			multiplicity = 1;
			all = false;
			defaultMultiplicity = 1;
			defaultAll = false;
			priority = 0;
		}
		
	}
	
	public static final class ActiveCardComp implements Comp {
		
		public ID activeCardID = null;
		
		private ActiveCardComp() {}
		
		public boolean hasCard() {
			return activeCardID != null;
		}
		
		public CardComp getCardComp() {
			return Mapper.CardComp.get(Mapper.Entity.get(activeCardID));
		}
		
		@Override
		public void reset() {
			activeCardID = null;
		}
	}
	
	// ------------------- BOOLEAN COMPONENTS -------------------------
	
	public static final class PlayerComp implements Comp {
		private PlayerComp() {}
		
		@Override
		public void reset() {}
	}
	
	public static final class FocusComp implements Comp {
		private FocusComp() {}
		
		@Override
		public void reset() {}
	}
	
	public static final class InvisibleComp implements Comp {
		private InvisibleComp() {}
		
		@Override
		public void reset() {}
	}
	
	public static final class CursorComp implements Comp {
		public ID turnActionEntityID = null;
		
		private CursorComp() {}
		
		public boolean hasTargetingComp() {
			return turnActionEntityID != null;
		}
		
		public TurnActionComp getTurnActionComp() {
			if(turnActionEntityID == null) {
				return null;
			}
			return Mapper.TurnActionComp.get(Mapper.Entity.get(turnActionEntityID));
		}
		
		@Override
		public void reset() {
			turnActionEntityID = null;
		}
	}
	
	public static final class TurnTimerComp implements Comp {
		public float accumulator = 0;
		public float turnTime = 0;
		
		private TurnTimerComp() {}
		
		@Override
		public void reset() {
			accumulator = 0;
			turnTime = 0;
		}
	}
	
	// ---------------- UNSERIALIZABLE COMPONENTS ------------------------------
	
	public static final class CursorInputRegulatorComp implements Comp {
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
	
	public static final class CursorInputComp implements Comp {
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
	
	public static final class ZoneComp implements Comp {
		private ZoneType zoneType = ZoneType.NONE;
		private Array<ID> objects = new Array<ID>();
		private boolean dirty = true;
		
		public void zoneType(ZoneType zoneType) {
			this.zoneType = zoneType;
			dirty = true;
		}

		public ZoneType zoneType() {
			return zoneType;
		}
		
		public boolean isDirty() {
			return dirty;
		}
		
		public boolean hasIndex(ZonePositionComp zp) {
			return hasIndex(zp.index);
		}
		
		public boolean hasIndex(int index) {
			return index >= 0 && index < objects.size;
		}
		
		public void add(EntityBuilder b) {
			add(b.IDComp(), b.ZonePositionComp());
		}
		
		public void add(IDComp id, ZonePositionComp zp) {
			add(id.id, zp);
		}
		
		public void add(ID id, ZonePositionComp zp) {
			zp.index = objects.size;
			objects.add(id);
			zp.zoneType = zoneType;
			dirty = true;
		}
		
		public ID getID(ZonePositionComp zp) {
			int index = zp.index;
			if(index < 0 || index >= objects.size) {
				return null;
			}
			return objects.get(index);
		}
		
		public Entity getEntity(ZonePositionComp zp) {
			ID id = getID(zp);
			if(id == null) {
				return null;
			}
			return Mapper.Entity.get(id);
		}
		
		public void remove(IDComp id) {
			remove(id.id);
		}
		
		public void remove(ID id) {
			objects.removeValue(id, false);
			for(int i = 0; i < objects.size; i++) {
				ID oID = objects.get(i);
				Entity obj = Mapper.Entity.get(oID);
				ZonePositionComp zp = Mapper.ZonePositionComp.get(obj);
				zp.index = i;
			}
			dirty = true;
		}
		
		public int size() {
			return objects.size;
		}
		
		public ID get(int index) {
			return objects.get(index);
		}
		
		private ZoneComp() {}
		
		@Override
		public void reset() {
			zoneType = ZoneType.NONE;
			objects.clear();
			dirty = true;
		}
		
	}
	
	public static final class ZonePositionComp implements Comp {
		
		private ZoneType zoneType = ZoneType.NONE;
		private int index = -1;
		private transient Array<ZonePositionComp> history = new Array<ZonePositionComp>();
		private boolean dirty = true;
		
		private ZonePositionComp() {}
		
		public ZoneComp getZoneComp() {
			if(zoneType == ZoneType.NONE) {
				return null;
			}
			return Mapper.ZoneComp.get(this);
		}
		
		public int index() {
			return index;
		}
		
		public void index(int index) {
			this.index = index;
			dirty = true;
		}
		
		public ZoneType zoneType() {
			return zoneType;
		}
		
		public void zoneType(ZoneType zoneType) {
			this.zoneType = zoneType;
			dirty = true;
		}
		
		public boolean isDirty() {
			return dirty;
		}
		
		private ZonePositionComp copyForHistory() {
			ZonePositionComp zp = Pools.get(ZonePositionComp.class).obtain();
			zp.zoneType = zoneType;
			zp.index = index;
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
			index = copy.index;
			return true;
		}
		
		public void clearHistory() {
			history.clear();
		}
		
		@Override
		public void reset() {
			zoneType = ZoneType.NONE;
			index = -1;
			history.clear();
			dirty = true;
		}
		
	}
	
	public static final class InputComp implements Comp {
		public GameInput input; // Can't serialize

		private InputComp() {}
		
		@Override
		public void reset() {
			input = null;
		}
	}
	
	public static final class TileComp implements Comp {
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
	
	public static final class TextureComp implements Comp {
		public TextureRegion defaultRegion; // Can't serialize
		public TextureRegion region; // Can't serialize

		private TextureComp() {}
		
		@Override
		public void reset() {
			defaultRegion = null;
			region = null;
		}
	}
	
	public static final class AnimationComp implements Comp {
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
	
	public static final class BodyComp implements Comp {
		public Body body; // Can't serialize

		private BodyComp() {}
		
		@Override
		public void reset() {
			body = null;
		}
	}
	
	public static final class SensorComp implements Comp {
		public Body sensor; // Can't serialize
		
		private SensorComp() {}

		@Override
		public void reset() {
			sensor = null;
		}
	}
	
}
