package de.moldiy.moldiyecs.componentManager;

import com.badlogic.gdx.utils.Pool;

import de.moldiy.moldiyecs.utils.Bag;
import de.moldiy.moldiyecs.utils.reflect.ClassReflection;
import de.moldiy.moldiyecs.utils.reflect.ReflectionException;

public class ComponentMapper<T extends Component> {

	private final Bag<T> components;
	private Pool<T> componentPool;
	
	public ComponentMapper(final Class<T> componentClass) {
		components = new Bag<T>();
		this.componentPool = new Pool<T>() {
			@Override
			protected T newObject() {
				try {
					return ClassReflection.newInstance(componentClass);
				} catch (ReflectionException e) {
					e.printStackTrace();
				}
				return null;
			}
		};
	}
	
	public T get(int entityID) {
		return this.components.get(entityID);
	}
	
	public void remove(int entityID) {
		T component = this.get(entityID);
		if(component != null) {
			this.components.remove(entityID);
			this.componentPool.free(component);
		}
	}
	
	public T create(int entityID) {
		T component = this.get(entityID);
		if(component == null) {
			component = this.componentPool.obtain();
			this.components.set(entityID, component);
		}
		return component;
	}
	
	
	
}
