package com.jharter.game.ecs.components;

import java.util.EnumSet;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.jharter.game.control.GameInput;
import com.jharter.game.ecs.components.subcomponents.Interaction;
import com.jharter.game.ecs.components.subcomponents.RelativePositionRules;
import com.jharter.game.ecs.components.subcomponents.TurnAction;
import com.jharter.game.ecs.components.subcomponents.TurnTimer;
import com.jharter.game.ecs.entities.EntityBuilder;
import com.jharter.game.layout.ZoneLayout;
import com.jharter.game.render.ShapeRenderMethod;
import com.jharter.game.util.id.ID;

import uk.co.carelesslabs.Enums.CardOwnerAction;
import uk.co.carelesslabs.Enums.CardType;
import uk.co.carelesslabs.Enums.EntityType;
import uk.co.carelesslabs.Enums.TileType;
import uk.co.carelesslabs.Enums.ZoneType;

/**
 * All components used in the game. Components should not inherit properties
 * from superclasses, but can implement interfaces that 
 * 
 * @author Jon
 *
 */
public final class Components {

	private Components() {}
	
	// ------------------- SUPERCLASS AND INTERFACe COMPONENTS --------
	
	/**
	 * Convenience class to ensure that our components are all poolable.
	 */
	private static interface C extends Component, Poolable {}
	
	/**
	 * Convenience class for simple boolean component, no data inside.
	 */
	private static class B implements C, Tag {
		private B() {}
		@Override public void reset() {}
	}
	
	/**
	 * Special interface that denotes a component is to be used as a 
	 * tag. Tags are stateless components meaning they should never
	 * have any properties. They are completely boolean.
	 */
	public static interface Tag {}
	
	/**
	 * Special interface that denotes a component is supposed to only
	 * exist on one entity total in the entire engine. It should be
	 * safe to use "FirstSystems" with families containing these
	 * components since exactly one entity should exist with it.
	 */
	public static interface Unique {}
	
	/**
	 * Special interface to denote which comps will be treated as events.
	 * This is mainly here for book-keeping, it has no functional effect.
	 */
	public static interface Event {}
	
	// ------------------- BOOL COMPONENTS -----------------------------

	public static final class FocusTag extends B implements Unique, Tag {}
	public static final class TurnPhaseTag extends B implements Unique, Tag {}
	public static final class TurnPhaseStartBattleTag extends B implements Unique, Tag {}
	public static final class TurnPhaseStartTurnTag extends B implements Unique, Tag {}
	public static final class TurnPhaseSelectActionsTag extends B implements Unique, Tag {}
	public static final class TurnPhasePerformActionsTag extends B implements Unique, Tag {}
	public static final class TurnPhaseEndTurnTag extends B implements Unique, Tag {}
	public static final class TurnPhaseEndBattleTag extends B implements Unique, Tag {}
	public static final class TurnPhaseNoneTag extends B implements Unique, Tag {}

	public static final class FriendTag extends B implements Tag {}
	public static final class EnemyTag extends B implements Tag {}
	public static final class PendingTurnActionTag extends B implements Tag {}
	public static final class TargetableTag extends B implements Tag {}
	public static final class UntargetableTag extends B implements Tag {}
	public static final class InvisibleTag extends B implements Tag {}
	public static final class DisabledTag extends B implements Tag {}
	public static final class ActionReadyTag extends B implements Tag {}
	public static final class CleanupTurnActionTag extends B implements Tag {}
	public static final class DiscardCardTag extends B implements Tag {}
	public static final class PlayerTag extends B implements Tag {}
	
	public static final class CursorTargetEvent extends B implements Event {}
	public static final class CursorUntargetEvent implements C, Event {
		
		public ID cursorID;
		
		private CursorUntargetEvent() {}
		
