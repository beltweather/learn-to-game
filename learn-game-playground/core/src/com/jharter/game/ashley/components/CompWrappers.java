package com.jharter.game.ashley.components;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.ObjectMap;
import com.jharter.game.ashley.components.Components.SpriteComp;
import com.jharter.game.ashley.components.Components.VitalsComp;
import com.jharter.game.ashley.components.Components.ZoneComp;
import com.jharter.game.ashley.components.Components.ZonePositionComp;
import com.jharter.game.util.id.ID;

/**
 * Class that links components, entities, and ids together. It is a sister class of C, the component mapper.
 */
public class CompWrappers {
	
	private ObjectMap<Class<?>, CompWrapper<?>> wrappersByClass = new ObjectMap<Class<?>, CompWrapper<?>>();
	private ObjectMap<Class<? extends Component>, Boolean> permissionsByClass = new ObjectMap<Class<? extends Component>, Boolean>();
	
	private CompManager Comp;
	
	CompWrappers(CompManager Comp) {
		this.Comp = Comp;
	}
	
	public <W extends CompWrapper<C>, C extends Component> W get(Class<W> wrapperClass, C comp) {
		if(!isAllowed(comp.getClass())) {
			return null;
		}
		if(!wrappersByClass.containsKey(comp.getClass())) {
			try {
				Constructor c = wrapperClass.getDeclaredConstructors()[0];
				c.setAccessible(true);
				wrappersByClass.put(comp.getClass(), (W) c.newInstance(Comp));
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		W wrapper = (W) wrappersByClass.get(comp.getClass());
		wrapper.setComponent(comp);
		return wrapper;
	}
	
	public <W extends CompWrapper<C>, C extends Component> W get(Class<W> wrapperClass, Class<C> compClass, Entity entity) {
		return get(wrapperClass, Comp.getFor(compClass).get(entity));
	}
	
	void clearPermissions() {
		permissionsByClass.clear();
	}
	
	void setPermissions(Class<? extends Component>... allowClasses) {
		for(Class<? extends Component> klass : allowClasses) {
			permissionsByClass.put(klass, true);
		}
	}
	
	boolean isAllowed(Class<? extends Component> klass) {
		if(permissionsByClass.size == 0) {
			return true;
		}
		if(!permissionsByClass.containsKey(klass)) {
			return false;
		}
		return permissionsByClass.get(klass);
	}
	
	private static class CompWrapper<T extends Component> {
		
		private T comp;
		protected CompManager Comp;
		
		private CompWrapper(CompManager Comp) {
			this.Comp = Comp;
		}
		
		void setComponent(T comp) {
			this.comp = comp;
		}
		
		protected T comp() {
			return comp;
		}
		
	}
	
	// ----------------------- BEGIN COMP WRAPPERS ----------------------------
	
	public static class CompWrapperSpriteComp extends CompWrapper<SpriteComp> {
		
		private CompWrapperSpriteComp(CompManager Comp) {
			super(Comp);
		}
		
		public float scaledWidth() {
			return scaledWidth(comp().scale.x);
		}
		
		public float scaledWidth(float scaleX) {
			if(scaleX == 1) {
				return comp().width;
			}
			return scaleX * comp().width;
		}
		
		public float scaledHeight() {
			return scaledHeight(comp().scale.y);
		}
		
		public float scaledHeight(float scaleY) {
			if(scaleY == 1) {
				return comp().height;
			}
			return scaleY * comp().height;
		}
	}
	
	public static class CompWrapperVitalsComp extends CompWrapper<VitalsComp> {
		
		private CompWrapperVitalsComp(CompManager Comp) {
			super(Comp);
		}
		
		public void heal(int hp) {
			comp().health += hp;
			if(comp().health > comp().maxHealth) {
				comp().health = comp().maxHealth;
			}
		}
		
		public void damage(int hp) {
			comp().health -= hp;
			if(comp().health < 0) {
				comp().health = 0;
			}
		}
		
		public boolean isDead() {
			return comp().health == 0;
		}
		
		public boolean isNearDeath() {
			return comp().health <= comp().weakHealth && !isDead();
		}
		
	}
	
	public static class CompWrapperZoneComp extends CompWrapper<ZoneComp> {
		
		private CompWrapperZoneComp(CompManager Comp) {
			super(Comp);
		}
		
		public boolean hasIndex(int index) {
			return index >= 0 && index < comp().internalObjectIDs.size;
		}
		
		public void add(EntityBuilder b) {
			add(b.IDComp().id, b.ZonePositionComp());
		}
		
		public void add(ID id, ZonePositionComp zp) {
			comp().internalObjectIDs.add(id);
			if(zp != null) {
				zp.index = comp().internalObjectIDs.size;
				zp.zoneID = comp().zoneID;
			}
		}
		
		public void remove(ID id) {
			comp().internalObjectIDs.removeValue(id, false);
			for(int i = 0; i < comp().internalObjectIDs.size; i++) {
				ID oID = comp().internalObjectIDs.get(i);
				Entity obj = Comp.Entity.get(oID);
				ZonePositionComp zp = Comp.ZonePositionComp.get(obj);
				zp.index = i;
			}
		}
		
	}
	
}
