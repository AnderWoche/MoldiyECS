/**
 * Copyright 2011 GAMADU.COM. All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package de.moldiy.moldiyecs;

import de.moldiy.moldiyecs.utils.Bag;
import de.moldiy.moldiyecs.utils.BitVector;
import de.moldiy.moldiyecs.utils.IntDeque;

public class EntityManager {
	
	/**
	 * The World for The Entity Instances.
	 */
	private World world;

	private final Bag<Entity> entities;
	private final Bag<BitVector> componentIDFromEntitys = new Bag<BitVector>();

	private final BitVector recycled = new BitVector();
	private final IntDeque limbo = new IntDeque();
	private int nextId;
	private Bag<BitVector> entityBitVectorsStores = new Bag<BitVector>(BitVector.class);

	public EntityManager(int initialContainerSize, World world) {
		this.world = world;
		entities = new Bag<Entity>(initialContainerSize);
		this.registerEntityStore(recycled);
	}

	public void registerEntityStore(BitVector bv) {
		bv.ensureCapacity(entities.getCapacity());
		entityBitVectorsStores.add(bv);
	}

	public Entity createEntityInstance() {
		return obtain();
	}

	public int create() {
		return obtain().getID();
	}

	public boolean isActive(int entityId) {
		return !recycled.unsafeGet(entityId);
	}

	public synchronized BitVector getComponentIDs(int entity) {
		return this.componentIDFromEntitys.get(entity);
	}

	public synchronized void deleteAndFreeEntitys(int entity) {
			// usually never happens but:
			// this happens when an entity is deleted before
			// it is added to the world, ie; created and deleted
			// before World#process has been called
			if (!recycled.unsafeGet(entity)) {
				free(entity);
				BitVector componentIDs =  this.componentIDFromEntitys.get(entity);
				componentIDs.clear();
				this.world.getSubscriptionManager().entityComponentsChanged(entity, componentIDs);
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
		Entity e = new Entity(id, this.world);
		if (e.getID() >= entities.getCapacity()) {
			growEntityStores();
		}

		// can't use unsafe set, as we need to track highest id
		// for faster iteration when syncing up new subscriptions
		// in ComponentManager#synchronize
		this.entities.set(e.getID(), e);
		this.componentIDFromEntitys.set(e.getID(), new BitVector());

		return e;
	}

	public Entity getEntity(int entityId) {
		return entities.get(entityId);
	}

	private void growEntityStores() {
		int newSize = 2 * entities.getCapacity();
		this.entities.ensureCapacity(newSize);

		for (int i = 0, s = this.entityBitVectorsStores.size(); s > i; i++) {
			this.entityBitVectorsStores.get(i).ensureCapacity(newSize);
		}
	}

	private synchronized Entity obtain() {
		if (limbo.isEmpty()) {
			return createEntity(nextId++);
		} else {
			int id = limbo.popFirst();
			recycled.unsafeClear(id);
			return entities.get(id);
		}
	}

	private synchronized void free(int entityId) {
		limbo.add(entityId);
		recycled.unsafeSet(entityId);
	}

}
