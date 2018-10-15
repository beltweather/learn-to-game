package com.jharter.game.ecs.components.subcomponents;

import com.badlogic.ashley.core.Entity;
import com.jharter.game.ecs.components.Components.ZoneComp;
import com.jharter.game.ecs.components.Components.ZonePositionComp;
import com.jharter.game.ecs.entities.EntityBuilder;
import com.jharter.game.ecs.entities.EntityHandler;
import com.jharter.game.ecs.entities.IEntityHandler;
import com.jharter.game.util.id.ID;

import uk.co.carelesslabs.Enums.CardType;
import uk.co.carelesslabs.Enums.ZoneType;

public abstract class VoidCallback<T> extends EntityHandler {

	public VoidCallback(IEntityHandler handler) {
		super(handler);
	}

	public abstract void call(T object);

	public abstract static class FriendEnemyCallback extends VoidCallback<TurnAction> {

		public FriendEnemyCallback(IEntityHandler handler, EntityBuilder b) {
			super(handler);
			b.CardComp().cardType = CardType.TARGET_FRIEND_THEN_ENEMY;
			TurnAction t = b.TurnActionComp().turnAction;
			t.targetZoneTypes.add(ZoneType.FRIEND);
			t.targetZoneTypes.add(ZoneType.ENEMY);
		}

		@Override
		public void call(TurnAction t) {
			Entity card = t.getEntity();
			Entity friend = t.getTargetEntity(0);
			Entity enemy = t.getTargetEntity(1);
			Entity character = t.getOwnerEntity();
			call(character, card, friend, enemy);

			if(t.all) {
				ZonePositionComp zp = Comp.ZonePositionComp.get(friend);
				ZoneComp z = Comp.ZoneComp.get(zp.zoneID);
				ID id;
				for(int i = 0; i < z.objectIDs.size; i++) {
					id = z.objectIDs.get(i);
					Entity entity = Comp.Entity.get(id);
					if(Comp.CursorComp.has(entity)) {
						continue;
					}

					// Maybe need to check what's valid or not here
					call(character, card, entity, enemy);
				}
				t.all = t.defaultAll;

			} else {
				call(character, card, friend, enemy);
			}

		}

		public abstract void call(Entity character, Entity card, Entity friend, Entity enemy);

	}

	public abstract static class FriendCallback extends VoidCallback<TurnAction> {

		public FriendCallback(IEntityHandler handler, EntityBuilder b) {
			super(handler);
			b.CardComp().cardType = CardType.TARGET_FRIEND;
			TurnAction t = b.TurnActionComp().turnAction;
			t.targetZoneTypes.add(ZoneType.FRIEND);
		}

		@Override
		public void call(TurnAction t) {
			Entity card = t.getEntity();
			Entity friend = t.getTargetEntity(0);
			Entity character = t.getOwnerEntity();

			if(t.all) {
				ZonePositionComp zp = Comp.ZonePositionComp.get(friend);
				ZoneComp z = Comp.ZoneComp.get(zp.zoneID);
				ID id;
				for(int i = 0; i < z.objectIDs.size; i++) {
					id = z.objectIDs.get(i);
					Entity entity = Comp.Entity.get(id);
					if(Comp.CursorComp.has(entity)) {
						continue;
					}

					// Maybe need to check what's valid or not here
					call(character, card, entity);
				}
				t.all = t.defaultAll;

			} else {
				call(character, card, friend);
			}
		}

		public abstract void call(Entity character, Entity card, Entity friend);

	}

	public abstract static class CardCallback extends VoidCallback<TurnAction> {

		public CardCallback(IEntityHandler handler, EntityBuilder b) {
			super(handler);
			b.CardComp().cardType = CardType.TARGET_CARD;
			TurnAction t = b.TurnActionComp().turnAction;
			t.targetZoneTypes.add(ZoneType.FRIEND_ACTIVE_CARD);
			t.priority = 1;
			//new HasActiveCardCallback(t);
		}

		public void call(TurnAction t) {
			Entity card = t.getEntity();
			Entity activeCard = t.getTargetEntity(0);
			Entity character = t.getOwnerEntity();

			call(character, card, activeCard);
		}

		public abstract void call(Entity character, Entity card, Entity activeCard);

	}

	public abstract static class EnemyCallback extends VoidCallback<TurnAction> {

		public EnemyCallback(IEntityHandler handler, EntityBuilder b) {
			super(handler);
			b.CardComp().cardType = CardType.TARGET_ENEMY;
			TurnAction t = b.TurnActionComp().turnAction;
			t.targetZoneTypes.add(ZoneType.ENEMY);
		}

		@Override
		public void call(TurnAction t) {
			Entity card = t.getEntity();
			Entity enemy = t.getTargetEntity(0);
			Entity character = t.getOwnerEntity();

			if(t.all) {
				ZonePositionComp zp = Comp.ZonePositionComp.get(enemy);
				ZoneComp z = Comp.ZoneComp.get(zp.zoneID);
				ID id;
				for(int i = 0; i < z.objectIDs.size; i++) {
					id = z.objectIDs.get(i);
					Entity entity = Comp.Entity.get(id);
					if(Comp.CursorComp.has(entity)) {
						continue;
					}

					// Maybe need to check what's valid or not here
					call(character, card, entity);
				}
				t.all = t.defaultAll;

			} else {
				call(character, card, enemy);
			}
		}

		public abstract void call(Entity character, Entity card, Entity enemy);

	}

}
