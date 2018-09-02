package com.jharter.game.control;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Queue;
import com.badlogic.gdx.utils.TimeUtils;
import com.jharter.game.controller.XboxMapping;
import com.jharter.game.network.endpoints.GameClient;
import com.jharter.game.network.endpoints.GameNetwork.EntityData;
import com.jharter.game.network.packets.Packets.InputPacket;
import com.jharter.game.util.id.ID;

public class GameInput extends InputAdapter implements InputProcessor, ControllerListener {
	
	private static final int CONTROL_FRAME_DELAY = 3;
	
	private static boolean isActive(GlobalInputState inputState) {
		return inputState.up || inputState.down || inputState.left || inputState.right || inputState.accept || inputState.cancel; 
	}
	
	private static GlobalInputState copy(GlobalInputState inputState) {
		GlobalInputState copy = new GlobalInputState();
		copy.id = inputState.id;
		copy.up = inputState.up;
		copy.down = inputState.down;
		copy.left = inputState.left;
		copy.right = inputState.right;
		copy.accept = inputState.accept;
		copy.cancel = inputState.cancel;
		copy.time = inputState.time;
		return copy;
	}
	
	// Empty input state so we don't have to create a new empty one over and over
	private final GlobalInputState noState = new GlobalInputState();
	
	// This is the state that catches the actual control events as they happen
	private GlobalInputState realtimeState = new GlobalInputState();
	
	// This is the state we'll use in our render loop on the client, this will
	// be an older state than the current listening state and is the reason
	// we can delay our input to better stay in time with the server.
	private GlobalInputState renderState = noState;
	
	// This is the state we use for our client only. We will never send this
	// state to the server. It will be updated and displayed in real time.
	private LocalInputState localRenderState = new LocalInputState();
	
	// This is the state we'll send to the server
	private GlobalInputState stateToSend = null;
	
	private Queue<GlobalInputState> stateHistory = new Queue<GlobalInputState>();
	private boolean lastTickWasActive = false;
	private boolean sentInputLastTick = false;

	// CAMERA
    private OrthographicCamera camera;
    
    // SCREEN
    private int screenWidth;
    private int screenHeight;
    
    // Joystick flags
	private boolean lastDirectionJoystick = false;
	private boolean lastAxisHorz = false;
    
    public GameInput(int screenWidth, int screenHeight, OrthographicCamera camera){
        this.camera = camera;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }
    
    private void setMouseClickedPos(int screenX, int screenY){
        // Set mouse position (flip screen Y)
    	if(localRenderState.mouseClickPos == null) {
    		localRenderState.mouseClickPos = new Vector2();
    	}
    	if(localRenderState.mapClickPos == null) {
    		localRenderState.mapClickPos = new Vector2();
    	}
    	localRenderState.mouseClickPos.set(screenX, screenHeight - screenY);
    	localRenderState.mapClickPos.set(get_map_coords(localRenderState.mouseClickPos));
    }
        
    private Vector2 get_map_coords(Vector2 mouseCoords){
        Vector3 v3 = new Vector3(mouseCoords.x, screenHeight - mouseCoords.y, 0);
        this.camera.unproject(v3);
        return new Vector2(v3.x,v3.y);
    }
    
