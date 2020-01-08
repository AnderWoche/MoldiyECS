package de.moldiy.moldiyecs.systems;

import java.util.HashMap;

import de.moldiy.moldiyecs.World;
import de.moldiy.moldiyecs.utils.Bag;
import de.moldiy.moldiyecs.utils.reflect.ReflectionException;

public class SystemManager {

	private final World world;

	private final Bag<Class<? extends BaseSystem>> allSystems = new Bag<Class<? extends BaseSystem>>();
	private final Bag<BaseSystem> mainSystems = new Bag<BaseSystem>(BaseSystem.class);

	private final HashMap<String, SystemThreadGroup> systemGroup = new HashMap<String, SystemThreadGroup>();
	private final Bag<SystemThreadGroup> allSystemGroupsForIteration = new Bag<SystemThreadGroup>(
			SystemThreadGroup.class);

	public SystemManager(World world) {
		this.world = world;
	}

	public void start() {
		BaseSystem[] baseSystems = this.mainSystems.getData();
		for (int i = 0, s = this.mainSystems.size(); i < s; i++) {
			baseSystems[i].initialize();
		}
		SystemThreadGroup[] systemThreadGroups = this.allSystemGroupsForIteration.getData();
		for (int i = 0, s = this.allSystemGroupsForIteration.size(); i < s; i++) {
			systemThreadGroups[i].start();
		}
	}

	/**
	 * Add's System with the Group NULL. The System will be processesed at the main
	 * Loop.
	 * 
	 * @param <T> The System.
	 */
	public <T extends BaseSystem> void addSystem(T system) {
		this.addSystem(null, system);
	}

	public <T extends BaseSystem> void addSystem(String group, T system) {
//		if (!this.allSystems.contains(system.getClass())) { 
			try {
				SystemInitalizer.initSystem(system, world);
				System.out.println("system iniut!");
			} catch (ReflectionException e) {
				e.printStackTrace();
				return;
			}
			if (group == null) {
				this.mainSystems.add(system);
			} else {
				SystemThreadGroup systemThreadGroup = this.systemGroup.get(group);
				if (systemThreadGroup == null) {
					throw new IllegalArgumentException("Can't add the System: " + system.getClass()
							+ ". The SystemThreadGroup " + group + " don't exists!");
				} else {
					systemThreadGroup.addSystem(system);
				}
			}
			this.allSystems.add(system.getClass());
//		} else {
//			System.out.println("System ALready exist! (exeption?)");
////			new SystemAlreadyAddedExeption(); // ?
//		}
	}

	public SystemThreadGroup getSystemThreadGroup(String groupName) {
		return this.systemGroup.get(groupName);
	}

	public SystemThreadGroup createSystemThreadGroup(String groupName, int iterationPerSecond) {
		SystemThreadGroup systemThreadGroup = this.getSystemThreadGroup(groupName);
		if (systemThreadGroup == null) {
			systemThreadGroup = new SystemThreadGroup(iterationPerSecond, groupName);
			this.systemGroup.put(groupName, systemThreadGroup);
			this.allSystemGroupsForIteration.add(systemThreadGroup);
		}
		return systemThreadGroup;
	}

	public void process() {
		BaseSystem[] baseSystems = this.mainSystems.getData();
		for (int i = 0, s = this.mainSystems.size(); i < s; i++) {
			baseSystems[i].processSystem();
		}
	}

	public class SystemAlreadyAddedExeption extends Exception {
		private static final long serialVersionUID = 5530107672002274672L;
		
	}
}
