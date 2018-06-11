package uk.co.carelesslabs;

import java.util.Comparator;
import java.util.HashMap;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.TimeUtils;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import com.jharter.game.control.GlobalInputState;
import com.jharter.game.control.Input;
import com.jharter.game.network.GameClient;
import com.jharter.game.network.GameServer;
import com.jharter.game.network.GameNetwork.AddPlayer;
import com.jharter.game.network.GameNetwork.AddPlayers;
import com.jharter.game.network.GameNetwork.EntityData;
import com.jharter.game.network.GameNetwork.MoveUser;
import com.jharter.game.network.GameNetwork.RemoveEntities;
import com.jharter.game.network.GameNetwork.RequestPlayer;
import com.jharter.game.network.GameNetwork.SnapshotPacket;
import com.jharter.game.util.EntityFactory;
import com.jharter.game.util.IDUtil;

import uk.co.carelesslabs.box2d.Box2DWorld;
import uk.co.carelesslabs.entity.Bird;
import uk.co.carelesslabs.entity.GameEntity;
import uk.co.carelesslabs.entity.Hero;
import uk.co.carelesslabs.map.Island;
import uk.co.carelesslabs.map.Tile;
import uk.co.carelesslabs.ui.SquareMenu;

public class OnlineEvoGame extends ApplicationAdapter {
	
	private DelayedRemovalArray<SnapshotPacket> snapshotPackets = new DelayedRemovalArray<SnapshotPacket>();
	
	GameServer server;
	GameClient client;
	
	OrthographicCamera camera;
    //public Control control;
    SpriteBatch batch;
    Matrix4 screenMatrix;
    Box2DWorld box2D;

    // Display Size
    private int displayW;
    private int displayH;

    // Hero
    private String mainHeroId;
    private Hero mainHero;
    private ObjectMap<String, Hero> heroes = new ObjectMap<String, Hero>();
    private ObjectMap<String, Input> heroInput = new ObjectMap<String, Input>();
    
    // Island
    Island island;
    
    // TIME
    float time;
    
    // Menu test
    SquareMenu squareMenu;
    
    private boolean headless;
    private boolean serverSide;
    //public Input input;
    
    public OnlineEvoGame(boolean headless, boolean serverSide) {
    	this.headless = headless;
    	this.serverSide = serverSide;
    }
    
    public Input getInput() {
    	if(!heroInput.containsKey(mainHeroId)) {
    		return null;
    	}
    	return heroInput.get(mainHeroId);
    }
    
    public boolean isVisual() {
    	return !headless;
    }
    
    public boolean isHeadless() {
    	return headless;
    }
    
    public boolean isClientSide() {
    	return !serverSide;
    }
    
    public boolean isServerSide() {
    	return serverSide;
    }
    
    private void addHero(Hero hero) {
    	island.entities.add(hero);
    	heroes.put(hero.id, hero);
    	if(hero.id.equals(mainHeroId) || (isServerSide() && heroes.size == 1)) {
    		mainHeroId = hero.id;
    		mainHero = hero;
    	}
    	Input input = new Input(displayW, displayH, camera);
    	heroInput.put(hero.id, input);
    	if(isClientSide() && hero.id.equals(mainHeroId)) {
    	    Gdx.input.setInputProcessor(input);
    	}
    }
    
