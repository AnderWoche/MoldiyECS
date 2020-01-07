package de.moldiy.moldiyecs.componentmanager;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.badlogic.gdx.utils.Pool;

import de.moldiy.moldiyecs.utils.Bag;
import de.moldiy.moldiyecs.utils.reflect.ClassReflection;
import de.moldiy.moldiyecs.utils.reflect.ReflectionException;

public class ComponentMapper<T extends Component> {

	private final Bag<T> components;
	private final Pool<T> componentPool;

	private boolean isLocked;
	private final Lock lock;
	
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
		this.lock = new ReentrantLock();

	}

	public T get(int entity) {
		return this.components.get(entity);
	}

	/**
	 * 
	 * @param entityID
	 */
	public void remove(int entityID) {
		if (isLocked) {
			this.lock.lock();
			this.notThreadSafeRemove(entityID);
			this.lock.unlock();
		} else {
			this.notThreadSafeRemove(entityID);
		}
	}

	private void notThreadSafeRemove(int entityID) {
		T component = this.get(entityID);
		if (component != null) {
			this.components.remove(entityID);
			this.componentPool.free(component);
		}

	}

	public T create(int entityID) {
		T component = this.get(entityID);
		if (component == null) {
			component = this.componentPool.obtain();
			this.components.set(entityID, component);
		}
		return component;
	}
	
	public interface ComponentListener {
		public void componentDeleteAfterThisMethod(int entity);
		public void componentIsAddedToComponentMapper(int entity);
	}

}
