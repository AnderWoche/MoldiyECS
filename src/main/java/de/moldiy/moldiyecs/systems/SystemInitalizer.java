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
import de.moldiy.moldiyecs.componentmanager.Component;
import de.moldiy.moldiyecs.componentmanager.ComponentManager;
import de.moldiy.moldiyecs.componentmanager.ComponentMapper;
import de.moldiy.moldiyecs.componentmanager.ComponentMapperGetOnly;
import de.moldiy.moldiyecs.componentmanager.Mapper;
import de.moldiy.moldiyecs.subscription.Aspect;
import de.moldiy.moldiyecs.utils.Bag;
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
					if (system.getClass().isAnnotationPresent(All.class)) {
						All all = system.getClass().getAnnotation(All.class);
						aspectBuilder.all(all.value());
					}
					if (system.getClass().isAnnotationPresent(One.class)) {
						One one = system.getClass().getAnnotation(One.class);
						aspectBuilder.one(one.value());
					}
					if (system.getClass().isAnnotationPresent(Exclude.class)) {
						Exclude exclude = system.getClass().getAnnotation(Exclude.class);
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

	public static <T> void initMapper(T system, SystemGroup group,
			ComponentManager componentManager) throws ReflectionException {
		
		Class<?> baseSystem = system.getClass();
		while (true) {
			if (baseSystem != null) {
				if (baseSystem == BaseSystem.class) {
					break;
				}
				baseSystem = baseSystem.getSuperclass();
			} else
				break;
		}
		
		Field[] field = ClassReflection.getDeclaredFields(system.getClass());
		for (int i = 0, s = field.length; i < s; i++) {
			Class<?> fieldClass = field[i].getType();
			if (fieldClass == ComponentMapper.class) {
				Mapper mapperAnotation = field[i].getAnnotation(Mapper.class);
				if (mapperAnotation != null) {
					field[i].setAccessible(true);
					ComponentMapper<?> mapper = componentManager.getMapper(mapperAnotation.value(), group);
					field[i].set(system, mapper);
					if(baseSystem != null) {
						Field groupField = ClassReflection.getDeclaredField(baseSystem, "mappers");
						groupField.setAccessible(true);
						@SuppressWarnings("unchecked")
						Bag<ComponentMapper<? extends Component>> mappers = (Bag<ComponentMapper<? extends Component>>) groupField.get(system);
						mappers.add(mapper);
					}
				}
			} else if(fieldClass == ComponentMapperGetOnly.class) {
				Mapper mapperAnotation = field[i].getAnnotation(Mapper.class);
				if (mapperAnotation != null) {
					field[i].setAccessible(true);
					field[i].set(system, componentManager.getMapper(mapperAnotation.value()));
				}
			}
		}
	}

}