		@Override
		public void reset() {
			cursorID = null;
		}
		
	}
	public static final class CursorChangedZoneEvent extends B implements Event {}
	
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
		public Array<ID> ids = new Array<ID>(); // Used to identify what's at a certain index in this list so it can be cleared by id
		
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
			ids.clear();
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
	
	public static final class CardOwnerComp implements C {
		
		public int handSize = 3;
		public Array<ID> cardIDs = new Array<ID>();
		public EnumSet<CardOwnerAction> actions = EnumSet.noneOf(CardOwnerAction.class);
		public int draw = 0;
		
		private CardOwnerComp() {}
		
		@Override
		public void reset() {
			handSize = 3;
			cardIDs.clear();
			actions.clear();
			draw = 0;
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
		public int incomingDamage = 0;
		public int incomingHealing = 0;

		private VitalsComp() {}
		
		@Override
		public void reset() {
			maxHealth = 0;
			weakHealth = 0;
			health = 0;
			incomingDamage = 0;
			incomingHealing = 0;
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
	
	public static final class CursorTargetComp implements C {
		public ID cursorID;
		public ID mainTargetID = null; // The id of the actual target of the cursor if this target is part of a group of targets due to "all"
		public boolean isSub = false;
		public boolean isAll = false;
		public int multiplicity = 1;
		
		private CursorTargetComp() {}
		
		@Override
		public void reset() {
			cursorID = null;
			mainTargetID = null;
			isSub = false;
			isAll = false;
			multiplicity = 1;
		}
		
	}
	
	public static final class CursorComp implements C, Unique {
		public ID turnActionID = null;
		public ID targetID = null; // The id of the entity the cursor is pointing to
		public Array<ID> history = new Array<ID>(); // The history of targets based on cursor "select" events, somewhat redundant with turn action list
		
		private CursorComp() {}
		
		@Override
		public void reset() {
			turnActionID = null;
			targetID = null;
			history.clear();
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
		public Array<ID> objectIDs = new Array<ID>();
		public ZoneLayout layout = null;
		
		private ZoneComp() {
			
		}
		
		@Override
		public void reset() {
			zoneID = null;
			zoneType = ZoneType.NONE;
			objectIDs.clear();
			layout = null;
		}
		
	}
	
	public static class ZoneCompUtil extends CompUtil<ZoneComp> {
		
		public boolean hasIndex(int index) {
			return index >= 0 && index < c.objectIDs.size;
		}
		
		public void add(EntityBuilder b) {
			add(b.IDComp().id, b.ZonePositionComp());
		}
		
		public void add(ID id) {
			add(id, Comp.ZonePositionComp.get(id));
		}
		
		public void add(ID id, ZonePositionComp zp) {
			c.objectIDs.add(id);
			if(zp != null) {
				zp.index = c.objectIDs.size;
				zp.zoneID = c.zoneID;
			}
		}
		
		public void remove(ID id) {
			c.objectIDs.removeValue(id, false);
			for(int i = 0; i < c.objectIDs.size; i++) {
				ID oID = c.objectIDs.get(i);
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
	
	public static final class ActionQueueableComp implements C {
		
		public long timestamp = Long.MAX_VALUE;
		
		private ActionQueueableComp() {}
		
		@Override 
		public void reset() { 
			timestamp = Long.MAX_VALUE; 
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
	
	public static final class AutoSelectTurnActionComp implements C {
		
		public float minWaitTime = 0;
		public float maxWaitTime = 20;
		public float waitTime = 0;
		public float waited = 0;
		public boolean waiting = false;
		
		private AutoSelectTurnActionComp() {}
		
		@Override
		public void reset() {
			minWaitTime = 0;
			maxWaitTime = 20;
			waitTime = 0;
			waited = 0;
			waiting = false;
		}
	}
	
	public static final class NextTurnPhaseComp implements C {
		
		public Class<? extends Component> next = null;
		
		private NextTurnPhaseComp() {}
		
		@Override
		public void reset() {
			next = null;
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
