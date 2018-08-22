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
				for(ID id : z.objects) {
					// Maybe need to check what's valid or not here
					call(owner, card, Mapper.Entity.get(id));
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
			call(owner, card, friend, Mapper.Entity.get(Mapper.ActiveCardComp.get(friend).activeCardID));
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
				for(ID id : z.objects) {
					// Maybe need to check what's valid or not here
					call(owner, card, Mapper.Entity.get(id));
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
			call(owner, card, enemy, Mapper.Entity.get(Mapper.ActiveCardComp.get(enemy).activeCardID));
		}
		
		public abstract void call(Entity owner, Entity card, Entity enemy, Entity enemyCard);
		
	}
	
}
