package com.jharter.game.ecs.components.subcomponents;

public class TurnActionMods {

	public boolean defaultAll = false;
	public boolean all = false;
	public int defaultMultiplicity = 1;
	public int multiplicity = 1;

	public TurnActionMods setFrom(TurnActionMods mods) {
		defaultAll = mods.defaultAll;
		all = mods.all;
		defaultMultiplicity = mods.defaultMultiplicity;
		multiplicity = mods.multiplicity;
		return this;
	}

	public void clear() {
		defaultAll = false;
		all = false;
		defaultMultiplicity = 1;
		multiplicity = 1;
	}

}
