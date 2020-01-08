package de.moldiy.moldiyecs.subscription;

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
	
	public EntitySubscription(Aspect aspect, EntityManager entityManager) {
		this.aspect = aspect;
		entityManager.registerEntityStore(entities);
	}
	
	public void entityComponentsChanged(int entity, BitVector compoentIDs) {
		lock.lock();
		if(aspect.isInterested(compoentIDs)) {
			entities.unsafeSet(entity);
		} else {
			entities.unsafeClear(entity);
		}
		lock.unlock();
	}
	
	public IntBag updateEntityBag() {
		lock.lock();
		this.entities.toIntBag(this.entitiesForIteration);
		lock.unlock();
		return this.entitiesForIteration;
	}
	
	public IntBag getEntites() {
		if(entities.isEmpty()) {
			this.entities.toIntBag(this.entitiesForIteration);
		}
		return entitiesForIteration;
	}
	
	
}
