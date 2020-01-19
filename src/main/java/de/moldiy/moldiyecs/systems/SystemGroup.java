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

import de.moldiy.moldiyecs.utils.Bag;

public class SystemGroup {
	
	private final Bag<BaseSystem> systems = new Bag<>(BaseSystem.class);
	
	private long oldTime;
	private long newTime;
	
	/**
	 * don't remove Systems! Read only
	 * @return
	 */
	public Bag<BaseSystem> getSystems() {
		return this.systems;
	}
	
	/**
	 * Package Method so The SystemManager can init and add The System.
	 * @param system
	 */
	<T extends BaseSystem> void addSystem(T system) {
		this.systems.add(system);
	}
	
	public void initialize() {
		BaseSystem[] baseSystems = getSystems().getData();
		for(int i = 0, s = getSystems().size(); i < s; i++) {
			BaseSystem system = baseSystems[i];
			system.initialize();
		}	
	}
	
	public void process() {
		this.oldTime = this.newTime;
		this.newTime = System.currentTimeMillis();
		BaseSystem[] baseSystems = systems.getData();
		for(int i = 0, s = systems.size(); i < s; i++) {
			BaseSystem system = baseSystems[i];
			float delta = (newTime - oldTime) / 1000F;
			system.setDeltaTime(delta);
			system.processSystem();
		}
	}
	
}
