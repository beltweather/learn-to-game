package com.jharter.game.ashley.components.subcomponents;

import com.badlogic.ashley.core.Entity;
import com.jharter.game.ashley.components.Comp;
import com.jharter.game.ashley.components.Components.CardComp;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.Components.ZonePositionComp;
import com.jharter.game.ashley.components.EntityBuilder;
import com.jharter.game.util.id.ID;

import uk.co.carelesslabs.Enums.CardType;
import uk.co.carelesslabs.Enums.ZoneType;

public abstract class VoidCallback<T> {

	public VoidCallback() {}
	
	public abstract void call(T object);
	
	public abstract static class FriendEnemyCallback extends VoidCallback<TurnAction> {
		
		public FriendEnemyCallback(EntityBuilder b) {
			b.CardComp().cardType = CardType.TARGET_FRIEND_THEN_ENEMY;
			TurnAction t = b.TurnActionComp().turnAction;
			t.targetZoneTypes.add(ZoneType.FRIEND);
			t.targetZoneTypes.add(ZoneType.ENEMY);
			t.acceptCallback = this;
		}
		
		@Override
		public void call(TurnAction t) {
			Entity card = t.getPerformer();
			Entity friend = t.getEntity(0);
			Entity enemy = t.getEntity(1);
			CardComp c = Comp.CardComp.get(card);
			Entity character = Comp.Method.CardComp.getBattleAvatarEntity(c);
			call(character, card, friend, enemy);
			
			if(t.all) {
				ZonePositionComp zp = Comp.ZonePositionComp.get(friend);
				ZoneComp z = Comp.Method.ZoneComp.get(zp);
				ID id;
				for(int i = 0; i < z.objectIDs.size(); i++) {
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
		
		public FriendCallback(EntityBuilder b) {
			b.CardComp().cardType = CardType.TARGET_FRIEND;
			TurnAction t = b.TurnActionComp().turnAction;
			t.targetZoneTypes.add(ZoneType.FRIEND);
			t.acceptCallback = this;
		}
		
		@Override
		public void call(TurnAction t) {
			Entity card = t.getPerformer();
			CardComp c = Comp.CardComp.get(card);
			Entity friend = t.getEntity(0);
			Entity character = Comp.Method.CardComp.getBattleAvatarEntity(c);
			
			if(t.all) {
				ZonePositionComp zp = Comp.ZonePositionComp.get(friend);
				ZoneComp z = Comp.Method.ZoneComp.get(zp);
				ID id;
				for(int i = 0; i < z.objectIDs.size(); i++) {
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
		
		public CardCallback(EntityBuilder b) {
			b.CardComp().cardType = CardType.TARGET_CARD;
			TurnAction t = b.TurnActionComp().turnAction;
			t.targetZoneTypes.add(ZoneType.ACTIVE_CARD);
			t.acceptCallback = this;
			t.priority = 1;
			//new HasActiveCardCallback(t);
		}
		
		public void call(TurnAction t) {
			Entity card = t.getPerformer();
			CardComp c = Comp.CardComp.get(card);
			Entity activeCard = t.getEntity(0); 
			Entity character = Comp.Method.CardComp.getBattleAvatarEntity(c);
			
			call(character, card, activeCard);
		}
		
		public abstract void call(Entity character, Entity card, Entity activeCard);
		
	}

	public abstract static class EnemyCallback extends VoidCallback<TurnAction> {
		
		public EnemyCallback(EntityBuilder b) {
			b.CardComp().cardType = CardType.TARGET_ENEMY;
			TurnAction t = b.TurnActionComp().turnAction;
			t.targetZoneTypes.add(ZoneType.ENEMY);
			t.acceptCallback = this;
		}
		
		@Override
		public void call(TurnAction t) {
			Entity card = t.getPerformer();
			CardComp c = Comp.CardComp.get(card);
			Entity enemy = t.getEntity(0);
			Entity character = Comp.Method.CardComp.getBattleAvatarEntity(c);
			
			if(t.all) {
				ZonePositionComp zp = Comp.ZonePositionComp.get(enemy);
				ZoneComp z = Comp.Method.ZoneComp.get(zp);
				ID id;
				for(int i = 0; i < z.objectIDs.size(); i++) {
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
