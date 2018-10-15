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

import uk.co.carelesslabs.Enums.CardType;
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

	private void targetZones(EntityBuilder b, CardType cardType, ZoneType...zoneTypes) {
		b.CardComp().cardType = cardType;
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
		targetZones(b, CardType.TARGET_ENEMY, ZoneType.ENEMY);
		effects(b, new DamageEffect(40));
		getEngine().addEntity(b.Entity());
		b.free();
	}

	public void addDrainCard() {
		EntityBuilder b = buildCard(Media.drainLife, "Drain Life");
		targetZones(b, CardType.TARGET_FRIEND_THEN_ENEMY, ZoneType.FRIEND, ZoneType.ENEMY);
		effects(b, new DamageEffect(25).setTargetIndex(1),
				   new HealFromDamageEffect(25).setTargetIndex(0));
		getEngine().addEntity(b.Entity());
		b.free();
	}

	public void addAttackAllCard() {
		EntityBuilder b = buildCard(Media.attackAll, "Attack All");
		b.TurnActionComp().turnAction.defaultAll = true;
		b.TurnActionComp().turnAction.all = true;
		targetZones(b, CardType.TARGET_ENEMY, ZoneType.ENEMY);
		effects(b, new DamageEffect(20));
		getEngine().addEntity(b.Entity());
		b.free();
	}

	public void addHealAllCard() {
		EntityBuilder b = buildCard(Media.healAll, "Heal All");
		b.TurnActionComp().turnAction.defaultAll = true;
		b.TurnActionComp().turnAction.all = true;
		targetZones(b, CardType.TARGET_FRIEND, ZoneType.FRIEND);
		effects(b, new HealEffect(50));
		getEngine().addEntity(b.Entity());
		b.free();
	}

	public void addEnemyAttackCard() {
		EntityBuilder b = buildCard(Media.attack, "Attack");
		targetZones(b, CardType.TARGET_FRIEND, ZoneType.FRIEND);
		effects(b, new DamageEffect(20));
		getEngine().addEntity(b.Entity());
		b.free();
	}

	public void addX2Card() {
		EntityBuilder b = buildCard(Media.x2, "x2");
		b.TurnActionComp().turnAction.makesTargetMultiplicity = 2;
		b.TurnActionComp().turnAction.targetValidator = new DoesntTargetFriendCardValidator(this);
		targetZones(b, CardType.TARGET_CARD, ZoneType.FRIEND_ACTIVE_CARD);
		effects(b, new X2Effect());
		getEngine().addEntity(b.Entity());
		b.free();
	}

	public void addAllCard() {
		EntityBuilder b = buildCard(Media.all, "All");
		b.TurnActionComp().turnAction.makesTargetAll = true;
		b.TurnActionComp().turnAction.targetValidator = TargetValidator.combine(this,
			new DoesntHaveAllValidator(this),
			new DoesntTargetFriendCardValidator(this));
		targetZones(b, CardType.TARGET_CARD, ZoneType.FRIEND_ACTIVE_CARD);
		effects(b, new AllEffect());
		getEngine().addEntity(b.Entity());
		b.free();
	}

	/*public void addOrigDrainCard() {
	EntityBuilder b = buildCard(Media.drainLife, "Drain Life");
	new FriendEnemyCallback(this, b) {

		@Override
		public void call(Entity owner, Entity card, Entity friend, Entity enemy) {
			Media.weaponSwing.play();

			int damage = CombatHelper.getDamage(owner, enemy, 13);

			VitalsComp vEnemy = Comp.VitalsComp.get(enemy);
			VitalsComp vFriend = Comp.VitalsComp.get(friend);
			int origHealthFriend = vFriend.vitals.health;
			int origHealthEnemy = vEnemy.vitals.health;

			Comp.util(vEnemy).damage(damage);
			Comp.util(vFriend).heal(damage);

			// DEBUG PRINTING
			int healed = vFriend.vitals.health - origHealthFriend;
			int damaged = origHealthEnemy - vEnemy.vitals.health;

			String nameFriend = Comp.DescriptionComp.get(friend).name;
			String nameEnemy = Comp.DescriptionComp.get(enemy).name;

			Sys.out.println("Deal damage and heal friend for amount:");
			Sys.out.println(nameEnemy + " received " + damaged + " damage.");
			Sys.out.println(nameFriend + " received " + healed + " health.");

			Sys.out.println(nameEnemy + " hp: " + vEnemy.vitals.health);
			Sys.out.println(nameFriend + " hp: " + vFriend.vitals.health);

			if(Comp.util(vEnemy).isNearDeath()) {
				Sys.out.println(nameEnemy + " is near death.");
			} else if(Comp.util(vEnemy).isDead()) {
				Sys.out.println(nameEnemy + " is dead.");
			}
		}

	};
	getEngine().addEntity(b.Entity());
	b.free();
}

public void addOrigAttackCard() {
	EntityBuilder b = buildCard(Media.attack, "Attack");
	new EnemyCallback(this, b) {

		@Override
		public void call(Entity owner, Entity card, Entity enemy) {
			Media.weaponSwing.play();

			int damage = CombatHelper.getDamage(owner, enemy, 20);
			Comp.util(enemy, VitalsComp.class).damage(damage);
			Sys.out.println("Dealt " + damage + " damage.");
		}

	};
	getEngine().addEntity(b.Entity());
	b.free();
}

public void addOrigAttackAllCard() {
	EntityBuilder b = buildCard(Media.attackAll, "Attack All");
	b.TurnActionComp().turnAction.defaultAll = true;
	b.TurnActionComp().turnAction.all = true;
	new EnemyCallback(this, b) {

		@Override
		public void call(Entity owner, Entity card, Entity enemy) {
			Media.weaponSwing.play();

			int damage = CombatHelper.getDamage(owner, enemy, 20);
			Comp.util(enemy, VitalsComp.class).damage(damage);
			Sys.out.println("Dealt " + damage + " damage.");
		}

	};
	getEngine().addEntity(b.Entity());
	b.free();
}

public void addOrigHealAllCard() {
	EntityBuilder b = buildCard(Media.healAll, "Heal All");
	b.TurnActionComp().turnAction.defaultAll = true;
	b.TurnActionComp().turnAction.all = true;
	new FriendCallback(this, b) {

		@Override
		public void call(Entity owner, Entity card, Entity friend) {
			int hp = 50;
			Comp.util(friend, VitalsComp.class).heal(hp);
			Sys.out.println("Healed " + hp + " hp to " + Comp.DescriptionComp.get(friend).name);
		}

	};
	getEngine().addEntity(b.Entity());
	b.free();
}*/

}
