package com.jharter.game.ashley.components.subcomponents;

import com.badlogic.ashley.core.Entity;
import com.jharter.game.ashley.components.Components.ActiveCardComp;
import com.jharter.game.ashley.components.Components.CardComp;
import com.jharter.game.ashley.components.Components.TargetingComp;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.Components.ZonePositionComp;
import com.jharter.game.ashley.components.EntityBuilder;
import com.jharter.game.ashley.components.Mapper;
import com.jharter.game.ashley.components.subcomponents.Callback.ActiveCardCallback;
import com.jharter.game.util.id.ID;

import uk.co.carelesslabs.Enums.ZoneType;

public abstract class VoidCallback<T> {

	public VoidCallback() {}
	
	public abstract void call(T object);
	
	public abstract static class FriendEnemyCallback extends VoidCallback<TargetingComp> {
		
		public FriendEnemyCallback(EntityBuilder b) {
			this(b.TargetingComp());
		}
		
		public FriendEnemyCallback(TargetingComp t) {
			t.targetZoneTypes.add(ZoneType.FRIEND);
			t.targetZoneTypes.add(ZoneType.ENEMY);
			t.acceptCallback = this;
		}
		
		@Override
		public void call(TargetingComp t) {
			Entity card = t.getEntity(0);
			Entity friend = t.getEntity(1);
			Entity enemy = t.getEntity(2);
			CardComp cCard = Mapper.CardComp.get(card);
			Entity owner = Mapper.Entity.get(cCard.ownerID);
			call(owner, card, friend, enemy);
			
			// XXX Add ALL variant
		}
		
		public abstract void call(Entity owner, Entity card, Entity friend, Entity enemy);
		
	}
	
	public abstract static class FriendCallback extends VoidCallback<TargetingComp> {
		
		public FriendCallback(EntityBuilder b) {
			this(b.TargetingComp());
		}
		
		public FriendCallback(TargetingComp t) {
			t.targetZoneTypes.add(ZoneType.FRIEND);
			t.acceptCallback = this;
		}
		
		@Override
		public void call(TargetingComp t) {
			Entity card = t.getEntity(0);
			CardComp cCard = Mapper.CardComp.get(card);
			Entity owner = Mapper.Entity.get(cCard.ownerID);
			Entity friend = t.getEntity(1);
			
			if(t.all) {
				ZonePositionComp zp = Mapper.ZonePositionComp.get(friend);
				ZoneComp z = Mapper.ZoneComp.get(zp);
				ID id;
				for(int i = 0; i < z.size(); i++) {
					id = z.get(i);
					Entity entity = Mapper.Entity.get(id);
					if(Mapper.CursorComp.has(entity)) {
						continue;
					}
					
					// Maybe need to check what's valid or not here
					call(owner, card, entity);
				}
				t.all = t.defaultAll;
				
			} else {
				call(owner, card, friend);	
			}
		}
		
		public abstract void call(Entity owner, Entity card, Entity friend);
		
	}
	
	public abstract static class FriendWithCardCallback extends FriendCallback {
		
		public FriendWithCardCallback(EntityBuilder b) {
			this(b.TargetingComp());
		}
		
		public FriendWithCardCallback(TargetingComp t) {
			super(t);
			new ActiveCardCallback(t);
		}
		
		public void call(Entity owner, Entity card, Entity friend) {
			ActiveCardComp ac = Mapper.ActiveCardComp.get(friend);
			Entity friendCard = (ac == null || ac.activeCardID == null) ? null : Mapper.Entity.get(ac.activeCardID);
			call(owner, card, friend, friendCard);
		}
		
		public abstract void call(Entity owner, Entity card, Entity friend, Entity friendCard);
		
	}

	public abstract static class EnemyCallback extends VoidCallback<TargetingComp> {
		
		public EnemyCallback(EntityBuilder b) {
			this(b.TargetingComp());
		}
		
		public EnemyCallback(TargetingComp t) {
			t.targetZoneTypes.add(ZoneType.ENEMY);
			t.acceptCallback = this;
		}
		
		@Override
		public void call(TargetingComp t) {
			Entity card = t.getEntity(0);
			CardComp cCard = Mapper.CardComp.get(card);
			Entity owner = Mapper.Entity.get(cCard.ownerID);
			Entity enemy = t.getEntity(1);
			
			if(t.all) {
				ZonePositionComp zp = Mapper.ZonePositionComp.get(enemy);
				ZoneComp z = Mapper.ZoneComp.get(zp);
				ID id;
				for(int i = 0; i < z.size(); i++) {
					id = z.get(i);
					Entity entity = Mapper.Entity.get(id);
					if(Mapper.CursorComp.has(entity)) {
						continue;
					}
					
					// Maybe need to check what's valid or not here
					call(owner, card, entity);
				}
				t.all = t.defaultAll;
				
			} else {
				call(owner, card, enemy);	
			}
		}
		
		public abstract void call(Entity owner, Entity card, Entity enemy);
		
	}
	
	public abstract static class EnemyWithCardCallback extends EnemyCallback {
		
		public EnemyWithCardCallback(EntityBuilder b) {
			this(b.TargetingComp());
		}
		
		public EnemyWithCardCallback(TargetingComp t) {
			super(t);
			new ActiveCardCallback(t);
		}
		
		public void call(Entity owner, Entity card, Entity enemy) {
			ActiveCardComp ac = Mapper.ActiveCardComp.get(enemy);
			Entity enemyCard = (ac == null || ac.activeCardID == null) ? null : Mapper.Entity.get(ac.activeCardID);
			call(owner, card, enemy, enemyCard);
		}
		
		public abstract void call(Entity owner, Entity card, Entity enemy, Entity enemyCard);
		
	}
	
}
