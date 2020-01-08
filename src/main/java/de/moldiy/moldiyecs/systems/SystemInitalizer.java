package de.moldiy.moldiyecs.systems;

import de.moldiy.moldiyecs.World;
import de.moldiy.moldiyecs.utils.reflect.ClassReflection;
import de.moldiy.moldiyecs.utils.reflect.Field;
import de.moldiy.moldiyecs.utils.reflect.ReflectionException;

public class SystemInitalizer {

	public static <T extends BaseSystem> void initSystem(T system, World world) throws ReflectionException {
		Class<?> superClass = system.getClass();
		while(true) {
			Class<?> tempClass = superClass.getSuperclass();
			if(tempClass != null && tempClass != Object.class) {
				superClass = tempClass;
			} else break;
		}
		Field f = ClassReflection.getDeclaredField(superClass, "world");
		f.setAccessible(true);
		f.set(system, world);
	}
	
}
