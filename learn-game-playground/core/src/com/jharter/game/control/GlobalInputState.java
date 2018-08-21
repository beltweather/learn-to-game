package com.jharter.game.control;

import com.jharter.game.util.id.ID;

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
	public ID id;
	public boolean up, down, left, right, accept, cancel;
	public long time;
}
