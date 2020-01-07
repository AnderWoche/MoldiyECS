package de.moldiy.moldiyecs.subscription;

import java.util.HashMap;

import de.moldiy.moldiyecs.World;
import de.moldiy.moldiyecs.utils.Bag;

public class SubscriptionManager {

	private final World world;

	private final HashMap<Aspect.Builder, EntitySubscription> activeSubscriptions = new HashMap<Aspect.Builder, EntitySubscription>();

	private final Bag<EntitySubscription> entitySubscriptionsIterationBag = new Bag<EntitySubscription>();
	
	public SubscriptionManager(World world) {
		this.world = world;
	}

	public EntitySubscription getSubscription(Aspect.Builder aspectBuilder) {
		EntitySubscription subscription = this.activeSubscriptions.get(aspectBuilder);
		return (subscription != null) ? subscription : this.createSubscription(aspectBuilder);
	}

	private synchronized EntitySubscription createSubscription(Aspect.Builder aspectBuilder) {
		EntitySubscription entitySubscription = new EntitySubscription(
				aspectBuilder.build(world.getComponentManager().getComponentIDFactory()));
		
		this.activeSubscriptions.put(aspectBuilder, entitySubscription);
		this.entitySubscriptionsIterationBag.add(entitySubscription);
		return entitySubscription;
	}

}
