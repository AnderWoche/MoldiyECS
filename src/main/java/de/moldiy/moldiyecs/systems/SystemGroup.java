package de.moldiy.moldiyecs.systems;

import de.moldiy.moldiyecs.utils.Bag;

public class SystemGroup {
	
	private final Bag<BaseSystem> systems = new Bag<>(BaseSystem.class);
	
	/**
	 * don't remove Systems! Read only
	 * @return
	 */
	public Bag<BaseSystem> getSystems() {
		return this.systems;
	}
	
	/**
	 * Package Method so The SystemManager can init and add The System.
	 * @param system
	 */
	<T extends BaseSystem> void addSystem(T system) {
		this.systems.add(system);
	}
	
	public void initialize() {
		BaseSystem[] baseSystems = getSystems().getData();
		for(int i = 0, s = getSystems().size(); i < s; i++) {
			BaseSystem system = baseSystems[i];
			system.initialize();
		}	
	}
	
	public void process() {
		BaseSystem[] baseSystems = systems.getData();
		for(int i = 0, s = systems.size(); i < s; i++) {
			BaseSystem system = baseSystems[i];
			system.processSystem();
		}
	}
	
}