    /*
     * This method will add a delay to the current input so that the client can stay
     * in sync with the server. This delay is based off of frame counts. It will 
     * also make sure to take of snapshot of input to send to the server for this
     * particular tick.
     */
    public void tick(float deltaTime) {
    	// Check if we currently have active input
    	boolean isActive = isActive(realtimeState);
    	
    	// Only update the state if we have active input or previously had
    	// active input and now need to record that we're newly inactive.
    	if(isActive || (lastTickWasActive && !isActive)) {
    		stateToSend = copy(realtimeState);
    		stateToSend.time = TimeUtils.millis();
    		stateHistory.addLast(stateToSend);
    		sentInputLastTick = true;
    	} else {
    		stateToSend = null;
    		sentInputLastTick = false;
    	}
    	lastTickWasActive = isActive;
    	
    	// Decide whether we should use the oldest history state or not
    	// depending on if we've waited enough frames. This also takes
    	// into account when we've stopped giving input but still need
    	// to keep reading from our history until it's empty.
    	boolean popOldestState = stateHistory.size >= CONTROL_FRAME_DELAY || (stateHistory.size > 0 && !sentInputLastTick);
		
    	// Debug println
    	/*if(!popOldestState && stateHistory.size > 0) {
			Sys.out.println("Ignoring inputState (" + stateHistory.size + " / " + CONTROL_FRAME_DELAY + ")");
		}*/
		
    	// Either use the oldest state in our history or just use a 
    	// state that isn't inputing anything.
		if(popOldestState) {
			renderState = stateHistory.removeFirst();
		} else {
			renderState = noState;
		}
    }
    
    public void setInputState(GlobalInputState inputState) {
    	this.renderState = inputState;
    }
    
    public void maybeSendInputState(GameClient client, ID focusId) {
    	if(stateToSend != null) {
    		stateToSend.id = focusId;
    		client.send(InputPacket.newInstance(stateToSend));
    	}
    }
    
    public void addInputState(EntityData entityData) {
    	entityData.input = renderState;
    }
    
    public void setRenderStateToRealTimeState() {
    	renderState = realtimeState;
    }
    
    @Override
    public boolean keyDown(int keyCode) {
        switch (keyCode) {
            case Keys.DOWN:
                realtimeState.down = true;
                break;
            case Keys.UP:
            	realtimeState.up = true;
                break;
            case Keys.LEFT:
            	realtimeState.left = true;
                break;
            case Keys.RIGHT:
            	realtimeState.right = true;
                break;
            case Keys.W:
            	realtimeState.up = true;
                break;
            case Keys.A:
            	realtimeState.left = true;
                break;
            case Keys.S:
            	realtimeState.down = true;
                break;
            case Keys.D:
            	realtimeState.right = true;
                break;
        }
        return false;
    }
    
    @Override
    public boolean keyUp(int keycode) {
        switch (keycode) {
            case Keys.DOWN:
            	realtimeState.down = false;
                break;
            case Keys.UP:
            	realtimeState.up = false;
                break;
            case Keys.LEFT:
            	realtimeState.left = false;
                break;
            case Keys.RIGHT:
            	realtimeState.right = false;
                break;
            case Keys.W:
            	realtimeState.up = false;
                break;
            case Keys.A:
            	realtimeState.left = false;
                break;
            case Keys.S:
            	realtimeState.down = false;
                break;
            case Keys.D:
            	realtimeState.right = false;
                break;
            case Keys.E:
            	realtimeState.accept = true;
                break;
            case Keys.Q:
            	realtimeState.cancel = true;
                break;
            case Keys.ESCAPE:
                Gdx.app.exit();
                break;
            case Keys.BACKSPACE:
            	localRenderState.debug = !localRenderState.debug;
                break;
            case Keys.R:
            	localRenderState.reset = true;
                break;
            case Keys.I:
            	localRenderState.inventory = true;
                break;
        }
        return false;
    }
    
    public void reset() {
    	realtimeState.down = false;
    	realtimeState.up = false;
    	realtimeState.left = false;
    	realtimeState.right = false;
    	realtimeState.up = false;
    	realtimeState.left = false;
    	realtimeState.down = false;
    	realtimeState.right = false;
    	realtimeState.accept = false;
    	realtimeState.cancel = false;
    	localRenderState.reset = false;
    	localRenderState.inventory = false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }
    
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if(pointer == 0 && button == 0){
        	setLeftMouseBtn(true);
        } else if (pointer == 0 && button == 0){
        	setRightMouseBtn(true);
        }
    
