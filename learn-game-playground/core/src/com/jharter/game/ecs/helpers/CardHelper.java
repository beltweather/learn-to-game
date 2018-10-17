package com.jharter.game.ecs.helpers;

import com.badlogic.gdx.graphics.Texture;
import com.jharter.game.card.Card;
import com.jharter.game.ecs.components.Components.CardOwnerComp;
import com.jharter.game.ecs.components.subcomponents.TargetValidator.DoesntHaveAllValidator;
import com.jharter.game.ecs.entities.EntityHandler;
import com.jharter.game.ecs.entities.IEntityHandler;
import com.jharter.game.effect.AllEffect;
import com.jharter.game.effect.DamageEffect;
import com.jharter.game.effect.HealEffect;
import com.jharter.game.effect.HealFromDamageEffect;
import com.jharter.game.effect.X2Effect;
import com.jharter.game.util.id.ID;

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

	public Card card(String name, Texture texture) {
		return Card.create(this, name, texture).owner(ownerID, co);
	}

	public void addAttackCard() {
		card("Attack", Media.attack)
			.targetZones(ZoneType.ENEMY)
			.effects(new DamageEffect(40))
			.free();
	}

	public void addDrainCard() {
		card("Drain Life", Media.drainLife)
			.targetZones(ZoneType.ENEMY, ZoneType.FRIEND)
			.effects(new DamageEffect(25), new HealFromDamageEffect(25))
			.free();
	}

	public void addAttackAllCard() {
		card("Attack All", Media.attackAll)
			.targetZones(ZoneType.ENEMY)
			.effects(new DamageEffect(20))
			.all()
			.free();
	}

	public void addHealAllCard() {
		card("Heal All", Media.healAll)
			.targetZones(ZoneType.FRIEND)
			.effects(new HealEffect(50))
			.all()
			.free();
	}

	public void addEnemyAttackCard() {
		card("Enemy's Attack", Media.attack)
			.targetZones(ZoneType.FRIEND)
			.effects(new DamageEffect(20))
			.free();
	}

	public void addX2Card() {
		card("x2", Media.x2)
			.targetsFriendCard()
			.effects(new X2Effect())
			.makesTargetMultiplicity(2)
			.free();
	}

	public void addAllCard() {
		card("All", Media.all)
			.targetsFriendCard()
			.effects(new AllEffect())
			.makesTargetAll()
			.targetValidators(new DoesntHaveAllValidator(this))
			.free();
	}

}
