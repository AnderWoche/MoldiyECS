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

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import de.moldiy.moldiyecs.EntityManager;
import de.moldiy.moldiyecs.utils.BitVector;
import de.moldiy.moldiyecs.utils.IntBag;

public class EntitySubscription {

	private final Aspect aspect;

	private final BitVector entities = new BitVector();
	private final IntBag entitiesForIteration = new IntBag();

	private final Lock lock = new ReentrantLock();
	private final Condition condition = lock.newCondition();
	private boolean needToLock;

	public EntitySubscription(Aspect aspect, EntityManager entityManager) {
		this.aspect = aspect;
		entityManager.registerEntityStore(entities);
	}

	public void entityComponentsChanged(int entity, BitVector compoentIDs) {
		if (needToLock) {
			this.lock.lock();
			try {
				this.condition.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			this.lock.unlock();
		}
		if (aspect.isInterested(compoentIDs)) {
			entities.unsafeSet(entity);
		} else {
			entities.unsafeClear(entity);
		}
	}

	/**
	 * check the entitys and update.(Remove/Add entities)
	 * 
	 * call this method only when you shure that the using Mapper of this
	 * Subscription is only in one Single thread.
	 * 
	 * @return IntBag this the entitys with the Subscrpipted Compoennts (Only read don't remove from intbag)
	 */
	public IntBag updateEntityBag() {
		this.entities.toIntBag(this.entitiesForIteration);
		return this.getEntites();
	}

	/**
	 * check the entitys and update.(Remove/Add entities)
	 * @return IntBag this the entitys with the Subscrpipted Compoennts (Only read don't remove from intbag)
	 */
	public IntBag updateEntityBagWithLock() {
		this.needToLock = true;
		this.entities.toIntBag(this.entitiesForIteration);
		this.needToLock = false;
		this.lock.lock();
		this.condition.signalAll();
		this.lock.unlock();
		return this.getEntites();
	}

	public IntBag getEntites() {
		return entitiesForIteration;
	}

}
