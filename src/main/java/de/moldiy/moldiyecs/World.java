package de.moldiy.moldiyecs;

import de.moldiy.moldiyecs.componentmanager.Component;
import de.moldiy.moldiyecs.componentmanager.ComponentManager;
import de.moldiy.moldiyecs.componentmanager.ComponentManager.EntityChangedComponentIDsListener;
import de.moldiy.moldiyecs.subscription.SubscriptionManager;
import de.moldiy.moldiyecs.systems.SystemManager;
import de.moldiy.moldiyecs.utils.BitVector;

public class World {

	private final EntityManager entityManager;

	private final ComponentManager componentManager;

	private final SubscriptionManager subscriptionManager;

	private SystemManager systemManager;

	public World() {
		this.entityManager = new EntityManager(128);
		this.componentManager = new ComponentManager();
		this.subscriptionManager = new SubscriptionManager(this);
		this.systemManager = new SystemManager(this);
		this.componentManager.addEntityChangedCompoenntIDsListener(new EntityChangedComponentIDsListener() {
			@Override
			public void entityComponentIDRemoved(Class<? extends Component> component, int entity) {
				BitVector componentIDs = entityManager.getComponentIDs(entity);
				componentIDs.unsafeClear(componentManager.getComponentIDFactory().getComponentIDFor(component));
				subscriptionManager.entityComponentsChanged(entity, componentIDs);
			}

			@Override
			public void entityComponentIDAdded(Class<? extends Component> component, int entity) {
				BitVector componentIDs = entityManager.getComponentIDs(entity);
				componentIDs.set(componentManager.getComponentIDFactory().getComponentIDFor(component));
				subscriptionManager.entityComponentsChanged(entity, componentIDs);
			}
		});

	}

	public void start() {
		this.systemManager.start();
	}

	public void process() {
		this.systemManager.process();
	}

	public SubscriptionManager getSubscriptionManager() {
		return this.subscriptionManager;
	}

	public SystemManager getSystemManager() {
		return this.systemManager;
	}

	public ComponentManager getComponentManager() {
		return this.componentManager;
	}

	public EntityManager getEntityManager() {
		return this.entityManager;
	}

}
