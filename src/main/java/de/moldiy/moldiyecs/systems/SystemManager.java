package de.moldiy.moldiyecs.systems;

import de.moldiy.moldiyecs.World;
import de.moldiy.moldiyecs.utils.Bag;
import de.moldiy.moldiyecs.utils.reflect.ReflectionException;

public class SystemManager {

	private final World world;

	private final Bag<Class<? extends BaseSystem>> registertBaseSystems = new Bag<>();
	private final Bag<SystemGroup> systemGroups = new Bag<SystemGroup>(SystemGroup.class);
	private final SystemGroup mainThreadSystem = new SystemGroup();

	public SystemManager(World world) {
		this.world = world;
	}

	public void start() {
		SystemGroup[] baseSystems = this.systemGroups.getData();
		for (int i = 0, s = this.systemGroups.size(); i < s; i++) {
			baseSystems[i].initialize();
		}
	}

	/**
	 * If you shose null for a group then is going to add the system to the main
	 * loop or main thread.
	 * 
	 * @param <T>
	 *            class extends BaseSystem
	 * @param group
	 *            The group the system will be processed. null is the main loop or
	 *            main Thread.
	 * @param system
	 *            The system that will be added
	 */
	public <T extends BaseSystem> void addSystem(T system, SystemGroup group) {
		if (!this.registertBaseSystems.contains(system.getClass())) {
			try {
				SystemInitalizer.initSystem(system, world);
				SystemInitalizer.initMapperInSystem(system, group, this.world.getComponentManager());
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
			this.registertBaseSystems.add(system.getClass());
		} else {
			throw new SystemAlreadyAddedExeption(
					"The System " + system.getClass() + " is already added! and can only addedonce."); // ?
		}
	}

	public ThreadedSystemGroup createSystemThreadGroup() {
		ThreadedSystemGroup systemThreadGroup = new ThreadedSystemGroup();
		this.systemGroups.add(systemThreadGroup);
		return systemThreadGroup;
	}

	public void process() {
		SystemGroup[] baseSystems = this.systemGroups.getData();
		for (int i = 0, s = this.systemGroups.size(); i < s; i++) {
			baseSystems[i].process();
		}
	}

	public class SystemAlreadyAddedExeption extends RuntimeException {
		private static final long serialVersionUID = 360415350986643726L;

		public SystemAlreadyAddedExeption(String massage) {
			super(massage);
		}
	}

	public SystemGroup getMainThreadSystemGroup() {
		return this.mainThreadSystem;
	}
}
