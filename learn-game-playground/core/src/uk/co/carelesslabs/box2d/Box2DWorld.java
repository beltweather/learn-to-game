package uk.co.carelesslabs.box2d;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.jharter.game.control.GameInput;
import com.jharter.game.ecs.components.Components.CollisionComp;
import com.jharter.game.ecs.entities.EntityHandler;
import com.jharter.game.stages.GameStage;
import com.jharter.game.util.id.ID;

public class Box2DWorld extends EntityHandler {
	
    public World world;
    private Box2DDebugRenderer debugRenderer;

    public Box2DWorld(GameStage game) {
    	super(game);
    	
        world = new World(new Vector2(.0f, .0f), true);
        debugRenderer = new Box2DDebugRenderer();

        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                Fixture fixtureA = contact.getFixtureA();
                Fixture fixtureB = contact.getFixtureB();

                processCollisions(fixtureA, fixtureB, true);
            }

            @Override
            public void endContact(Contact contact) {
                Fixture fixtureA = contact.getFixtureA();
                Fixture fixtureB = contact.getFixtureB();

                processCollisions(fixtureA, fixtureB, false);
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {
            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {
            }

        });
    }
    
    public void tick(OrthographicCamera camera, GameInput control) {
        if (control != null && control.isDebug())
            debugRenderer.render(world, camera.combined);
        world.step(Gdx.app.getGraphics().getDeltaTime(), 6, 2);
    }

    public void clearAllBodies() {
        Array<Body> bodies = new Array<Body>();
        world.getBodies(bodies);
        for (Body b : bodies) {
            world.destroyBody(b);
        }
    }
    
    private ID getEntityId(Fixture fixture) {
    	return (ID) fixture.getBody().getUserData();
    }
    
    private void processCollisions(Fixture aFixture, Fixture bFixture, boolean begin) {
    	ID aId = getEntityId(aFixture);
        ID bId = getEntityId(bFixture);
        
        if(aId == null || bId == null) {
        	return;
        }
        
        Entity entityA = Comp.Entity.get(aId);
        Entity entityB = Comp.Entity.get(bId);
        
        if (entityA != null && entityB != null) {
            if (aFixture.isSensor() && !bFixture.isSensor()) {
            	CollisionComp collisionCompB = Comp.CollisionComp.get(entityB);
            	if(collisionCompB != null) {
            		collisionCompB.collisionWithId = aId;
            		collisionCompB.begin = begin;
            	}
            } else if (bFixture.isSensor() && !aFixture.isSensor()) {
            	CollisionComp collisionCompA = Comp.CollisionComp.get(entityA);
            	if(collisionCompA != null) {
            		collisionCompA.collisionWithId = bId;
            		collisionCompA.begin = begin;
            	}
            }
        }
    }

    public void dispose() {
    	world.dispose();
    	debugRenderer.dispose();
    }
}