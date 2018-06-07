package com.jharter.game.control;

/**
 * A simple class for storing and sending input that should be
 * communicated between the server and client. This class should
 * be kept as lean as possible, hence, no methods or constructors.
 * This allows Kryo to serialize it efficiently.
 * 
 * @author Jon
 *
 */
public class GlobalInputState {
	public String id;
	public boolean up, down, left, right, interact;
	public long time;
}
