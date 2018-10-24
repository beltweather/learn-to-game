package com.jharter.game.card;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.Pools;
import com.jharter.game.ecs.components.Components.CardOwnerComp;
import com.jharter.game.ecs.components.subcomponents.TargetValidator;
import com.jharter.game.ecs.components.subcomponents.TargetValidator.DoesntTargetFriendCardValidator;
import com.jharter.game.ecs.components.subcomponents.TurnAction;
import com.jharter.game.ecs.entities.EntityBuildUtil;
import com.jharter.game.ecs.entities.EntityBuilder;
import com.jharter.game.effect.Effect;
import com.jharter.game.util.id.ID;

import uk.co.carelesslabs.Enums.EntityType;
import uk.co.carelesslabs.Enums.ZoneType;

/**
 * This is actually a "builder" type class, instances of this are never meant to be held onto.
 *
 * @author Jon
 *
 */
class CardBuilder implements Poolable {

	public static CardBuilder create(Cards handler, String name, Texture texture) {
		return create(handler, name, new TextureRegion(texture));
	}

	public static CardBuilder create(Cards handler, String name, TextureRegion texture) {
		CardBuilder card = Pools.get(CardBuilder.class).obtain();
		card.init(handler, name, texture);
		return card;
	}

	private EntityBuilder b;
	private Cards handler;

	private CardBuilder() {}

	private void init(Cards handler, String name, TextureRegion texture) {
		this.handler = handler;
		b = EntityBuildUtil.buildBasicEntity(handler.getEngine(), EntityType.CARD, new Vector3(), texture);
		name(name);
		b.SpriteComp();
		b.VelocityComp();
		b.TurnActionComp().turnAction = new TurnAction(handler);
		b.TurnActionComp().turnAction.entityID = b.IDComp().id;
		b.ZonePositionComp();
		handler.getEngine().addEntity(b.Entity());
	}

	public CardBuilder name(String name) {
		b.DescriptionComp().name = name;
		return this;
	}

	public CardBuilder texture(Texture texture) {
		b.TextureComp().defaultRegion = new TextureRegion(texture);
		return this;
	}

	public CardBuilder texture(TextureRegion texture) {
		b.TextureComp().defaultRegion = texture;
		return this;
	}

	public CardBuilder owner(ID ownerID) {
		return owner(ownerID, handler.getCompManager().CardOwnerComp.getOrAdd(ownerID));
	}

	public CardBuilder owner(ID ownerID, CardOwnerComp co) {
		b.CardComp().ownerID = ownerID;
		b.TurnActionComp().turnAction.ownerID = ownerID;
		co.cardIDs.add(b.IDComp().id);
		return this;
	}

	public CardBuilder targetsFriendCard() {
		return targetZones(ZoneType.FRIEND_ACTIVE_CARD)
			   .priority(1)
			   .targetValidators(new DoesntTargetFriendCardValidator(handler));
	}

	public CardBuilder targetZones(ZoneType...zoneTypes) {
		b.TurnActionComp().turnAction.targetZoneTypes.addAll(zoneTypes);
		return this;
	}

	public CardBuilder effects(Effect...effects) {
		TurnAction t = b.TurnActionComp().turnAction;
		for(Effect effect : effects) {
			t.addEffect(effect);
		}
		return this;
	}

	public CardBuilder makesTargetAll() {
		b.TurnActionComp().turnAction.makesTargetAll = true;
		return this;
	}

	public CardBuilder makesTargetMultiplicity(int m) {
		b.TurnActionComp().turnAction.makesTargetMultiplicity = m;
		return this;
	}

	public CardBuilder all() {
		b.TurnActionComp().turnAction.mods.all.d(true);
		return this;
	}

	public CardBuilder multiplicity(int m) {
		b.TurnActionComp().turnAction.mods.multiplicity.d(m);
		return this;
	}

	public CardBuilder priority(int priority) {
		b.TurnActionComp().turnAction.priority = priority;
		return this;
	}

	public CardBuilder targetValidators(TargetValidator...validators) {
		TargetValidator validator = TargetValidator.combine(handler, validators);
		if(b.TurnActionComp().turnAction.targetValidator != null) {
			validator = TargetValidator.combine(b.TurnActionComp().turnAction.targetValidator, validator);
		}
		b.TurnActionComp().turnAction.targetValidator = validator;
		return this;
	}

	public Cards done() {
		free();
		return handler;
	}

	public void free() {
		Pools.get(CardBuilder.class).free(this);
	}

	@Override
	public void reset() {
		if(b != null) {
			b.free();
			b = null;
		}
		handler = null;
	}

}
