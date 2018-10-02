package com.jharter.game.ecs.components;

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
import com.jharter.game.control.GameInput;
import com.jharter.game.ecs.components.Components.ChangeZoneComp;
import com.jharter.game.ecs.components.Components.ZoneComp;
import com.jharter.game.ecs.components.subcomponents.Interaction;
import com.jharter.game.ecs.components.subcomponents.RelativePositionRules;
import com.jharter.game.ecs.components.subcomponents.TurnAction;
import com.jharter.game.ecs.components.subcomponents.TurnTimer;
import com.jharter.game.ecs.entities.EntityBuilder;
import com.jharter.game.layout.ZoneLayout;
import com.jharter.game.render.ShapeRenderMethod;
import com.jharter.game.util.id.ID;

import uk.co.carelesslabs.Enums.CardType;
import uk.co.carelesslabs.Enums.EntityType;
import uk.co.carelesslabs.Enums.TileType;
import uk.co.carelesslabs.Enums.ZoneType;

public final class Components {

	private Components() {}
	
	// ------------------- SUPERCLASS AND INTERFACe COMPONENTS --------
	
	/**
	 * Convenience class to ensure that our components are all poolable.
	 */
	private static interface C extends Component, Poolable {}
	
	/**
	 * Simple boolean component, no data inside.
	 */
	private static class B implements C {
		private B() {}
		@Override public void reset() {}
	}
	
	/**
	 * Special interface that denotes a component is supposed to only
	 * exist on one entity total in the entire engine.
	 */
	public static interface Unique {}
	
	// ------------------- BOOL COMPONENTS -----------------------------

	public static final class FocusComp extends B implements Unique {}
	
	public static final class TurnPhaseComp extends B implements Unique {}
	public static final class TurnPhaseStartBattleComp extends B implements Unique {}
	public static final class TurnPhaseStartTurnComp extends B implements Unique {}
	public static final class TurnPhaseSelectEnemyActionsComp extends B implements Unique {}
	public static final class TurnPhaseSelectFriendActionsComp extends B implements Unique {}
	public static final class TurnPhasePerformFriendActionsComp extends B implements Unique {}
	public static final class TurnPhasePerformEnemyActionsComp extends B implements Unique {}
	public static final class TurnPhaseEndTurnComp extends B implements Unique {}
	public static final class TurnPhaseEndBattleComp extends B implements Unique {}
	public static final class TurnPhaseNoneComp extends B implements Unique {}
	
	public static final class PendingTurnActionComp extends B {}
	public static final class TargetableComp extends B {}
	public static final class UntargetableComp extends B {}
	public static final class InvisibleComp extends B {}
	public static final class DisabledComp extends B {}
	
	public static final class ActionQueueableComp extends B {}
	public static final class ActionReadyComp extends B {}
	public static final class CleanupTurnActionComp extends B {}
	
	public static final class ToDiscardZoneComp extends B {}
	
	// ------------------- NORMAL COMPONENTS ---------------------------
	
	public static final class IDComp implements C {
		public ID id;
		
		private IDComp() {}

		@Override
		public void reset() {
			id = null;
		}
	}
	
	public static final class TypeComp implements C {
		public EntityType type;

		private TypeComp() {}
		
		@Override
		public void reset() {
			type = null;
		}
	}
	
	public static final class DescriptionComp implements C {
		public String name = null;
		
		private DescriptionComp() {}
		
		@Override
		public void reset() {
			name = null;
		}
	}
	
	public static final class SpriteComp implements C {
		public Vector3 position = new Vector3(0, 0, 0);
		public Vector2 direction = new Vector2(0, 0);
		public float angleDegrees = 0.0f;
		public RelativePositionRules relativePositionRules = new RelativePositionRules();
		public float width;
		public float height;
		public Vector2 scale = new Vector2(1.0f, 1.0f);
		public float alpha = 1f;
		
		private SpriteComp() {}
		
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
	
	public static class SpriteCompUtil extends CompUtil<SpriteComp> {
		
		public float scaledWidth() {
			return scaledWidth(c.scale.x);
		}
		
		public float scaledWidth(float scaleX) {
			if(scaleX == 1) {
				return c.width;
			}
			return scaleX * c.width;
		}
		
		public float scaledHeight() {
			return scaledHeight(c.scale.y);
		}
		
		public float scaledHeight(float scaleY) {
			if(scaleY == 1) {
				return c.height;
			}
			return scaleY * c.height;
		}
	}
	
	public static final class MultiSpriteComp implements C {
		public Array<Vector3> positions = new Array<Vector3>();
		public Array<Vector2> scales = new Array<Vector2>();
		public Array<Float> alphas = new Array<Float>();
		public Array<Float> anglesDegrees = new Array<Float>();
		public boolean reflectAngle = false;
		public int size = 0;
		public boolean drawSingle = false;
		
