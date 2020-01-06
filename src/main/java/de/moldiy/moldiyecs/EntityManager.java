package de.moldiy.moldiyecs;

import de.moldiy.moldiyecs.utils.Bag;
import de.moldiy.moldiyecs.utils.BitVector;
import de.moldiy.moldiyecs.utils.IntBag;
import de.moldiy.moldiyecs.utils.IntDeque;

public class EntityManager {

	final Bag<Entity> entities;
	private final BitVector recycled = new BitVector();
	private final IntDeque limbo = new IntDeque();
	private int nextId;
	private Bag<BitVector> entityBitVectorsStores = new Bag<BitVector>(BitVector.class);
	
	public EntityManager(int initialContainerSize) {
		entities = new Bag<Entity>(initialContainerSize);
		this.registerEntityStore(recycled);
	}
	
	public void registerEntityStore(BitVector bv) {
		bv.ensureCapacity(entities.getCapacity());
		entityBitVectorsStores.add(bv);
	}
	
	protected Entity createEntityInstance() {
		return obtain();
	}
	
	protected int create() {
		return obtain().getID();
	}
	
	public boolean isActive(int entityId) {
		return !recycled.unsafeGet(entityId);
	}
	
	public void deleteAndFreeEntitys(IntBag pendingDeletion) {
		int[] ids = pendingDeletion.getData();
		for(int i = 0, s = pendingDeletion.size(); s > i; i++) {
			int id = ids[i];
			// usually never happens but:
			// this happens when an entity is deleted before
			// it is added to the world, ie; created and deleted
			// before World#process has been called
			if (!recycled.unsafeGet(id)) {
				free(id);
			}
		}
	}
	
	public void reset() {
//		int count = world.getAspectSubscriptionManager()
//			.get(all())
//			.getActiveEntityIds()
//			.cardinality();
//
//		if (count > 0)
//			return false;

		limbo.clear();
		recycled.clear();
		entities.clear();

		nextId = 0;

	}
	
	private Entity createEntity(int id) {
		Entity e = new Entity(id);
		if (e.getID() >= entities.getCapacity()) {
			growEntityStores();
		}

		// can't use unsafe set, as we need to track highest id
		// for faster iteration when syncing up new subscriptions
		// in ComponentManager#synchronize
		entities.set(e.getID(), e);

		return e;
	}
	
	public Entity getEntity(int entityId) {
		return entities.get(entityId);
	}
	
	private void growEntityStores() {
		int newSize = 2 * entities.getCapacity();
		entities.ensureCapacity(newSize);
//		ComponentManager cm = world.getComponentManager();
//		cm.ensureCapacity(newSize);

		for (int i = 0, s = entityBitVectorsStores.size(); s > i; i++) {
			entityBitVectorsStores.get(i).ensureCapacity(newSize);
		}
	}
	
	private Entity obtain() {
		if (limbo.isEmpty()) {
			return createEntity(nextId++);
		} else {
			int id = limbo.popFirst();
			recycled.unsafeClear(id);
			return entities.get(id);
		}
	}
	
	private void free(int entityId) {
		limbo.add(entityId);
		recycled.unsafeSet(entityId);
	}
	
}
