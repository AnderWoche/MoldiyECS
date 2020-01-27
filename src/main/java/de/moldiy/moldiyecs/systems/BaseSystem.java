/**
 * Copyright 2011 GAMADU.COM. All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE
 */
package de.moldiy.moldiyecs.systems;

import de.moldiy.moldiyecs.EntityEdit;
import de.moldiy.moldiyecs.World;
import de.moldiy.moldiyecs.componentmanager.Component;
import de.moldiy.moldiyecs.componentmanager.ComponentMapper;
import de.moldiy.moldiyecs.utils.Bag;

public abstract class BaseSystem {

	/**
	 * Gettet init in SystemManager with the SystemInitalizer class it's happens
	 * with reflection
	 */
	private World world;

	private SystemGroup group;

	private EntityEdit entityEdit;

	private Bag<ComponentMapper<?>> mappers = new Bag<ComponentMapper<?>>();

	private float deltaTime;

	public BaseSystem() {
	}

	/**
	 * package mthod for SystemGroup
	 */
	void setDeltaTime(float deltaTime) {
		this.deltaTime = deltaTime;
	}

	public float getDeltaTime() {
		return this.deltaTime;
	}

	public World getWorld() {
		return world;
	}

	public SystemGroup getGroup() {
		return this.group;
	}

	public Bag<ComponentMapper<? extends Component>> getComponentMapper() {
		return this.mappers;
	}

	protected void initialize() {
		this.entityEdit = new EntityEdit(this.world, this.group);
	}

	public EntityEdit getEntityEdit() {
		return this.entityEdit;
	}

	void process() {
		if (checkProcessing()) {
//			for (int i = 0, s = this.mappers.size(); i < s; i++) {
//				this.mappers.get(i).callListener();
//			}
			this.processSystem();
//			for (int i = 0, s = this.mappers.size(); i < s; i++) {
//				ComponentMapper<?> mapper = this.mappers.get(i);
////				if (mapper.isSynchronized())
//					mapper.publicAccess();
//			}
		}
	}

	protected boolean checkProcessing() {
		return true;
	}

	protected abstract void processSystem();

	public void dispose() {
	}

}