		private MultiSpriteComp() {}
		
		/**
		 * Use this method to restore multi sprite comp to its default
		 * empyty values, but still keep rules in tact (unlike reset)
		 */
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
			reflectAngle = false;
		}
	}
	
	public static final class AnimatingComp implements C {
		public int activeCount = 0;
		
		private AnimatingComp() {}
		
		@Override
		public void reset() {
			activeCount = 0;
		}
	}
	
	public static final class PlayerComp extends B {
		private PlayerComp() {}
	}
	
	public static final class ActivePlayerComp implements C, Unique {
		public ID activePlayerID = null;
		public Array<ID> spentPlayers = new Array<ID>();
		
		private ActivePlayerComp() {}
		
		@Override
		public void reset() {
			activePlayerID = null;
			spentPlayers.clear();
		}
	}
	
	public static final class TargetPositionComp implements C {
		public Vector3 position = null;

		private TargetPositionComp() {}
		
		@Override
		public void reset() {
			position = null;
		}
	}
	
	public static final class VelocityComp implements C {
		public float speed = 0.0f;
		public Vector2 velocity = new Vector2(0, 0);

		private VelocityComp() {}
		
		@Override
		public void reset() {
			speed = 0f;
			velocity.set(0, 0);
		}
	}
	
	public static final class CollisionComp implements C {
		public ID collisionWithId;
		public boolean begin;
		
		private CollisionComp() {}
	
		@Override
		public void reset() {
			collisionWithId = null;
			begin = false;
		}
	}
	
	public static final class RemoveComp implements C {
		public boolean requestRemove = false;
		public boolean remove = false;
		
		private RemoveComp() {}

		@Override
		public void reset() {
			requestRemove = false;
			remove = false;
		}
	}
	
	public static final class InteractComp implements C {
		public Array<ID> interactables = new Array<ID>();
		public Interaction interaction;

		private InteractComp() {}
		
		@Override
		public void reset() {
			interactables.clear();
			interaction = null;
		}
	}
	
	public static final class CardComp implements C {
		public CardType cardType = CardType.NONE;
		public String text = null;
		public String tooltipText = null;
		public ID ownerID = null;
		
		private CardComp() {}
		
		@Override
		public void reset() {
			cardType = CardType.NONE;
			text = null;
			tooltipText = null;
			ownerID = null;
		}
	}
	
	public static final class StatsComp implements C {
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
	
	public static final class VitalsComp implements C {
		public int maxHealth = 0;
		public int weakHealth = 0;
		public int health = 0;
		
		private VitalsComp() {}
		
		@Override
		public void reset() {
			maxHealth = 0;
			weakHealth = 0;
			health = 0;
		}
	}
	
	public static class VitalsCompUtil extends CompUtil<VitalsComp> {
		
		public void heal(int hp) {
			c.health += hp;
			if(c.health > c.maxHealth) {
				c.health = c.maxHealth;
			}
		}
		
		public void damage(int hp) {
			c.health -= hp;
			if(c.health < 0) {
				c.health = 0;
			}
		}
		
		public boolean isDead() {
			return c.health <= 0;
		}
		
		public boolean isNearDeath() {
			return c.health <= c.weakHealth && !isDead();
		}
		
	}

	public static final class TurnActionComp implements C, Unique {
		public TurnAction turnAction = null;
		
		private TurnActionComp() {}
		
		@Override
		public void reset() {
			turnAction = null;
		}
	}
	
	public static final class ActiveTurnActionComp implements C {
		public ID activeTurnActionID = null;
		
		private ActiveTurnActionComp() {}
		
		@Override
		public void reset() {
			activeTurnActionID = null;
		}
	}
	
	public static final class CursorComp implements C, Unique {
		public ID turnActionID = null;
		public ID lastTargetID = null; // Remember the ID of the last target, possibly only going to be used for layout purposes
		public ID targetID = null; // The id of the entity the cursor is pointing to
		public Array<ID> history = new Array<ID>(); // The history of targets based on cursor "select" events, somewhat redundant with turn action list
		
		@Deprecated
		public ID lastZoneID = null; // XXX Used only by cursor layout to reason on cursor speed, need better way to do this
		
		private CursorComp() {}
		
		@Override
		public void reset() {
			turnActionID = null;
			targetID = null;
			lastTargetID = null;
			history.clear();

			lastZoneID = null;
		}
	}
	
	public static final class TurnTimerComp implements C, Unique {
		public TurnTimer turnTimer = new TurnTimer();
		
		private TurnTimerComp() {}
		
		@Override
		public void reset() {
			turnTimer.reset();
		}
	}
	
