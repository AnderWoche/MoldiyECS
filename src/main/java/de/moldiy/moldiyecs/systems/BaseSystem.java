package de.moldiy.moldiyecs.systems;

import de.moldiy.moldiyecs.World;

public abstract class BaseSystem {
	
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
