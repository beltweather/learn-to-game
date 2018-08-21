package com.jharter.game.screens.impl;

import com.jharter.game.network.endpoints.EndPointHelper;
import com.jharter.game.screens.GameScreen;
import com.jharter.game.stages.GameStage;
import com.jharter.game.stages.impl.BattleStage;
import com.jharter.game.stages.impl.TestStageA;

public class TestStageScreen extends GameScreen {

	public TestStageScreen(EndPointHelper endPointHelper) {
		super(endPointHelper);
	}
	
	@Override
	protected GameStage create(EndPointHelper endPointHelper) {
		GameStage testStageA = new TestStageA(endPointHelper);
		//GameStage testStageA = new BattleStage(endPointHelper);
		addStage(testStageA);
	 	return testStageA;
	}

}