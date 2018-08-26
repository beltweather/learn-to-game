package com.jharter.game.tween;

public enum TweenType {
	
	POSITION_X(0), 
	POSITION_Y(1), 
	POSITION_XY(2),
	POSITION_XY_SCALE(3),
	SCALE_X(4), 
	SCALE_Y(5), 
	SCALE_XY(6), 
	ALPHA(7), 
	ANGLE(8);
	
	//COLOR_RGB(8),
	//COLOR_RGBA(9),
	//COLOR_A(10);
	
	private int index;
	
	private TweenType(int index) {
		this.index = index;
	}
	
	public int asInt() {
		return index;
	}
	
	public static TweenType get(int index) {
		return TweenType.values()[index];
	}
	
}