    @Override
    public void create() {
    	Media.load_assets();
        batch = new SpriteBatch();
        
        // CAMERA
        displayW = Gdx.graphics.getWidth();
        displayH = Gdx.graphics.getHeight();
        
        // For 800x600 we will get 266*200
        int h = (int) (displayH/Math.floor(displayH/160));
        int w = (int) (displayW/(displayH/ (displayH/Math.floor(displayH/160))));
        
        camera = new OrthographicCamera(w,h);
        camera.zoom = .6f;
        
        // Used to capture Keyboard Input
        //control = new Control(displayW, displayH, camera);
        //Gdx.input.setInputProcessor(control);
        
        //input = new Input(displayW, displayH, camera);
        //Gdx.input.setInputProcessor(input);
        
        // Setup Matrix4 for HUD
        screenMatrix = new Matrix4(batch.getProjectionMatrix().setToOrtho2D(0, 0, displayW, displayH));
        
        // Box2D
        box2D = new Box2DWorld();
        
        // Island
        island = new Island(box2D);
        
        // Hero
        //mainHero = EntityFactory.newHero(island.centreTile.pos, box2D);
        //island.entities.add(mainHero);
       
        // HashMap of Entities for collisions
        //box2D.populateEntityMap(island.entities);  
        
        /*if(getInput() != null) {
        	getInput().setReset(true);
        }*/
        
        //Menu
        squareMenu = new SquareMenu(this);
        
        if(isServerSide()) {
        	System.out.println("Starting Server");
			server = new GameServer() {
	
				@Override
				public void received(Connection c, Object object, Server server) {
					if(object instanceof GlobalInputState) {
						GlobalInputState state = (GlobalInputState) object;
						String heroId = state.id;
						if(heroInput.containsKey(heroId)) {
							heroInput.get(heroId).setInputState(state);
						}
					} else if(object instanceof RequestPlayer) {
						RequestPlayer request = (RequestPlayer) object;
						Hero hero = EntityFactory.newHero(request.id, island.centreTile.pos, box2D);
						addHero(hero);
				        //box2D.populateEntityMap(island.entities);
						AddPlayers addHeroes = new AddPlayers();
						for(Hero h : heroes.values()) {
							AddPlayer addHero = new AddPlayer();
							addHero.id = h.id;
							addHero.x = h.pos.x;
							addHero.y = h.pos.y;
							addHero.z = h.pos.z;
							addHeroes.players.add(addHero);
						}
						System.out.println("Server sending " + addHeroes.players.size + " heroes to all clients.");
						server.sendToAllTCP(addHeroes);
					} else if(object instanceof RemoveEntities) {
						RemoveEntities removeEntities = (RemoveEntities) object;
						island.markEntitiesAsRemoved(box2D, removeEntities);
						server.sendToAllTCP(removeEntities);
					}
				} 
				
			};
			server.start();
        } else {
        	System.out.println("Starting Client");
    		client = new GameClient() {

    			@Override
    			public void received(GameClient gameClient, Connection connection, Object object) {
    				Client client = gameClient.getKryoClient();
    				if(object instanceof MoveUser) {
    					serverMoveUser = (MoveUser) object;
    					serverMoveUserTime = System.currentTimeMillis();
    				} else if(object instanceof SnapshotPacket) {
    					/*snapshotPackets.begin();
    					DelayedRemovalArray<SnapshotPacket> packets = new DelayedRemovalArray<SnapshotPacket>(snapshotPackets);
    					packets.add((SnapshotPacket) object);
    					packets.sort(new Comparator<SnapshotPacket>() {

							@Override
							public int compare(SnapshotPacket arg0, SnapshotPacket arg1) {
								return (int) (arg0.time - arg1.time);
							}
    						
    					});
    					DelayedRemovalArray<SnapshotPacket> temp = snapshotPackets;
    					snapshotPackets = packets;
    					temp.end();*/    				
    				} else if(object instanceof AddPlayers) {
    					AddPlayers addHeroes = (AddPlayers) object;
    					System.out.println("Client " + client.getID() + " received " + addHeroes.players.size + " heroes from server.");
    					for(AddPlayer addHero : addHeroes.players) {
    						if(heroes.containsKey(addHero.id)) {
    							System.out.println("Client " + client.getID() + " skipping hero " + addHero.id);
    							continue;
    						}
    						System.out.println("Client " + client.getID() + " adding new hero " + addHero.id);
        					Vector3 pos = new Vector3(addHero.x, addHero.y, addHero.z);
        					Hero hero = EntityFactory.newHero(addHero.id, pos, box2D);
        					addHero(hero);
    					}
    			        //box2D.populateEntityMap(island.entities);
    				} else if(object instanceof RemoveEntities) {
						RemoveEntities removeEntities = (RemoveEntities) object;
						island.markEntitiesAsRemoved(box2D, removeEntities);
					}
    			}
    			
    		};
    		client.start();
    		
    		mainHeroId = IDUtil.newID();
    		RequestPlayer requestHero = new RequestPlayer();
    		requestHero.id = mainHeroId;
    		client.sendTCP(requestHero);
        }
    }
    
