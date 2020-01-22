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

import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import de.moldiy.moldiyecs.World;
import de.moldiy.moldiyecs.componentmanager.ComponentMapper.ComponentListener;
import de.moldiy.moldiyecs.systems.SystemGroup;
import de.moldiy.moldiyecs.utils.Bag;

public class ComponentManager {
	
	/**
	 * 
	 * Ein eigendes System CompoenntManager extends Base System oder so
	 * 
	 * weil die mapper regelmäsgig geupdated werden mussen 
	 * 
	 * hrgentiwe muss das passiren
	 * 
	 * 
	 */
	

	private final Bag<ComponentMapper<? extends Component>> mappers = new Bag<ComponentMapper<? extends Component>>();
	private final HashMap<Class<? extends Component>, Bag<SystemGroup>> mapperInSystemGroups = new HashMap<>();

	private final ComponentIDFactory componentIDFactory = new ComponentIDFactory();

	private final Lock lock;
	
	private final World world;

	private Bag<EntityChangedComponentIDsListener> listener = new Bag<ComponentManager.EntityChangedComponentIDsListener>(
			EntityChangedComponentIDsListener.class);

	private ComponentListener componentListener;

	public ComponentManager(World world) {
		this.world = world;
		lock = new ReentrantLock();
		componentListener = new ComponentListener() {
			@Override
			public void componentAdded(Class<? extends Component> component, int entity) {
				notifyEntityChangedComponentIDsListener_ADDED(component, entity);
			}

			@Override
			public void componentDeleteted(Class<? extends Component> component, int entity) {
				notifyEntityChangedComponentIDsListener_REMOVED(component, entity);
			}
		};
	}
	
	/**
	 * get a ComponentMapper from MainThread/MainSystemGroup
	 * @param c the Component Class for the mapper
	 * @return The Component Mapper
	 */
	public <T extends Component> ComponentMapper<T> getMapper(Class<T> c) {
		this.registerGroupOnAMapper(c, this.world.getSystemManager().getMainSystemGroup());
//		this.checkForMapperSynchronizion(c);
		return this.getOrCreateMapper(c);
	}

	public <T extends Component> ComponentMapper<T> getMapper(Class<T> c, SystemGroup group) {
		this.registerGroupOnAMapper(c, group);
//		this.checkForMapperSynchronizion(c);
		return this.getOrCreateMapper(c);
	}

	@SuppressWarnings("unchecked")
	private <T extends Component> ComponentMapper<T> getOrCreateMapper(Class<T> c) {
		int componentID = this.componentIDFactory.getComponentIDFor(c);
		ComponentMapper<?> mapper = this.mappers.get(componentID);
		if (mapper == null) {
			mapper = this.createMapper(c, componentID);
			mapper.addComponentListener(this.componentListener);
		}
		return (ComponentMapper<T>) mapper;
	}

	private <T extends Component> void registerGroupOnAMapper(Class<T> c, SystemGroup systemGroup) {
		Bag<SystemGroup> groups = this.mapperInSystemGroups.get(c);
		if (groups == null) {
			groups = new Bag<>(SystemGroup.class);
			this.mapperInSystemGroups.put(c, groups);
		}
		if (!groups.contains(systemGroup)) {
			groups.add(systemGroup);
			this.checkForMapperSynchronizion(c); // wird nur ausgeführt wenn ein system hinzugefügt wird
		}

	}

	private <T extends Component> void checkForMapperSynchronizion(Class<T> c) {
		Bag<SystemGroup> bagGroup = this.mapperInSystemGroups.get(c);
		if (bagGroup.size() > 1) {
			ComponentMapper<T> mapper = this.getOrCreateMapper(c);
			if (mapper.isSynchronized() == false) {
				mapper.setSynchronized(true);
				System.out.println("The Mapper for " + c.getSimpleName() + "is Syncornized now!");
			}
		}
	}

	private <T extends Component> ComponentMapper<T> createMapper(Class<T> c, int componentID) {
		lock.lock();
		ComponentMapper<T> mapper = new ComponentMapper<T>(c);

		this.mappers.set(componentID, mapper);
		lock.unlock();
		return mapper;
	}

	public ComponentIDFactory getComponentIDFactory() {
		return this.componentIDFactory;
	}

	public void addEntityChangedCompoenntIDsListener(EntityChangedComponentIDsListener changedComponentIDsListener) {
		this.listener.add(changedComponentIDsListener);
	}

	protected void notifyEntityChangedComponentIDsListener_ADDED(Class<? extends Component> component, int entity) {
		EntityChangedComponentIDsListener[] changedComponentIDs = this.listener.getData();
		for (int i = 0, s = this.listener.size(); i < s; i++) {
			changedComponentIDs[i].entityComponentIDAdded(component, entity);
		}
	}

	protected void notifyEntityChangedComponentIDsListener_REMOVED(Class<? extends Component> component, int entity) {
		EntityChangedComponentIDsListener[] changedComponentIDs = this.listener.getData();
		for (int i = 0, s = this.listener.size(); i < s; i++) {
			changedComponentIDs[i].entityComponentIDRemoved(component, entity);
		}
	}

	public interface EntityChangedComponentIDsListener {
		public void entityComponentIDRemoved(Class<? extends Component> component, int entity);

		public void entityComponentIDAdded(Class<? extends Component> component, int entity);
	}

}
