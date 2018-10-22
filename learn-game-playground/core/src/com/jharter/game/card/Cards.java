package com.jharter.game.card;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.jharter.game.ecs.components.Components.CardOwnerComp;
import com.jharter.game.ecs.components.subcomponents.TargetValidator.DoesntHaveAllValidator;
import com.jharter.game.ecs.entities.EntityHandler;
import com.jharter.game.ecs.entities.IEntityHandler;
import com.jharter.game.effect.AllEffect;
import com.jharter.game.effect.DamageEffect;
import com.jharter.game.effect.HealEffect;
import com.jharter.game.effect.HealFromDamageEffect;
import com.jharter.game.effect.StatusEffect;
import com.jharter.game.effect.X2Effect;
import com.jharter.game.util.id.ID;

import uk.co.carelesslabs.Enums.ZoneType;
import uk.co.carelesslabs.Media;

public class Cards extends EntityHandler {

	public Card Attack = new Card("Attack") {

		@Override
		protected CardBuilder build() {
			return texture(Media.attack)
				.targetZones(ZoneType.ENEMY)
				.effects(new DamageEffect(40));
		}

	};

	public Card FireAttack = new Card("Fire Attack") {

		@Override
		protected CardBuilder build() {
			return texture(Media.attack)
				.targetZones(ZoneType.ENEMY)
				.effects(new DamageEffect(40),
						 new StatusEffect().fire());
		}

	};

	public Card Drain = new Card("Drain") {

		@Override
		protected CardBuilder build() {
			return texture(Media.drainLife)
				.targetZones(ZoneType.ENEMY, ZoneType.FRIEND)
				.effects(new DamageEffect(25).index(0),
						 new StatusEffect().poison().index(0),
						 new HealFromDamageEffect(25).index(1));
		}

	};

	public Card AttackAll = new Card("Attack All") {

		@Override
		protected CardBuilder build() {
			return texture(Media.attackAll)
				.targetZones(ZoneType.ENEMY)
				.effects(new DamageEffect(20), new StatusEffect().lightning())
				.all();
		}

	};

	public Card HealAll = new Card("Heal All") {

		@Override
		protected CardBuilder build() {
			return texture(Media.healAll)
				.targetZones(ZoneType.FRIEND)
				.effects(new HealEffect(50))
				.all();
		}

	};

	public Card EnemyAttack = new Card("Enemy's Attack") {

		@Override
		protected CardBuilder build() {
			return texture(Media.attack)
				.targetZones(ZoneType.FRIEND)
				.effects(new DamageEffect(20), new StatusEffect().ice());
		}

	};

	public Card X2 = new Card("x2") {

		@Override
		protected CardBuilder build() {
			return texture(Media.x2)
				.targetsFriendCard()
				.effects(new X2Effect())
				.makesTargetMultiplicity(2);
		}

	};

	public Card All = new Card("All") {

		@Override
		protected CardBuilder build() {
			return texture(Media.all)
				.targetsFriendCard()
				.effects(new AllEffect())
				.makesTargetAll()
				.targetValidators(new DoesntHaveAllValidator(Cards.this));
		}

	};



	// To insert template type: ca -> ctrl+space -> enter

	//private static final TextureRegion swampTexture = GraphicsUtil.buildCardTexture(Media.swamp, Media.warrior, "Damage Enemy Very Badly");

	private ID ownerID;
	private CardOwnerComp co;

	public Cards(IEntityHandler handler) {
		super(handler);
	}

	public void setOwnerID(ID ownerID) {
		this.ownerID = ownerID;
		this.co = Comp.CardOwnerComp.getOrAdd(ownerID);
	}

	public abstract class Card {

		private String name;

		private Card(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public Cards add() {
			build().done();
			return Cards.this;
		}

		protected abstract CardBuilder build();

		protected CardBuilder texture(Texture texture) {
			return texture(new TextureRegion(texture));
		}

		protected CardBuilder texture(TextureRegion texture) {
			return CardBuilder.create(Cards.this, name, texture).owner(ownerID, co);
		}
	}
}
