/**
 * Copyright 2020 Moldiy

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
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

	private final Bag<EntitySubscription> entitySubscriptionsIterationBag = new Bag<EntitySubscription>(
			EntitySubscription.class);

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

	//!!!!!!!!!! Wen eine neue sub erstellt wird mussen ale schon forgandenn entity überpruft wrrden
	private EntitySubscription createSubscription(Aspect.Builder aspectBuilder) {
		this.lock.lock();
		EntitySubscription entitySubscription = new EntitySubscription(
				aspectBuilder.build(world.getComponentManager().getComponentIDFactory()));
		this.world.getEntityManager().registerEntityStore(entitySubscription.getEntitiesAsBitVector());

		this.activeSubscriptions.put(aspectBuilder, entitySubscription);
		this.entitySubscriptionsIterationBag.add(entitySubscription);
		this.lock.unlock();
		return entitySubscription;
	}

}