	public static final class ZoneComp implements C {
		public ID zoneID = null;
		public ZoneType zoneType = ZoneType.NONE;
		Array<ID> internalObjectIDs = new Array<ID>();
		public ImmutableArray<ID> objectIDs = new ImmutableArray<ID>(internalObjectIDs);
		public ZoneLayout layout = null;
		
		private ZoneComp() {
			
		}
		
		@Override
		public void reset() {
			zoneID = null;
			zoneType = ZoneType.NONE;
			internalObjectIDs.clear();
			layout = null;
		}
		
	}
	
	public static class ZoneCompUtil extends CompUtil<ZoneComp> {
		
		public boolean hasIndex(int index) {
			return index >= 0 && index < c.internalObjectIDs.size;
		}
		
		public void add(EntityBuilder b) {
			add(b.IDComp().id, b.ZonePositionComp());
		}
		
		public void add(ID id, ZonePositionComp zp) {
			c.internalObjectIDs.add(id);
			if(zp != null) {
				zp.index = c.internalObjectIDs.size;
				zp.zoneID = c.zoneID;
			}
		}
		
		public void remove(ID id) {
			c.internalObjectIDs.removeValue(id, false);
			for(int i = 0; i < c.internalObjectIDs.size; i++) {
				ID oID = c.internalObjectIDs.get(i);
				Entity obj = Comp.Entity.get(oID);
				ZonePositionComp zp = Comp.ZonePositionComp.get(obj);
				zp.index = i;
			}
		}
		
	}
	
	public static final class ZonePositionComp implements C {
		
		public ID zoneID = null;
		public int index = -1;
		
		private ZonePositionComp() {}
		
		@Override
		public void reset() {
			zoneID = null;
			index = -1;
		}
		
	}
	
	public static final class ChangeZoneComp implements C {
		
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
	
	public static final class ChangeZoneCompUtil extends CompUtil<ChangeZoneComp> {
		
		public void change(ID oldZoneID, ID newZoneID) {
			change(oldZoneID, newZoneID, true);
		}
		
		public void change(ID oldZoneID, ID newZoneID, boolean useNextIndex) {
			change(oldZoneID, newZoneID, useNextIndex, false);	
		}
		
		public void change(ID oldZoneID, ID newZoneID, boolean useNextIndex, boolean instantChange) {
			c.oldZoneID = oldZoneID;
			c.newZoneID = newZoneID;
			c.useNextIndex = useNextIndex;
			c.instantChange = instantChange;
		}
		
	}
	
	public static final class ActionQueuedComp implements C {
		
		public long timestamp = Long.MAX_VALUE;
		
		private ActionQueuedComp() {}
		
		@Override 
		public void reset() { 
			timestamp = Long.MAX_VALUE; 
		}
		
	}
	
	// ---------------- UNSERIALIZABLE COMPONENTS ------------------------------
	
	public static final class CursorInputRegulatorComp implements C {
		public boolean processedMove = false;
		public float processedMoveDelta = 0;
		public float maxProcessedMoveDelta = 0.2f;
		
		private CursorInputRegulatorComp() {}
		
		@Override
		public void reset() {
			processedMove = false;
			processedMoveDelta = 0;
			maxProcessedMoveDelta = 0.2f;
		}
	}
	
	public static final class CursorInputComp implements C {
		public Vector2 direction = new Vector2(0, 0);
		public boolean accept = false;
		public boolean cancel = false;
		public boolean next = false;
		public boolean prev = false;
		
		private CursorInputComp() {}
		
		@Override
		public void reset() {
			direction.set(0, 0);
			accept = false;
			cancel = false;
			next = false;
			prev = false;
		}
	}
	
	public static final class InputComp implements C {
		public GameInput input; // Can't serialize

		private InputComp() {}
		
		@Override
		public void reset() {
			input = null;
		}
	}
	
	public static final class TileComp implements C {
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
	
	public static final class TextureComp implements C {
		public TextureRegion defaultRegion; // Can't serialize
		public TextureRegion region; // Can't serialize

		private TextureComp() {}
		
		@Override
		public void reset() {
			defaultRegion = null;
			region = null;
		}
	}
	
	public static final class ShapeRenderComp implements C {
		public ShapeRenderMethod renderMethod = null;
		
		private ShapeRenderComp() {}
		
		@Override
		public void reset() {
			renderMethod = null;
		}
	}
	
	public static final class AnimationComp implements C {
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
	
	public static final class BodyComp implements C {
		public Body body; // Can't serialize

		private BodyComp() {}
		
		@Override
		public void reset() {
			body = null;
		}
	}
	
	public static final class SensorComp implements C {
		public Body sensor; // Can't serialize
		
		private SensorComp() {}

		@Override
		public void reset() {
			sensor = null;
		}
	}
	
}
