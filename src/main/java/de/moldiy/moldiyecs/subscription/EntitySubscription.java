package de.moldiy.moldiyecs.subscription;

import de.moldiy.moldiyecs.utils.BitVector;
import de.moldiy.moldiyecs.utils.IntBag;

public class EntitySubscription {
	
	private final Aspect aspect;
	
	private final BitVector entities = new BitVector();
	private final IntBag entitiesForIteration = new IntBag();
	
	public EntitySubscription(Aspect aspect) {
		this.aspect = aspect;
	}
	
	public void entityComponentsChanged(int entity, BitVector compoentIDs) {
		if(aspect.isInterested(compoentIDs)) {
			entities.unsafeSet(entity);
		} else {
			entities.unsafeClear(entity);
		}
	}
	
	public void updateEntityBag() {
		this.entities.toIntBag(this.entitiesForIteration);
	}
	
	
	
	
	
}
