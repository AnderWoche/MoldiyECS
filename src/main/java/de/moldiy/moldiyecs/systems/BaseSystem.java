package de.moldiy.moldiyecs.systems;

import de.moldiy.moldiyecs.World;

public abstract class BaseSystem {
	
	/**
	 * Gettet init in SystemManager with the SystemInitalizer class
	 * it's happens with reflection
	 */
	private World world;
	
	public BaseSystem() {
	}
	
	public World getWorld() {
		return world;
	}
	
	protected void initialize() {};
	
	public abstract void processSystem();
	
	public void dispose() {};
	
	
}
