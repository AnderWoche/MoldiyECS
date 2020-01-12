package de.moldiy.moldiyecs.subscription;

import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import de.moldiy.moldiyecs.World;
import de.moldiy.moldiyecs.utils.Bag;
import de.moldiy.moldiyecs.utils.BitVector;

public class SubscriptionManager {

	private final World world;
	
	
	private final HashMap<Aspect.Builder, EntitySubscription> activeSubscriptions = new HashMap<Aspect.Builder, EntitySubscription>();

	private final Bag<EntitySubscription> entitySubscriptionsIterationBag = new Bag<EntitySubscription>(EntitySubscription.class);

	private Lock lock = new ReentrantLock();

	public SubscriptionManager(World world) {
		this.world = world;
	}

	public void entityComponentsChanged(int entity, BitVector compoentIDs) {
		EntitySubscription[] subscriptions = this.entitySubscriptionsIterationBag.getData();
		for (int i = 0, s = this.entitySubscriptionsIterationBag.size(); i < s; i++) {
			subscriptions[i].entityComponentsChanged(entity, compoentIDs);
		}
	}

	public EntitySubscription getSubscription(Aspect.Builder aspectBuilder) {
		EntitySubscription subscription = this.activeSubscriptions.get(aspectBuilder);
		return (subscription != null) ? subscription : this.createSubscription(aspectBuilder);
	}

	private EntitySubscription createSubscription(Aspect.Builder aspectBuilder) {
		this.lock.lock();
		EntitySubscription entitySubscription = new EntitySubscription(
				aspectBuilder.build(world.getComponentManager().getComponentIDFactory()), world.getEntityManager());

		this.activeSubscriptions.put(aspectBuilder, entitySubscription);
		this.entitySubscriptionsIterationBag.add(entitySubscription);
		this.lock.unlock();
		return entitySubscription;
	}

}
