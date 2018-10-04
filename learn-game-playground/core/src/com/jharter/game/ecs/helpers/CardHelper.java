package com.jharter.game.ecs.helpers;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;
import com.jharter.game.ecs.components.Components.CardOwnerComp;
import com.jharter.game.ecs.components.Components.DescriptionComp;
import com.jharter.game.ecs.components.Components.VitalsComp;
import com.jharter.game.ecs.components.subcomponents.Callback.DoesntHaveAllCallback;
import com.jharter.game.ecs.components.subcomponents.TurnAction;
import com.jharter.game.ecs.components.subcomponents.VoidCallback.CardCallback;
import com.jharter.game.ecs.components.subcomponents.VoidCallback.EnemyCallback;
import com.jharter.game.ecs.components.subcomponents.VoidCallback.FriendCallback;
import com.jharter.game.ecs.components.subcomponents.VoidCallback.FriendEnemyCallback;
import com.jharter.game.ecs.entities.EntityBuildUtil;
import com.jharter.game.ecs.entities.EntityBuilder;
import com.jharter.game.ecs.entities.EntityHandler;
import com.jharter.game.ecs.entities.IEntityHandler;
import com.jharter.game.util.Sys;
import com.jharter.game.util.id.ID;

import uk.co.carelesslabs.Enums.EntityType;
import uk.co.carelesslabs.Media;

public class CardHelper extends EntityHandler {
	
	//TextureRegion swampTexture = GraphicsUtil.buildCardTexture(Media.swamp, Media.warrior, "Damage Enemy Very Badly");

	private CombatHelper CombatUtil;
	private ID ownerID;
	private CardOwnerComp co;
	
	public CardHelper(IEntityHandler handler) {
		super(handler);
		this.CombatUtil = new CombatHelper(handler);
	}
	
	public void setOwnerID(ID ownerID) {
		this.ownerID = ownerID;
		this.co = Comp.getOrAdd(CardOwnerComp.class, ownerID);
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
	
	public void addDrainCard() {
		EntityBuilder b = buildCard(Media.drainLife, "Drain Life");
		new FriendEnemyCallback(this, b) {

			@Override
			public void call(Entity owner, Entity card, Entity friend, Entity enemy) {
				Media.weaponSwing.play();
				
				int damage = CombatUtil.getDamage(owner, enemy, 13);
				
				VitalsComp vEnemy = Comp.VitalsComp.get(enemy);
				VitalsComp vFriend = Comp.VitalsComp.get(friend);
				int origHealthFriend = vFriend.health;
				int origHealthEnemy = vEnemy.health;
				
				Comp.util(vEnemy).damage(damage);
				Comp.util(vFriend).heal(damage);
				
				// DEBUG PRINTING
				int healed = vFriend.health - origHealthFriend;
				int damaged = origHealthEnemy - vEnemy.health; 
				
				String nameFriend = Comp.DescriptionComp.get(friend).name;
				String nameEnemy = Comp.DescriptionComp.get(enemy).name;
				
				Sys.out.println("Deal damage and heal friend for amount:");
				Sys.out.println(nameEnemy + " received " + damaged + " damage.");
				Sys.out.println(nameFriend + " received " + healed + " health.");
				
				Sys.out.println(nameEnemy + " hp: " + vEnemy.health);
				Sys.out.println(nameFriend + " hp: " + vFriend.health);
				
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
	
	public void addAttackCard() {
		EntityBuilder b = buildCard(Media.attack, "Attack");
		new EnemyCallback(this, b) {

			@Override
			public void call(Entity owner, Entity card, Entity enemy) {
				Media.weaponSwing.play();
				
				int damage = CombatUtil.getDamage(owner, enemy, 20);
				Comp.util(enemy, VitalsComp.class).damage(damage);
				Sys.out.println("Dealt " + damage + " damage.");
			}
			
		};
		getEngine().addEntity(b.Entity());
		b.free();
	}
	
	public void addEnemyAttackCard() {
		EntityBuilder b = buildCard(Media.attack, "Attack");
		new FriendCallback(this, b) {

			@Override
			public void call(Entity owner, Entity card, Entity friend) {
				Media.weaponSwing.play();
				
				int damage = CombatUtil.getDamage(owner, friend, 20);
				Comp.util(friend, VitalsComp.class).damage(damage);
				Sys.out.println("Dealt " + damage + " damage.");
			}
			
		};
		getEngine().addEntity(b.Entity());
		b.free();
	}
	
	public void addAttackAllCard() {
		EntityBuilder b = buildCard(Media.attackAll, "Attack All");
		b.TurnActionComp().turnAction.defaultAll = true;
		b.TurnActionComp().turnAction.all = true;
		new EnemyCallback(this, b) {

			@Override
			public void call(Entity owner, Entity card, Entity enemy) {
				Media.weaponSwing.play();
				
				int damage = CombatUtil.getDamage(owner, enemy, 20);
				Comp.util(enemy, VitalsComp.class).damage(damage);
				Sys.out.println("Dealt " + damage + " damage.");
			}
			
		};
		getEngine().addEntity(b.Entity());
		b.free();
	}
	
	public void addHealAllCard() {
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
	}

	public void addX2Card() {
		EntityBuilder b = buildCard(Media.x2, "x2");
		b.TurnActionComp().turnAction.makesTargetMultiplicity = 2;
		new CardCallback(this, b) {

			@Override
			public void call(Entity owner, Entity card, Entity activeCard) {
				DescriptionComp d = Comp.DescriptionComp.get(activeCard);
				Sys.out.println("Increasing multiplicity for: " + d.name);
				Comp.TurnActionComp.get(activeCard).turnAction.multiplicity*=2;
			}
			
		};
		getEngine().addEntity(b.Entity());
		b.free();
	}
	
	public void addAllCard() {
		EntityBuilder b = buildCard(Media.all, "All");
		b.TurnActionComp().turnAction.makesTargetAll = true;
		new DoesntHaveAllCallback(this, b);
		new CardCallback(this, b) {

			@Override
			public void call(Entity owner, Entity card, Entity activeCard) {
				DescriptionComp d = Comp.DescriptionComp.get(activeCard);
				Sys.out.println("Setting all ford: " + d.name);
				Comp.TurnActionComp.get(activeCard).turnAction.all = true;
			}
			
		};
		getEngine().addEntity(b.Entity());
		b.free();
	}

}
