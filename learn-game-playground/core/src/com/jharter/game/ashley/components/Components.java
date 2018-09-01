package com.jharter.game.ashley.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.Pools;
import com.jharter.game.ashley.components.subcomponents.RelativePositionRules;
import com.jharter.game.ashley.components.subcomponents.TurnAction;
import com.jharter.game.ashley.interactions.Interaction;
import com.jharter.game.control.GameInput;
import com.jharter.game.layout.ZoneLayout;
import com.jharter.game.render.ShapeRenderMethod;
import com.jharter.game.util.id.ID;

import uk.co.carelesslabs.Enums.CardType;
import uk.co.carelesslabs.Enums.EntityType;
import uk.co.carelesslabs.Enums.TileType;
import uk.co.carelesslabs.Enums.ZoneType;

public final class Components {

	private Components() {}
	
	public static interface Comp extends Component, Poolable {
		
	}
	
	public static class BoolComp implements Comp {
		private BoolComp() {}
		
		@Override
		public void reset() {
			
		}
	}
	
	// ------------------- BOOL COMPONENTS -----------------------------

	public static final class UntargetableComp extends BoolComp {}
	public static final class FocusComp extends BoolComp {}
	public static final class InvisibleComp extends BoolComp {}
	public static final class DisabledComp extends BoolComp {}
	
	public static final class TurnPhaseStartBattleComp extends BoolComp {}
	public static final class TurnPhaseStartTurnComp extends BoolComp {}
	public static final class TurnPhaseSelectEnemyActionsComp extends BoolComp {}
	public static final class TurnPhaseSelectFriendActionsComp extends BoolComp {}
	public static final class TurnPhasePerformFriendActionsComp extends BoolComp {}
	public static final class TurnPhasePerformEnemyActionsComp extends BoolComp {}
	public static final class TurnPhaseEndTurnComp extends BoolComp {}
	public static final class TurnPhaseEndBattleComp extends BoolComp {}
	public static final class TurnPhaseNoneComp extends BoolComp {}
	
	public static final class ActionQueueableComp extends BoolComp {}
	public static final class ActionQueuedComp extends BoolComp {}
	public static final class ActionReadyComp extends BoolComp {}
	public static final class ActionSpentComp extends BoolComp {}
	
	// ------------------- NORMAL COMPONENTS ---------------------------
	
	public static final class IDComp implements Comp {
		public ID id;
		
		private IDComp() {}

