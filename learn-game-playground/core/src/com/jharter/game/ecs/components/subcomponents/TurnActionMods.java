package com.jharter.game.ecs.components.subcomponents;

import com.jharter.game.primitives.boolean_;
import com.jharter.game.primitives.int_;

public class TurnActionMods implements Pendable<TurnActionMods> {

	public final boolean_ all = new boolean_().d(false);
	public final int_ multiplicity = new int_().d(1);

	@Override
	public void setToDefault() {
		all.setToDefault();
		multiplicity.setToDefault();
	}

	@Override
	public void resetPending() {
		all.resetPending();
		multiplicity.resetPending();
	}

	@Override
	public void clear() {
		all.clear();
		multiplicity.clear();
	}

}
