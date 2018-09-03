package com.jharter.game.stages.battlestage;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;
import com.jharter.game.ashley.components.Comp;
import com.jharter.game.ashley.components.Components.DescriptionComp;
import com.jharter.game.ashley.components.Components.VitalsComp;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.EntityBuilder;
import com.jharter.game.ashley.components.subcomponents.Callback.DoesntHaveAllCallback;
import com.jharter.game.ashley.components.subcomponents.CombatUtil;
import com.jharter.game.ashley.components.subcomponents.VoidCallback.CardCallback;
import com.jharter.game.ashley.components.subcomponents.VoidCallback.EnemyCallback;
import com.jharter.game.ashley.components.subcomponents.VoidCallback.FriendCallback;
import com.jharter.game.ashley.components.subcomponents.VoidCallback.FriendEnemyCallback;
import com.jharter.game.ashley.entities.EntityUtil;
import com.jharter.game.util.Sys;
import com.jharter.game.util.id.ID;

import uk.co.carelesslabs.Enums.EntityType;
import uk.co.carelesslabs.Media;

public class CardHelper {
	
	//TextureRegion swampTexture = GraphicsUtil.buildCardTexture(Media.swamp, Media.warrior, "Damage Enemy Very Badly");

	private CardHelper() {}
	
	public static EntityBuilder buildCard(PooledEngine engine, ID playerID, ZoneComp zone, Texture texture, String name) {
		EntityBuilder b = EntityUtil.buildBasicEntity(engine, 
				EntityType.CARD, 
				new Vector3(-450,-475,0), 
				texture);
		b.CardComp().playerID = playerID;
		b.DescriptionComp().name = name;
		b.SpriteComp();
		b.VelocityComp();
		b.BodyComp();
		zone.add(b);
		return b;
	}
	
	public static void addDrainCard(PooledEngine engine, ID playerID, ZoneComp zone) {
		EntityBuilder b = buildCard(engine, playerID, zone, Media.drainLife, "Drain Life");
		new FriendEnemyCallback(b) {

			@Override
			public void call(Entity owner, Entity card, Entity friend, Entity enemy) {
				int damage = CombatUtil.getDamage(owner, enemy, 13);
				
				VitalsComp vEnemy = Comp.VitalsComp.get(enemy);
				VitalsComp vFriend = Comp.VitalsComp.get(friend);
				int origHealthFriend = vFriend.health;
				int origHealthEnemy = vEnemy.health;
				
				vEnemy.damage(damage);
				vFriend.heal(damage);
				
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
				
				if(vEnemy.isNearDeath()) {
					Sys.out.println(nameEnemy + " is near death.");
				} else if(vEnemy.isDead()) {
					Sys.out.println(nameEnemy + " is dead.");
				}
			}
			
		};
		engine.addEntity(b.Entity());
		b.free();
	}
	
	public static void addAttackCard(PooledEngine engine, ID playerID, ZoneComp zone) {
		EntityBuilder b = buildCard(engine, playerID, zone, Media.attack, "Attack");
		new EnemyCallback(b) {

			@Override
			public void call(Entity owner, Entity card, Entity enemy) {
				int damage = CombatUtil.getDamage(owner, enemy, 20);
				Comp.VitalsComp.get(enemy).damage(damage);
				Sys.out.println("Dealt " + damage + " damage.");
			}
			
		};
		engine.addEntity(b.Entity());
		b.free();
	}
	
	public static void addAttackAllCard(PooledEngine engine, ID playerID, ZoneComp zone) {
		EntityBuilder b = buildCard(engine, playerID, zone, Media.attackAll, "Attack All");
		b.TurnActionComp().turnAction.defaultAll = true;
		b.TurnActionComp().turnAction.all = true;
		new EnemyCallback(b) {

			@Override
			public void call(Entity owner, Entity card, Entity enemy) {
				int damage = CombatUtil.getDamage(owner, enemy, 20);
				Comp.VitalsComp.get(enemy).damage(damage);
				Sys.out.println("Dealt " + damage + " damage.");
			}
			
		};
		engine.addEntity(b.Entity());
		b.free();
	}
	
	public static void addHealAllCard(PooledEngine engine, ID playerID, ZoneComp zone) {
		EntityBuilder b = buildCard(engine, playerID, zone, Media.healAll, "Heal All");
		b.TurnActionComp().turnAction.defaultAll = true;
		b.TurnActionComp().turnAction.all = true;
		new FriendCallback(b) {

			@Override
			public void call(Entity owner, Entity card, Entity friend) {
				int hp = 50;
				Comp.VitalsComp.get(friend).heal(hp);
				Sys.out.println("Healed " + hp + " hp to " + Comp.DescriptionComp.get(friend).name);
			}
			
		};
		engine.addEntity(b.Entity());
		b.free();
	}

	public static void addX2Card(PooledEngine engine, ID playerID, ZoneComp zone) {
		EntityBuilder b = buildCard(engine, playerID, zone, Media.x2, "x2");
		b.TurnActionComp().turnAction.makesTargetMultiplicity = 2;
		new CardCallback(b) {

			@Override
			public void call(Entity owner, Entity card, Entity activeCard) {
				DescriptionComp d = Comp.DescriptionComp.get(activeCard);
				Sys.out.println("Increasing multiplicity for: " + d.name);
				Comp.TurnActionComp.get(activeCard).turnAction.multiplicity*=2;
			}
			
		};
		engine.addEntity(b.Entity());
		b.free();
	}
	
	public static void addAllCard(PooledEngine engine, ID playerID, ZoneComp zone) {
		EntityBuilder b = buildCard(engine, playerID, zone, Media.all, "All");
		b.TurnActionComp().turnAction.makesTargetAll = true;
		new DoesntHaveAllCallback(b);
		new CardCallback(b) {

			@Override
			public void call(Entity owner, Entity card, Entity activeCard) {
				DescriptionComp d = Comp.DescriptionComp.get(activeCard);
				Sys.out.println("Setting all ford: " + d.name);
				Comp.TurnActionComp.get(activeCard).turnAction.all = true;
			}
			
		};
		engine.addEntity(b.Entity());
		b.free();
	}

}
