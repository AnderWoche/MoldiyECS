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

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.badlogic.gdx.utils.Pool;

import de.moldiy.moldiyecs.utils.Bag;
import de.moldiy.moldiyecs.utils.IntBag;
import de.moldiy.moldiyecs.utils.reflect.ClassReflection;
import de.moldiy.moldiyecs.utils.reflect.ReflectionException;

public class ComponentMapper<T extends Component> implements ComponentMapperGetOnly<T> {

	private Class<T> componentClass;

	private final Bag<T> components;
	private final Pool<T> componentPool;

	private final IntBag entitiesRemoved = new IntBag();
	private final IntBag entitiesAdded = new IntBag();

	private boolean isSynchronized = false;
	private Thread exclusiceAccess = null;
	private final Lock lock = new ReentrantLock();
	private final Condition condition = lock.newCondition();

	private final Bag<ComponentListener> componentListener = new Bag<ComponentMapper.ComponentListener>(
			ComponentListener.class);

	public ComponentMapper(final Class<T> componentClass) {
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
	public synchronized void exclusiceAccess() {
		this.lock.lock();
		if (this.exclusiceAccess == null) {
			this.exclusiceAccess = Thread.currentThread();
		} else if (this.exclusiceAccess != Thread.currentThread()) {
			try {
				this.condition.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			this.exclusiceAccess = Thread.currentThread();
		}
		this.lock.unlock();
	}

	public synchronized void publicAccess() {
		this.lock.lock();
		this.exclusiceAccess = null;
		this.condition.signal();
		this.lock.unlock();
	}

	@Override
	public T get(int entity) {
		return this.components.safeGet(entity);
	}

	public void callListener() {
//		if (this.isSynchronized())
//			exclusiceAccess();
////		if (this.exclusiceAccess != null && this.exclusiceAccess != Thread.currentThread()) {
////			throw new RuntimeErrorException(null, "Pls report this exeption!!");
////		}
//		int[] dataRemoved = this.entitiesRemoved.getData();
//		for (int i = 0, s = this.entitiesRemoved.size(); i < s; i++) {
//			this.notifyComponentListener_EntityDeleted(dataRemoved[i]);
//		}
//		int[] dataAdded = this.entitiesAdded.getData();
//		for (int i = 0, s = this.entitiesAdded.size(); i < s; i++) {
//			this.notifyComponentListener_EntityAdded(dataAdded[i]);
//		}
//		this.entitiesRemoved.clear();
//		this.entitiesAdded.clear();
	}

	/**
	 * 
	 * @param entityID
	 */
	public void remove(int entityID) {
		if (this.isSynchronized())
			exclusiceAccess();
//		if (this.exclusiceAccess != null && this.exclusiceAccess != Thread.currentThread()) {
//			throw new RuntimeErrorException(null, "Pls report this exeption!!");
//		}
		T component = this.get(entityID);
		if (component != null) {
//			this.entitiesRemoved.add(entityID);
			this.notifyComponentListener_EntityDeleted(entityID);
			this.components.unsafeSet(entityID, null);
			this.componentPool.free(component);
		}

	}

	public T create(int entityID) {
		if (this.isSynchronized())
			exclusiceAccess();
//		if (this.exclusiceAccess != null && this.exclusiceAccess != Thread.currentThread()) {
//			throw new RuntimeErrorException(null, "Pls report this exeption!!");
//		}
		T component = this.get(entityID);
		if (component == null) {
			component = this.componentPool.obtain();
			this.components.set(entityID, component);
//			this.entitiesAdded.add(entityID);
			this.notifyComponentListener_EntityAdded(entityID);
		}
		return component;
	}

	public T createComponentOnly() {
		if (this.isSynchronized())
			exclusiceAccess();
//		if (this.exclusiceAccess != null && this.exclusiceAccess != Thread.currentThread()) {
//			throw new RuntimeErrorException(null, "Pls report this exeption!!");
//		}
		return this.componentPool.obtain();
	}

	public void add(int entityID, T component) {
		if (this.isSynchronized())
			exclusiceAccess();
//		if (this.exclusiceAccess != null && this.exclusiceAccess != Thread.currentThread()) {
//			throw new RuntimeErrorException(null, "Pls report this exeption!!");
//		}
		this.components.set(entityID, component);
		this.notifyComponentListener_EntityAdded(entityID);
	}

	public void setSynchronized(boolean syncronized) {
		this.isSynchronized = syncronized;
	}

	public boolean isSynchronized() {
		return this.isSynchronized;
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