    private long getRenderTime(long currentTime) {
    	return currentTime - (client.getPing() + 50);
    }
    
    float minDiff = 1f;
    
    float alpha = 0.01f;
    
    private MoveUser serverMoveUser = null;
    private int frameCount = -1;
    
    private long lastServerMoveUserTime = 0;
    private long serverMoveUserTime = 0;
 
    private float fpsTime = 0;
    private long fpsFrames = 0;
    
    private boolean didInitialReset = false;
    
    @Override
    public void render () {
    	if(mainHero == null) {
        	return;
        }
    	
    	Input input = getInput();
    	if(input == null) {
    		return;
    	}
    	
    	float deltaTime = Gdx.graphics.getDeltaTime();
    	long currentTime = TimeUtils.millis();
    	frameCount++;
    	if(isClientSide()) {
    		client.tick(deltaTime);
    		
    		// We only need to tick the input manager on the client since the server
    		// will get input updates via messages.
    		input.tick(deltaTime);
    		
    		// Send the input state to the server if there was any activity this tick.
    		input.maybeSendInputState(client, mainHeroId);
    	}
    	
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // GAME LOGIC
        if(!didInitialReset) {// || input.isReset()){
            resetGameState();
            didInitialReset = true;
        }
        
        if(input.isInventory()){
            mainHero.inventory.print();
            input.setInventory(false);
        }
        
        // Menu Logic
        input.setProcessedClick(squareMenu.checkClick(input.getMouseClickPos(), input.isProcessedClick()));
        input.setProcessedClick(squareMenu.build.checkClick(input.getMouseClickPos(), input.isProcessedClick()));
        squareMenu.checkHover(input.getMousePos());
        
        if(isServerSide()) {
        	for(Hero hero : heroes.values()) {
            	Input in = heroInput.get(hero.id);
            	hero.update(in);
            	in.setInteract(false);
            }
        } else {
        	mainHero.update(input);
        }
        
        DelayedRemovalArray<SnapshotPacket> packets = snapshotPackets;
        if(isClientSide() && packets.size > 1) {
        	packets.begin();
        	long renderTime = getRenderTime(currentTime);
        	int newestIdx = -1;
        	for(int i = 0; i < packets.size; i++) {
        		if(i > 0 && packets.get(i).time >= renderTime) {
        			newestIdx = i;
        			for(int j = 0; j < newestIdx - 1; j++) {
        				packets.removeIndex(j); // Delayed removal won't happen until "end" is called
        			}
        			break;
        		}
        	}
        	if(newestIdx > -1) {
        		SnapshotPacket snapshot1 = packets.get(newestIdx - 1);
        		SnapshotPacket snapshot2 = packets.get(newestIdx);
        		if(snapshot2.time - snapshot1.time > 0) {
        			for(int i = 0; i < snapshot1.entityDatas.size(); i++) {
        				EntityData entityData1 = snapshot1.entityDatas.get(i);
        				EntityData entityData2 = snapshot2.entityDatas.get(i);
        				String heroId = entityData1.id;
        				if(heroes.containsKey(heroId)) {
        					Hero hero = heroes.get(heroId);
        					Input in = heroInput.get(hero.id);
        					if(entityData1.input != null && !hero.id.equals(mainHeroId)) {
        						in.setInputState(entityData1.input);
        						hero.update(in);
        					}
        					if(entityData2.input != null && !hero.id.equals(mainHeroId)) {
        						in.setInputState(entityData2.input);
        						hero.update(in);
        					}
        					hero.update(snapshot1.time, entityData1, snapshot2.time, entityData2, renderTime);
        				}
        			}
        		}
        	}
        	packets.end();
        }
        
        for(Hero hero : heroes.values()) {
        	hero.moveTowardTarget();
        }
        
        if(isServerSide() && frameCount == 3) {
        	frameCount = -1;
        	SnapshotPacket snapshotPacket = new SnapshotPacket();
        	snapshotPacket.time = TimeUtils.millis();
        	for(Hero hero : heroes.values()) {
        		EntityData entityData = new EntityData();
            	entityData.id = hero.id;
            	entityData.x = hero.pos.x;
            	entityData.y = hero.pos.y;
            	heroInput.get(hero.id).addInputState(entityData);
            	snapshotPacket.entityDatas.add(entityData);
        	}
        	server.sendToAllUDP(snapshotPacket);
        }
        
        // Hero Position
        if (Rumble.getRumbleTimeLeft() > 0){
            Rumble.tick(Gdx.graphics.getDeltaTime());
            camera.translate(Rumble.getPos());
        } else {
            camera.position.lerp(mainHero.pos, .2f);
        }
        
        // Tick all entities
        for(GameEntity e: island.entities){
            e.tick(Gdx.graphics.getDeltaTime());
            e.currentTile = island.chunk.getTile(e.body.getPosition());
            e.tick(Gdx.graphics.getDeltaTime(), island.chunk);
        }
        
        camera.update();
        
        island.entities.sort();
                
        // GAME DRAW
        batch.setProjectionMatrix(camera.combined);
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        
        batch.begin();
        // Draw all tiles in the chunk / chunk rows
        for(Array<Tile> row : island.chunk.tiles){
            for(Tile tile : row){
                batch.draw(tile.texture, tile.pos.x, tile.pos.y, tile.size, tile.size);                
                if (tile.secondaryTexture != null) batch.draw(tile.secondaryTexture, tile.pos.x, tile.pos.y, tile.size, tile.size);
            }
        }
        
        // Draw all entities
        for(GameEntity e: island.entities){
            e.draw(batch);
        }
        
        batch.end();
        
        // GUI
        batch.setProjectionMatrix(screenMatrix);
        
        batch.begin(); 
        squareMenu.draw(batch);
        batch.end();
        
        box2D.tick(camera, input);
        if(isClientSide()) {
        	island.requestRemoveEntities(box2D, client);
        }
        island.clearRemovedEntities(box2D);
        
        /*time += Gdx.graphics.getDeltaTime();
        fpsFrames++;
        if(time > 3){
        	System.out.println("RAW FPS: " + (Math.round(1/Gdx.graphics.getRawDeltaTime())));
            System.out.println("GDX FPS: " + Gdx.graphics.getFramesPerSecond());    
            System.out.println("FPS: " + (fpsFrames / 3));
            fpsFrames = 0;
            time = 0;
        }*/
        
        input.setProcessedClick(true);
    }
	
    private void resetGameState() {     
        island.reset(box2D);
        
        for(Hero hero : heroes.values()) {
        	hero.reset(box2D,island.getCentrePosition());
        	island.entities.add(hero);
        }
        
        for(int i = 0; i < MathUtils.random(20); i++){
            island.entities.add(new Bird(new Vector3(MathUtils.random(100),MathUtils.random(100),0), box2D, Enums.EnityState.FLYING));
        }
       
        //box2D.populateEntityMap(island.entities);
        //getInput().setReset(false);
    }

    @Override
    public void dispose () {
        batch.dispose();
    }
}
