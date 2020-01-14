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
package de.moldiy.moldiyecs.componentmanager;

import java.lang.reflect.Modifier;
import java.util.IdentityHashMap;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import de.moldiy.moldiyecs.utils.reflect.ClassReflection;
import de.moldiy.moldiyecs.utils.reflect.Constructor;
import de.moldiy.moldiyecs.utils.reflect.ReflectionException;

public class ComponentIDFactory {

	private final IdentityHashMap<Class<? extends Component>, Integer> componentids = new IdentityHashMap<Class<? extends Component>, Integer>();

	private Lock lock;

	public ComponentIDFactory() {
		this.lock = new ReentrantLock();
	}

	@SuppressWarnings("unchecked")
	public Entry<Class<? extends Component>, Integer> getAllComponentIDs() {
		return (Entry<Class<? extends Component>, Integer>) componentids.entrySet();
	}

	public int getComponentIDFor(Class<? extends Component> c) {
		Integer componentID = componentids.get(c);

		if (componentID == null) {
			componentID = this.createComponentID(c);
		}

		return componentID;
	}

	private int createComponentID(Class<? extends Component> c) {
		lock.lock();
		try {
			Constructor ctor = ClassReflection.getConstructor(c);
			if ((ctor.getModifiers() & Modifier.PUBLIC) == 0)
				throw new IllegalArgumentException(c.getClass() + "missing public constructor");
		} catch (ReflectionException e) {
			throw new IllegalArgumentException(c.getClass() + "missing public constructor", e);
		}

		int componentID = componentids.size();

		this.componentids.put(c, componentID);

		lock.unlock();
		return componentID;
	}

}
