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
package de.moldiy.moldiyecs.systems;

import de.moldiy.moldiyecs.subscription.EntitySubscription;
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

	@Override
	public void processSystem() {
		IntBag entityIDs = subscription.updateEntityBagWithLock();
		int[] entities = entityIDs.getData();
		for (int i = 0, s = entityIDs.size(); i < s; i++) {
//			for (int mapperID = 0, syncMapperSize = synchronizedMapper.size(); mapperID < syncMapperSize; mapperID++) {
//				synchronizedMapper.get(mapperID).exclusiceAccess();
//			}
			this.processEntity(entities[i]);
//			for (int mapperID = 0, syncMapperSize = synchronizedMapper.size(); mapperID < syncMapperSize; mapperID++) {
//				synchronizedMapper.get(mapperID).publicAccess();
//			}
		}
	}

	public abstract void processEntity(int entity);
}
