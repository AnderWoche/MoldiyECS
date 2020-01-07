package de.moldiy.moldiyecs;

import de.moldiy.moldiyecs.componentmanager.ComponentManager;
import de.moldiy.moldiyecs.subscription.SubscriptionManager;
import de.moldiy.moldiyecs.systems.SystemThreadGroup;
import de.moldiy.moldiyecs.utils.Bag;

public class World {
	
	private final EntityManager entityManager;
	
	private final ComponentManager componentManager;
	
	private final SubscriptionManager subscriptionManager;
	
	
	public World() {
		this.entityManager = new EntityManager(128);
		this.componentManager = new ComponentManager();
		this.subscriptionManager = new SubscriptionManager(this);
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
