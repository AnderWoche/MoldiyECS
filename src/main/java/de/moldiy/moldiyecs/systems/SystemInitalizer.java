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

import de.moldiy.moldiyecs.World;
import de.moldiy.moldiyecs.componentmanager.ComponentManager;
import de.moldiy.moldiyecs.componentmanager.ComponentMapper;
import de.moldiy.moldiyecs.componentmanager.Mapper;
import de.moldiy.moldiyecs.subscription.Aspect;
import de.moldiy.moldiyecs.utils.reflect.ClassReflection;
import de.moldiy.moldiyecs.utils.reflect.Field;
import de.moldiy.moldiyecs.utils.reflect.ReflectionException;

public class SystemInitalizer {

	public static <T extends BaseSystem> void initSystem(T system, SystemGroup group, World world) throws ReflectionException {
		Class<?> superClass = system.getClass();
		while (true) {
			if (superClass != null) {
				if (superClass == BaseSystem.class) {
					Field f = ClassReflection.getDeclaredField(superClass, "world");
					f.setAccessible(true);
					f.set(system, world);
					
					Field groupField = ClassReflection.getDeclaredField(superClass, "group");
					groupField.setAccessible(true);
					groupField.set(system, group);
				}
				if (superClass == IteratingSystem.class) {
					Aspect.Builder aspectBuilder = new Aspect.Builder();
					if (superClass.isAnnotationPresent(All.class)) {
						All all = superClass.getAnnotation(All.class);
						aspectBuilder.all(all.value());
					}
					if (superClass.isAnnotationPresent(One.class)) {
						One one = superClass.getAnnotation(One.class);
						aspectBuilder.one(one.value());
					}
					if (superClass.isAnnotationPresent(Exclude.class)) {
						Exclude exclude = superClass.getAnnotation(Exclude.class);
						aspectBuilder.exclude(exclude.value());
					}
					Field f = ClassReflection.getDeclaredField(superClass, "subscription");
					f.setAccessible(true);
					f.set(system, world.getSubscriptionManager().getSubscription(aspectBuilder));
				}
				superClass = superClass.getSuperclass();
			} else
				break;

		}

	}

	public static <T extends BaseSystem> void initMapperInSystem(T system, SystemGroup group,
			ComponentManager componentManager) throws ReflectionException {
		Field[] field = ClassReflection.getDeclaredFields(system.getClass());
		for (int i = 0, s = field.length; i < s; i++) {
			Class<?> fieldClass = field[i].getType();
			if (fieldClass == ComponentMapper.class) {
				Mapper mapperAnotation = field[i].getAnnotation(Mapper.class);
				if (mapperAnotation != null) {
					field[i].setAccessible(true);
					field[i].set(system, componentManager.getMapper(mapperAnotation.value(), group));
				}

			}
		}
	}

}