		@Override
		public void reset() {
			id = null;
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
	
	public static final class DescriptionComp implements Comp {
		
		public String name = null;
		
		private DescriptionComp() {}
		
		@Override
		public void reset() {
			name = null;
		}
	}
	
	public static final class SpriteComp implements Comp {
		public Vector3 position = new Vector3(0, 0, 0);
		public Vector2 direction = new Vector2(0, 0);
		public float angleDegrees = 0.0f;
		public RelativePositionRules relativePositionRules = new RelativePositionRules();
		public float width;
		public float height;
		public Vector2 scale = new Vector2(1.0f, 1.0f);
		public float alpha = 1f;
		
		private SpriteComp() {}
		
		public float scaledWidth() {
			return scaledWidth(scale.x);
		}
		
		public float scaledWidth(float scaleX) {
			if(scaleX == 1) {
				return width;
			}
			return scaleX * width;
		}
		
		public float scaledHeight() {
			return scaledHeight(scale.y);
		}
		
		public float scaledHeight(float scaleY) {
			if(scaleY == 1) {
				return height;
			}
			return scaleY * height;
		}
		
		public boolean isRelative() {
			return relativePositionRules.relative;
		}
		
		public boolean setToRelativePosition(Vector3 position) {
			return relativePositionRules.setToRelativePosition(this, position);
		}
		
		@Override 
		public void reset() {
			position.set(0, 0, 0);
			direction.set(0, 0);
			angleDegrees = 0.0f;
			width = 0f;
			height = 0f;
			scale.set(1.0f, 1.0f);
			alpha = 1f;
			relativePositionRules.reset();
		}
	}
	
	public static final class MultiSpriteComp implements Comp {
		public Array<Vector3> positions = new Array<Vector3>();
		public Array<Vector2> scales = new Array<Vector2>();
		public Array<Float> alphas = new Array<Float>();
		public Array<Float> anglesDegrees = new Array<Float>();
		public int size = 0;
		public boolean drawSingle = false;
		
		private MultiSpriteComp() {}
		
		public void clear() {
			positions.clear();
			scales.clear();
			alphas.clear();
			anglesDegrees.clear();
			size = 0;
		}
		
		@Override
		public void reset() {
			clear();
			drawSingle = false;
		}
	}
	
	/*public static final class RelativePositionComp implements Comp {
		public ID baselineID = null;
		public Vector3 margin = new Vector3();
		public Direction xAlign = Direction.CENTER;
		public Direction yAlign = Direction.CENTER;
		public boolean spriteAsOffset = true;
		private Vector3 tempPosition = new Vector3();
		
		private RelativePositionComp() {}
		
		public Vector3 toPosition(SpriteComp s) {
			if(baselineID == null) {
				float xOffset = margin.x;
				float yOffset = margin.y;
				float zOffset = margin.z;
				
				if(spriteAsOffset && s != null) {
					xOffset += s.position.x;
					yOffset += s.position.y;
					zOffset += s.position.z;
				}
				
				tempPosition.set(xOffset, yOffset, zOffset);
				Sys.out.println("X: " + tempPosition.x);
				return tempPosition;
			}
			
			Entity baselineEntity = Mapper.Entity.get(baselineID);
			SpriteComp sBaseline = Mapper.SpriteComp.get(baselineEntity);
			if(baselineEntity == null || s == null || sBaseline == null) {
				return null;
			}
			
			float x = sBaseline.position.x;
			float y = sBaseline.position.y;
			float z = sBaseline.position.z;
			
			switch(xAlign) {
				case WEST:
				case NORTH_WEST:
				case SOUTH_WEST:
					x -= s.scaledWidth();
					break;
				case EAST:
				case NORTH_EAST:
				case SOUTH_EAST:
					x += sBaseline.scaledWidth();
					break;
				case CENTER:
					x += (sBaseline.scaledWidth() - s.scaledWidth()) / 2f;
					break;
				default:
					break;
			}
			
			switch(yAlign) {
				case SOUTH:
				case SOUTH_WEST:
				case SOUTH_EAST:
					y -= s.scaledHeight();
					break;
				case NORTH:
				case NORTH_WEST:
				case NORTH_EAST:
					y += sBaseline.scaledHeight();
					break;
				case CENTER:
					y += (sBaseline.scaledHeight() - s.scaledHeight()) / 2f;
					break;
				default:
					break;
			}
			
			float xOffset = margin.x;
			float yOffset = margin.y;
			float zOffset = margin.z;
			
			if(spriteAsOffset) {
				xOffset += s.position.x;
				yOffset += s.position.y;
				zOffset += s.position.z;
			}
			
			tempPosition.set(x + xOffset, y + yOffset, z + zOffset);
			return tempPosition;
		}
		
		@Override
		public void reset() {
			baselineID = null;
			margin.set(0,0,0);
			xAlign = Direction.CENTER;
			yAlign = Direction.CENTER;
			tempPosition.set(0,0,0);
			spriteAsOffset = true;
		}
	}*/
	
	public static final class AnimatingComp implements Comp {
		
		public int activeCount = 0;
		
		private AnimatingComp() {}
		
		@Override
		public void reset() {
			activeCount = 0;
		}
		
	}
	
	public static final class PlayerComp implements Comp {
		
		public ID battleAvatarID = null;
		
		private PlayerComp() {}

		public Entity getBattleAvatarEntity() {
			return Mapper.Entity.get(battleAvatarID);
		}
		
		@Override
		public void reset() {
			battleAvatarID = null;
		}
	}
	
	public static final class ActivePlayerComp implements Comp {
		
		public ID activePlayerID = null;
		
		private ActivePlayerComp() {}
		
		@Override
		public void reset() {
			activePlayerID = null;
		}
		
	}
	
	public static final class BattleAvatarComp implements Comp {
		
		public ID playerID;
		
		private BattleAvatarComp() {}
		
		@Override
		public void reset() {
			playerID = null;
		}
		
	}
	
	public static final class TargetPositionComp implements Comp {
		public Vector3 position = null;

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
	
	public static final class CardComp implements Comp {
		
		public ID playerID = null;
		public CardType cardType = CardType.NONE;
		public String text = null;
		public String tooltipText = null;
		
		private CardComp() {}
		
		public Entity getCharacterEntity() {
			return Mapper.PlayerComp.get(Mapper.Entity.get(playerID)).getBattleAvatarEntity();
		}
		
		@Override
		public void reset() {
			playerID = null;
			cardType = CardType.NONE;
			text = null;
			tooltipText = null;
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

	public static final class TurnActionComp implements Comp {
		
		public TurnAction turnAction = Pools.get(TurnAction.class).obtain();
		
		private TurnActionComp() {}
		
		@Override
		public void reset() {
			turnAction.reset();
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
	
	public static final class CursorComp implements Comp {
		public ID turnActionEntityID = null;
		public ID lastZoneID = null;
		//public ID playerID = null;
		
		private CursorComp() {}
		
		public TurnAction getTurnAction() {
			if(turnActionEntityID == null) {
				return null;
			}
			TurnActionComp t = Mapper.TurnActionComp.get(Mapper.Entity.get(turnActionEntityID));
			if(t == null) {
				return null;
			}
			return t.turnAction;
		}
		
		public ID playerID() {
			return Mapper.getPlayerEntityID();
		}
		
		@Override
		public void reset() {
			turnActionEntityID = null;
			lastZoneID = null;
			//playerID = null;
		}
	}
	
	public static final class TurnTimerComp implements Comp {
		public float accumulator = 0;
		public float maxTurnTimeSec = 0;
		public boolean play = true;
		
		private TurnTimerComp() {}
		
		public void stop() {
			play = false;
			accumulator = 0;
		}
		
		public void start() {
			play = true;
			accumulator = 0;
		}
		
		public boolean isStopped() {
			return !play;
		}
		
		public boolean isOvertime() {
			return accumulator > maxTurnTimeSec;
		}
		
		public void increment(float deltaTime) {
			accumulator += deltaTime;
		}
		
		@Override
		public void reset() {
			accumulator = 0;
			maxTurnTimeSec = 0;
			play = true;
		}
	}
	
	public static final class TurnPhaseComp implements Comp {
		private TurnPhaseComp() {}
		
		@Override
		public void reset() {}
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
	
	public static final class ChangeZoneComp implements Comp {
		
		public boolean instantChange = true;
		public ID oldZoneID = null;
		public ID newZoneID = null;
		public int newIndex = -1;
		public boolean checkpoint = false;
		public boolean useNextIndex = false;
		
		private ChangeZoneComp() {}
		
		@Override
		public void reset() {
			instantChange = true;
			oldZoneID = null;
			newZoneID = null;
			newIndex = -1;
			checkpoint = false;
			useNextIndex = false;
		}
		
	}
	
	public static final class ZoneComp implements Comp {
		public ID zoneID = null;
		public ZoneType zoneType = ZoneType.NONE;
		private Array<ID> internalObjectIDs = new Array<ID>();
		public ImmutableArray<ID> objectIDs = new ImmutableArray<ID>(internalObjectIDs);
		public ZoneLayout layout = null;
		
		private ZoneComp() {
			
		}
		
		public boolean hasIndex(int index) {
			return index >= 0 && index < internalObjectIDs.size;
		}
		
		public void add(EntityBuilder b) {
			add(b.IDComp().id, b.ZonePositionComp());
		}
		
		public void add(ID id, ZonePositionComp zp) {
			zp.index = internalObjectIDs.size;
			internalObjectIDs.add(id);
			zp.zoneID = zoneID;
		}
		
		public void remove(ID id) {
			internalObjectIDs.removeValue(id, false);
			for(int i = 0; i < internalObjectIDs.size; i++) {
				ID oID = internalObjectIDs.get(i);
				Entity obj = Mapper.Entity.get(oID);
				ZonePositionComp zp = Mapper.ZonePositionComp.get(obj);
				zp.index = i;
			}
		}
		
		@Override
		public void reset() {
			zoneID = null;
			zoneType = ZoneType.NONE;
			internalObjectIDs.clear();
			layout = null;
		}
		
	}
	
	public static final class ZonePositionComp implements Comp {
		
		public ID zoneID = null;
		public int index = -1;
		private transient Array<ZonePositionComp> history = new Array<ZonePositionComp>();
		
		private ZonePositionComp() {}
		
		public ZoneComp getZoneComp() {
			if(zoneID == null) {
				return null;
			}
			return Mapper.ZoneComp.get(this);
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
			zoneID = copy.zoneID;
			index = copy.index;
			return true;
		}
		
		public void clearHistory() {
			history.clear();
		}
		
		private ZonePositionComp copyForHistory() {
			ZonePositionComp zp = Pools.get(ZonePositionComp.class).obtain();
			zp.zoneID = zoneID;
			zp.index = index;
			// Intentionally ignoring history for copies since we don't use it
			return zp;
		}
		
		@Override
		public void reset() {
			zoneID = null;
			index = -1;
			history.clear();
		}
		
	}
	
	public static final class ZonePositionPointerComp implements Comp {
		
		public ID zoneID = null;
		public int index = -1;
		private transient Array<ZonePositionComp> history = new Array<ZonePositionComp>();
		
		private ZonePositionPointerComp() {}
		
		public ZoneComp getZoneComp() {
			if(zoneID == null) {
				return null;
			}
			return Mapper.ZoneComp.get(this);
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
			zoneID = copy.zoneID;
			index = copy.index;
			return true;
		}
		
		public void clearHistory() {
			history.clear();
		}
		
		private ZonePositionComp copyForHistory() {
			ZonePositionComp zp = Pools.get(ZonePositionComp.class).obtain();
			zp.zoneID = zoneID;
			zp.index = index;
			// Intentionally ignoring history for copies since we don't use it
			return zp;
		}
		
		@Override
		public void reset() {
			zoneID = null;
			index = -1;
			history.clear();
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
	
	public static final class ShapeRenderComp implements Comp {
		public ShapeRenderMethod renderMethod = null;
		
		private ShapeRenderComp() {}
		
		@Override
		public void reset() {
			renderMethod = null;
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
