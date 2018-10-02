package com.jharter.game.ecs.components;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.ObjectMap;
import com.jharter.game.ecs.components.Components.SpriteComp;
import com.jharter.game.ecs.components.Components.VitalsComp;
import com.jharter.game.ecs.components.Components.ZoneComp;
import com.jharter.game.ecs.components.Components.ZonePositionComp;
import com.jharter.game.ecs.entities.EntityBuilder;
import com.jharter.game.util.id.ID;

/**
 * Class that links components, entities, and ids together. It is a sister class of C, the component mapper.
 */
public class CompUtilManager {
	
	private CompManager Comp;
	private ObjectMap<Class<?>, CompUtil<?>> utilByClass = new ObjectMap<Class<?>, CompUtil<?>>();
	
	CompUtilManager(CompManager Comp) {
		this.Comp = Comp;
	}
	
	public <W extends CompUtil<C>, C extends Component> W get(Class<W> wrapperClass, C comp) {
		if(!utilByClass.containsKey(comp.getClass())) {
			try {
				Constructor c = wrapperClass.getDeclaredConstructors()[0];
				c.setAccessible(true);
				W util = (W) c.newInstance();
				util.setHandler(Comp.getEntityHandler());
				utilByClass.put(comp.getClass(), util);
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
		W wrapper = (W) utilByClass.get(comp.getClass());
		wrapper.setComponent(comp);
		return wrapper;
	}
	
	public <W extends CompUtil<C>, C extends Component> W get(Class<W> wrapperClass, Class<C> compClass, Entity entity) {
		return get(wrapperClass, Comp.getFor(compClass).get(entity));
	}
	
}
