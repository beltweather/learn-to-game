package com.jharter.game.vitals;

public class Vitals {
	
	public int maxHealth = 0;
	public int weakHealth = 0;
	public int health = 0;
	
	public Vitals() {
		
	}
	
	public void setFrom(Vitals vitals) {
		maxHealth = vitals.maxHealth;
		weakHealth = vitals.weakHealth;
		health = vitals.health;
	}

	public void clear() {
		maxHealth = 0;
		weakHealth = 0;
		health = 0;
	}
	
}
