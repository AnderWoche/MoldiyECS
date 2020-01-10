package de.moldiy.moldiyecs.systems;

import de.moldiy.moldiyecs.componentmanager.Component;
import de.moldiy.moldiyecs.componentmanager.ComponentMapper;
import de.moldiy.moldiyecs.subscription.EntitySubscription;
import de.moldiy.moldiyecs.utils.Bag;
import de.moldiy.moldiyecs.utils.IntBag;

/**
 * This system manage Automaticly Critical code and Syncronized when need
 * - The Mapper getting automaticly syncronized if need
 * - The Entity Subscription getting Automatiocly sycronized if need.
 * 
 * when the frame work Automaticly detect That System have no dependencys to other subcriptions or Mapper
 * then it is automaticly not syncronized, so it's faster
 * @author Moldiy
 *
 */
public abstract class ManagedSystem extends BaseSystem {

	private EntitySubscription subscription;

	private Bag<ComponentMapper<? extends Component>> synchronizedMapper = new Bag<ComponentMapper<? extends Component>>();

	@Override
	public void processSystem() {
		IntBag entityIDs = subscription.updateEntityBagWithLock();
		int[] entities = entityIDs.getData();
		for (int i = 0, s = entityIDs.size(); i < s; i++) {
			for (int mapperID = 0, syncMapperSize = synchronizedMapper.size(); mapperID < syncMapperSize; mapperID++) {
				synchronizedMapper.get(mapperID).exclusiceAccess();
			}
			this.processEntity(entities[i]);
			for (int mapperID = 0, syncMapperSize = synchronizedMapper.size(); mapperID < syncMapperSize; mapperID++) {
				synchronizedMapper.get(mapperID).publicAccess();
			}
		}
	}

	public abstract void processEntity(int entity);

	public Bag<ComponentMapper<?>> getSynchronizedMapper() {
		return this.synchronizedMapper;
	}
	
	public void addSynchronizedMapper() {
		
	}
}
