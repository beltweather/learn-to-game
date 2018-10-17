package com.jharter.game.ecs.helpers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;
import com.jharter.game.ecs.components.Components.CardOwnerComp;
import com.jharter.game.ecs.components.subcomponents.TargetValidator;
import com.jharter.game.ecs.components.subcomponents.TargetValidator.DoesntHaveAllValidator;
import com.jharter.game.ecs.components.subcomponents.TargetValidator.DoesntTargetFriendCardValidator;
import com.jharter.game.ecs.components.subcomponents.TurnAction;
import com.jharter.game.ecs.entities.EntityBuildUtil;
import com.jharter.game.ecs.entities.EntityBuilder;
import com.jharter.game.ecs.entities.EntityHandler;
import com.jharter.game.ecs.entities.IEntityHandler;
import com.jharter.game.effect.AllEffect;
import com.jharter.game.effect.DamageEffect;
import com.jharter.game.effect.Effect;
import com.jharter.game.effect.HealEffect;
import com.jharter.game.effect.HealFromDamageEffect;
import com.jharter.game.effect.X2Effect;
import com.jharter.game.util.id.ID;

import uk.co.carelesslabs.Enums.EntityType;
import uk.co.carelesslabs.Enums.ZoneType;
import uk.co.carelesslabs.Media;

public class CardHelper extends EntityHandler {

	//TextureRegion swampTexture = GraphicsUtil.buildCardTexture(Media.swamp, Media.warrior, "Damage Enemy Very Badly");

	private ID ownerID;
	private CardOwnerComp co;

	public CardHelper(IEntityHandler handler) {
		super(handler);
	}

	public void setOwnerID(ID ownerID) {
		this.ownerID = ownerID;
		this.co = Comp.CardOwnerComp.getOrAdd(ownerID);
	}

	public EntityBuilder buildCard(Texture texture, String name) {
		EntityBuilder b = EntityBuildUtil.buildBasicEntity(getEngine(),
				EntityType.CARD,
				new Vector3(-450,-475,0),
				texture);
		b.DescriptionComp().name = name;
		b.SpriteComp();
		b.VelocityComp();
		b.BodyComp();
		b.CardComp().ownerID = ownerID;
		b.TurnActionComp().turnAction = new TurnAction(this);
		b.TurnActionComp().turnAction.entityID = b.IDComp().id;
		b.TurnActionComp().turnAction.ownerID = ownerID;
		b.ZonePositionComp();
		co.cardIDs.add(b.IDComp().id);
		return b;
	}

	private void targetZones(EntityBuilder b, ZoneType...zoneTypes) {
		b.TurnActionComp().turnAction.targetZoneTypes.addAll(zoneTypes);
	}

	private void effects(EntityBuilder b, Effect<?>...effects) {
		TurnAction t = b.TurnActionComp().turnAction;
		for(Effect<?> effect : effects) {
			t.addEffect(effect);
		}
	}

	public void addAttackCard() {
		EntityBuilder b = buildCard(Media.attack, "Attack");
		targetZones(b, ZoneType.ENEMY);
		effects(b, new DamageEffect(40));
		getEngine().addEntity(b.Entity());
		b.free();
	}

	public void addDrainCard() {
		EntityBuilder b = buildCard(Media.drainLife, "Drain Life");
		targetZones(b, ZoneType.ENEMY, ZoneType.FRIEND);
		effects(b, new DamageEffect(25), new HealFromDamageEffect(25));
		getEngine().addEntity(b.Entity());
		b.free();
	}

	public void addAttackAllCard() {
		EntityBuilder b = buildCard(Media.attackAll, "Attack All");
		b.TurnActionComp().turnAction.mods.defaultAll = true;
		b.TurnActionComp().turnAction.mods.all = true;
		targetZones(b, ZoneType.ENEMY);
		effects(b, new DamageEffect(20));
		getEngine().addEntity(b.Entity());
		b.free();
	}

	public void addHealAllCard() {
		EntityBuilder b = buildCard(Media.healAll, "Heal All");
		b.TurnActionComp().turnAction.mods.defaultAll = true;
		b.TurnActionComp().turnAction.mods.all = true;
		targetZones(b, ZoneType.FRIEND);
		effects(b, new HealEffect(50));
		getEngine().addEntity(b.Entity());
		b.free();
	}

	public void addEnemyAttackCard() {
		EntityBuilder b = buildCard(Media.attack, "Attack");
		targetZones(b, ZoneType.FRIEND);
		effects(b, new DamageEffect(20));
		getEngine().addEntity(b.Entity());
		b.free();
	}

	public void addX2Card() {
		EntityBuilder b = buildCard(Media.x2, "x2");
		b.TurnActionComp().turnAction.priority = 1;
		b.TurnActionComp().turnAction.makesTargetMultiplicity = 2;
		b.TurnActionComp().turnAction.targetValidator = new DoesntTargetFriendCardValidator(this);
		targetZones(b, ZoneType.FRIEND_ACTIVE_CARD);
		effects(b, new X2Effect());
		getEngine().addEntity(b.Entity());
		b.free();
	}

	public void addAllCard() {
		EntityBuilder b = buildCard(Media.all, "All");
		b.TurnActionComp().turnAction.priority = 1;
		b.TurnActionComp().turnAction.makesTargetAll = true;
		b.TurnActionComp().turnAction.targetValidator = TargetValidator.combine(this,
			new DoesntHaveAllValidator(this),
			new DoesntTargetFriendCardValidator(this));
		targetZones(b, ZoneType.FRIEND_ACTIVE_CARD);
		effects(b, new AllEffect());
		getEngine().addEntity(b.Entity());
		b.free();
	}

}
