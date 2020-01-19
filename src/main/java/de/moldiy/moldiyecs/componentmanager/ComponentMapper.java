/**
 * Copyright 2020 Moldiy

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package de.moldiy.moldiyecs.componentmanager;

import com.badlogic.gdx.utils.Pool;

import de.moldiy.moldiyecs.utils.Bag;
import de.moldiy.moldiyecs.utils.reflect.ClassReflection;
import de.moldiy.moldiyecs.utils.reflect.ReflectionException;

public class ComponentMapper<T extends Component> implements ComponentMapperGetOnly<T> {

	private Class<T> componentClass;

	private final Bag<T> components;
	private final Pool<T> componentPool;

	public boolean locked = false;

//	private Thread exclusiceAccess = null;
//	private final Lock lock;
//	private final Condition condition;

	private final Bag<ComponentListener> componentListener = new Bag<ComponentMapper.ComponentListener>(
			ComponentListener.class);

	public ComponentMapper(final Class<T> componentClass) {
//		this.lock = new ReentrantLock();
//		this.condition = this.lock.newCondition();
		this.componentClass = componentClass;
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

	/**
	 * Don't forget after the exclusice operation unlock the public access.
	 */
//	public void exclusiceAccess() {
//		if (this.exclusiceAccess == null) {
//			this.exclusiceAccess = Thread.currentThread();
//		} else {
//			this.lock.lock();
//			try {
//				this.condition.await();
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//			this.lock.unlock();
//			this.exclusiceAccess = Thread.currentThread();
//		}
//	}
//
//	public void publicAccess() {
//		this.lock.lock();
//		this.exclusiceAccess = null;
//		this.condition.signal();
//		this.lock.unlock();
//	}

	@Override
	public T get(int entity) {
		return this.components.safeGet(entity);
	}

	/**
	 * GEHÖER NCIHT ZU MEHTODE NUT IDEE NOTOIZ
	 * 
	 * wen erkannt wird das an diesem mapper mehr als 2 Systeme Interesiert sind
	 * wird automatisch sobal die methode REMOVE oder CREATE aufgerufen wird der
	 * mapper nur exclusive nur für diesen thread freigegeben ohne das der benutzer
	 * dieses Frame works was merkt. damit aber am ende der mapper wieder für alle
	 * freigeschaltet wrid wird immer IMMER am system ende automatisch the
	 * PUBLICACCESS methode aufgerufen soweit ich weis goibt es keine exeption wenn
	 * man notifi aufruft obwohl niemand wartet.
	 * 
	 * @param entityID
	 */
	public void remove(int entityID) {
		T component = this.get(entityID);
		if (component != null) {
			if (this.locked) {
				synchronized (this) {
					this.notifyComponentListener_EntityDeleted(entityID);
					this.components.unsafeSet(entityID, null);
					this.componentPool.free(component);
				}
			} else {
				this.notifyComponentListener_EntityDeleted(entityID);
				this.components.unsafeSet(entityID, null);
				this.componentPool.free(component);
			}
		}

	}

	public T create(int entityID) {
		T component = this.get(entityID);
		if (component == null) {
			if (this.locked) {
				synchronized (this) {
					component = this.componentPool.obtain();
					this.components.set(entityID, component);
					this.notifyComponentListener_EntityAdded(entityID);
				}
			} else {
				component = this.componentPool.obtain();
				this.components.set(entityID, component);
				this.notifyComponentListener_EntityAdded(entityID);
			}
		}
		return component;
	}

	public T createComponentOnly() {
		if (this.locked) {
			synchronized (this) {
				return this.componentPool.obtain();
			}
		} else {
			return this.componentPool.obtain();
		}
	}

	public void add(int entityID, T component) {
		if (this.locked) {
			synchronized (this) {
				this.components.set(entityID, component);
				this.notifyComponentListener_EntityAdded(entityID);
			}
		} else {
			this.components.set(entityID, component);
			this.notifyComponentListener_EntityAdded(entityID);
		}
	}

	public void addComponentListener(ComponentListener componentListener) {
		this.componentListener.add(componentListener);
	}

	protected void notifyComponentListener_EntityDeleted(int entity) {
		ComponentListener[] listneners = this.componentListener.getData();
		for (int i = 0, s = this.componentListener.size(); i < s; i++) {
			listneners[i].componentDeleteted(this.componentClass, entity);
		}
	}

	protected void notifyComponentListener_EntityAdded(int entity) {
		ComponentListener[] listneners = this.componentListener.getData();
		for (int i = 0, s = this.componentListener.size(); i < s; i++) {
			listneners[i].componentAdded(this.componentClass, entity);
		}
	}
	
	public interface ComponentListener {
		public void componentDeleteted(Class<? extends Component> component, int entity);

		public void componentAdded(Class<? extends Component> component, int entity);
	}

}
