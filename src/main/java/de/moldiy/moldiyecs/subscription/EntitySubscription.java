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

	public IntBag updateEntityBag() {
		this.entities.toIntBag(this.entitiesForIteration);
		return this.getEntites();
	}

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
