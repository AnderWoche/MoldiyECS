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
package de.moldiy.moldiyecs.systems;

import java.util.LinkedHashMap;

import de.moldiy.moldiyecs.World;
import de.moldiy.moldiyecs.utils.Bag;
import de.moldiy.moldiyecs.utils.reflect.ReflectionException;

public class SystemManager {

	private boolean isStarted = false;

	private final World world;

	private final LinkedHashMap<Class<? extends BaseSystem>, BaseSystem> registertBaseSystems = new LinkedHashMap<>();

	private final Bag<SystemGroup> systemGroups = new Bag<SystemGroup>(SystemGroup.class);
	private final SystemGroup mainThreadGroup = new SystemGroup();

	public SystemManager(World world) {
		this.world = world;
		this.systemGroups.add(mainThreadGroup);
	}

	/**
	 * init and start The Whole systemGroups and systems.
	 */
	public void initializeAndStart() {
		this.isStarted = true;
		SystemGroup[] baseSystems = this.systemGroups.getData();
		for (int i = 0, s = this.systemGroups.size(); i < s; i++) {
			baseSystems[i].initialize();
		}
	}

	/**
	 * If you shose null for a group then is going to add the system to the main
	 * loop or main thread.
	 * 
	 * @param <T>    class extends BaseSystem
	 * @param group  The group the system will be processed. null is the main loop
	 *               or main Thread.
	 * @param system The system that will be added
	 */
	public <T extends BaseSystem> void addSystem(T system, SystemGroup group) {
		if (this.isStarted) {
			throw new WorldAlreadyStartExeption("You can only add Systems when the World is not started");
		}
		if (!this.registertBaseSystems.containsKey(system.getClass())) {
			try {
				SystemInitalizer.initSystem(system, group, world);
				SystemInitalizer.initMapper(system, group, this.world.getComponentManager());
			} catch (ReflectionException e) {
				e.printStackTrace();
				return;
			}
			if (group == null) {
				throw new IllegalArgumentException(
						"Can't add the System: " + system.getClass() + ". The SystemGroup " + group + " don't exists!");
			} else {
				group.addSystem(system);
			}
			this.registertBaseSystems.put(system.getClass(), system);
		} else {
			throw new SystemAlreadyAddedExeption(
					"The System " + system.getClass() + " is already added! and can only addedonce."); // ?
		}
	}

	@SuppressWarnings("unchecked")
	public <T extends BaseSystem> T getSystem(Class<T> system) {
		BaseSystem baseSys = this.registertBaseSystems.get(system);
		return (T) baseSys;
	}

	public ThreadedSystemGroup createThreadedSystemGroup() {
		ThreadedSystemGroup systemThreadGroup = new ThreadedSystemGroup();
		this.systemGroups.add(systemThreadGroup);
		return systemThreadGroup;
	}

	public SystemGroup getMainSystemGroup() {
		return this.mainThreadGroup;
	}

	public void process() {
		SystemGroup[] baseSystems = this.systemGroups.getData();
		for (int i = 0, s = this.systemGroups.size(); i < s; i++) {
			baseSystems[i].process();
		}
	}

	public boolean isStated() {
		return this.isStarted;
	}

	public class SystemAlreadyAddedExeption extends RuntimeException {
		private static final long serialVersionUID = 360415350986643726L;

		public SystemAlreadyAddedExeption(String massage) {
			super(massage);
		}
	}

	public class WorldAlreadyStartExeption extends RuntimeException {
		private static final long serialVersionUID = 1356963955559879272L;

		public WorldAlreadyStartExeption(String massage) {
			super(massage);
		}
	}
}
