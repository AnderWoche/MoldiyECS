package de.moldiy.moldiyecs;

import de.moldiy.moldiyecs.componentManager.ComponentManager;

public class World {
	
	private EntityManager entityManager;
	
	private ComponentManager componentManager;
	
	
	public World() {
	}
	
	public void addSystem() {
		
	}
	
	public ComponentManager getComponentManager() {
		return this.componentManager;
	}
	
	public EntityManager getEntityManager() {
		return this.entityManager;
	}

}