        setMouseClickedPos(screenX, screenY);
        return false;
    }
    
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if(pointer == 0 && button == 0){
        	setLeftMouseBtn(false);
        	setProcessedClick(false);
        } else if (pointer == 0 && button == 0){
        	setRightMouseBtn(false);
        }
    
        setMouseClickedPos(screenX, screenY);
        return false;
    }
    
    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        setMouseClickedPos(screenX, screenY);
        return false;
    }
    
    @Override
    public boolean mouseMoved(int screenX, int screenY) {
    	if(localRenderState.mousePos == null) {
    		localRenderState.mousePos = new Vector2();
    	}
    	localRenderState.mousePos.set(screenX, screenHeight - screenY);
        return false;
    }
    
    @Override
    public boolean scrolled(int amount) {
        return false;
    }
    
    
    // ***************** INPUT STATE DELEGATORS ************************* 
    
    public boolean isUp() {
		return renderState.up;
	}
	
	public boolean isDown() {
		return renderState.down;
	}
	
	public boolean isLeft() {
		return renderState.left;
	}
	
	public boolean isRight() {
		return renderState.right;
	}
	
	public boolean isAccept() {
		return renderState.accept;
	}
	
	public boolean isCancel() {
		return renderState.cancel;
	}
	
	public boolean isInventory() {
		return localRenderState.inventory;
	}
	
	public boolean isDebug() {
		return localRenderState.debug;
	}
	
	public boolean isReset() {
		return localRenderState.reset;
	}
	
	public boolean isLeftMouseBtn() {
		return localRenderState.leftMouseBtn;
	}
	
	public boolean isRightMouseBtn() {
		return localRenderState.rightMouseBtn;
	}
	
	public boolean isProcessedClick() {
		return localRenderState.processedClick;
	}
	
	public Vector2 getMouseClickPos() {
		return localRenderState.mouseClickPos;
	}
	
	public Vector2 getMousePos() {
		return localRenderState.mousePos;
	}
	
	public Vector2 getMapClickPos() {
		return localRenderState.mapClickPos;
	}
	
	public long getTime() {
		return renderState.time;
	}
	
	public void setUp(boolean up) {
		realtimeState.up = up;
	}
	
	public void setDown(boolean down) {
		realtimeState.down = down;
	}
	
	public void setLeft(boolean left) {
		realtimeState.left = left;
	}
	
	public void setRight(boolean right) {
		realtimeState.right = right;
	}
	
	public void setAccept(boolean accept) {
		realtimeState.accept = accept;
	}
	
	public void setCancel(boolean cancel) {
		realtimeState.cancel = cancel;
	}
	
	public void setInventory(boolean inventory) {
		localRenderState.inventory = inventory;
	}
	
	public void setDebug(boolean debug) {
		localRenderState.debug = debug;
	}
	
	public void setReset(boolean reset) {
		localRenderState.reset = reset;
	}
	
	public void setLeftMouseBtn(boolean leftMouseBtn) {
		localRenderState.leftMouseBtn = leftMouseBtn; 
	}
	
	public void setRightMouseBtn(boolean rightMouseBtn) {
		localRenderState.rightMouseBtn = rightMouseBtn;
	}
	
	public void setProcessedClick(boolean processedClick) {
		localRenderState.processedClick = processedClick;
	}
	
	public void setMouseClickPos(Vector2 mouseClickPos) {
		localRenderState.mouseClickPos = mouseClickPos;
	}
	
	public void setMousePos(Vector2 mousePos) {
		localRenderState.mousePos = mousePos;
	}
	
	public void setMapClickPos(Vector2 mapClickPos) {
		localRenderState.mapClickPos = mapClickPos;
	}
	
	public void setTime(long time) {
		realtimeState.time = time;
	}
	
	private int buttonCodeToKeyCode(int buttonCode) {
		if(buttonCode == XboxMapping.DPAD_UP) {
			return Keys.UP;
		} else if(buttonCode == XboxMapping.DPAD_DOWN) {
			return Keys.DOWN;
		} else if(buttonCode == XboxMapping.DPAD_LEFT) {
			return Keys.LEFT;
		} else if(buttonCode == XboxMapping.DPAD_RIGHT) {
			return Keys.RIGHT;
		} else if(buttonCode == XboxMapping.A) {
			return Keys.E;
		} else if(buttonCode == XboxMapping.B) {
			return Keys.Q;
		}
		return -1;
	}
	
	private int povDirectionToButtonCode(PovDirection value) {
		if(value == PovDirection.north) {
			return XboxMapping.DPAD_UP;
		} else if(value == PovDirection.south) {
			return XboxMapping.DPAD_DOWN;
		} else if(value == PovDirection.west) {
			return XboxMapping.DPAD_LEFT;
		} else if(value == PovDirection.east) {
			return XboxMapping.DPAD_RIGHT;
		}
		return -1;
	}
	
	private PovDirection axisCodeAndValueToPovDirection(int axisCode, float value) {
		if(axisCode == XboxMapping.L_STICK_HORIZONTAL_AXIS) {
			lastAxisHorz = true;
			if(value < 0) {
				return PovDirection.west;
			}
			return PovDirection.east;
		} else if(axisCode == XboxMapping.L_STICK_VERTICAL_AXIS) {
			lastAxisHorz = false;
			if(value < 0) {
				return PovDirection.north;
			}
			return PovDirection.south;
		}
		return null;
	}

	@Override
	public void connected(Controller controller) {
		
	}

	@Override
	public void disconnected(Controller controller) {
		
	}

	@Override
	public boolean buttonDown(Controller controller, int buttonCode) {
		keyDown(buttonCodeToKeyCode(buttonCode));
		return false;
	}

	@Override
	public boolean buttonUp(Controller controller, int buttonCode) {
		keyUp(buttonCodeToKeyCode(buttonCode));
		return false;
	}

	@Override
	public boolean axisMoved(Controller controller, int axisCode, float value) {
		if(axisCode != XboxMapping.L_STICK_HORIZONTAL_AXIS && axisCode != XboxMapping.L_STICK_VERTICAL_AXIS) {
			return false;
		}
		if(Math.abs(value) < 0.4f) {
			if(axisCode == XboxMapping.L_STICK_HORIZONTAL_AXIS && !lastAxisHorz) {
				return false;
			}
			if(axisCode == XboxMapping.L_STICK_VERTICAL_AXIS && lastAxisHorz) {
				return false;
			}
			return povMoved(controller, 0, PovDirection.center, true);
		}
		return povMoved(controller, 0, axisCodeAndValueToPovDirection(axisCode, value), true);
	}

	@Override
	public boolean povMoved(Controller controller, int povCode, PovDirection value) {
		return povMoved(controller, povCode, value, false);
	}
	
	public boolean povMoved(Controller controller, int povCode, PovDirection value, boolean isJoystick) {
		if(value == PovDirection.center) {
			if(isJoystick && !lastDirectionJoystick) {
				return false;
			}
			buttonUp(controller, XboxMapping.DPAD_UP);
			buttonUp(controller, XboxMapping.DPAD_DOWN);
			buttonUp(controller, XboxMapping.DPAD_LEFT);
			buttonUp(controller, XboxMapping.DPAD_RIGHT);
		} else {
			lastDirectionJoystick = isJoystick;
			//Sys.out.println("Direction input");
			buttonDown(controller, povDirectionToButtonCode(value));
		}
		return false;
	}


	@Override
	public boolean xSliderMoved(Controller controller, int sliderCode, boolean value) {
		return false;
	}

	@Override
	public boolean ySliderMoved(Controller controller, int sliderCode, boolean value) {
		return false;
	}

	@Override
	public boolean accelerometerMoved(Controller controller, int accelerometerCode, Vector3 value) {
		return false;
	}

}