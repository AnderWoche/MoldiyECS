/**
 * Copyright 2011 GAMADU.COM. All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE
 */
package de.moldiy.moldiyecs;

import de.moldiy.moldiyecs.componentmanager.Component;
import de.moldiy.moldiyecs.componentmanager.ComponentManager;
import de.moldiy.moldiyecs.componentmanager.ComponentManager.EntityChangedComponentIDsListener;
import de.moldiy.moldiyecs.subscription.SubscriptionManager;
import de.moldiy.moldiyecs.systems.SystemManager;
import de.moldiy.moldiyecs.utils.BitVector;

public class World {

	private final EntityManager entityManager;

	private final ComponentManager componentManager;

	private final SubscriptionManager subscriptionManager;

	private SystemManager systemManager;

	public World() {
		this.entityManager = new EntityManager(128);
		this.componentManager = new ComponentManager();
		this.subscriptionManager = new SubscriptionManager(this);
		this.systemManager = new SystemManager(this);
		this.componentManager.addEntityChangedCompoenntIDsListener(new EntityChangedComponentIDsListener() {
			@Override
			public void entityComponentIDRemoved(Class<? extends Component> component, int entity) {
				BitVector componentIDs = entityManager.getComponentIDs(entity);
				componentIDs.unsafeClear(componentManager.getComponentIDFactory().getComponentIDFor(component));
				subscriptionManager.entityComponentsChanged(entity, componentIDs);
			}

			@Override
			public void entityComponentIDAdded(Class<? extends Component> component, int entity) {
				BitVector componentIDs = entityManager.getComponentIDs(entity);
				componentIDs.set(componentManager.getComponentIDFactory().getComponentIDFor(component));
				subscriptionManager.entityComponentsChanged(entity, componentIDs);
			}
		});

	}

	public void initializeAndStart() {
		this.systemManager.initializeAndStart();
	}

	public void process() {
		this.systemManager.process();
	}

	public SubscriptionManager getSubscriptionManager() {
		return this.subscriptionManager;
	}

	public SystemManager getSystemManager() {
		return this.systemManager;
	}

	public ComponentManager getComponentManager() {
		return this.componentManager;
	}

	public EntityManager getEntityManager() {
		return this.entityManager;
	}

}
