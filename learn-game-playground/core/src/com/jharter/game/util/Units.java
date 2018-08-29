package com.jharter.game.util;

public class Units {

	private Units() {}
	
	public static final float PIXELS_PER_UNIT = 12;
	private static final float TARGET_PIXEL_HEIGHT = 1080;
	public static final float WORLD_HEIGHT_IN_UNITS = TARGET_PIXEL_HEIGHT / PIXELS_PER_UNIT;
	
	/**
	 * Convenience method for developing. If you don't change your pixels per unit
	 * at any time during development, then this will act like the identity. What it
	 * does is allow you to experiment with different pixels per unit while also 
	 * letting you track all hard-coded unit constants that you might be using.
	 * @param units The amount of units you're hardcoding
	 * @return The equivalent amount of units in the current base system
	 */
	public static float u12(float units) {
		float assumedPixelsPerUnit = 12;
		if(assumedPixelsPerUnit != PIXELS_PER_UNIT) {
			return units * assumedPixelsPerUnit / PIXELS_PER_UNIT;
		}
		return units;
	}
	
	public static float u1(float units) {
		float assumedPixelsPerUnit = 1;
		if(assumedPixelsPerUnit != PIXELS_PER_UNIT) {
			return units * assumedPixelsPerUnit / PIXELS_PER_UNIT;
		}
		return units;
	}
	
}
