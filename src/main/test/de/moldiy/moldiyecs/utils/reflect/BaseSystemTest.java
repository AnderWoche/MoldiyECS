package de.moldiy.moldiyecs.utils.reflect;

import org.junit.Test;

import de.moldiy.moldiyecs.World;
import de.moldiy.moldiyecs.systems.BaseSystem;

public class BaseSystemTest {

	@Test
	public void test() throws ReflectionException {
		BaseSystem system = new BaseSystem() {
			@Override
			public void processSystem() {
			}
		};
		class TransormSystem extends BaseSystem {
			@Override
			public void processSystem() {
			}
		}
		TransormSystem system2 = new TransormSystem();
		class SuperSuperSystem extends TransormSystem {
			
		}
		SuperSuperSystem system3 = new SuperSuperSystem();
		
		initSystem(system);
		
		initSystem(system2);
		
		initSystem(system3);
		

	}
	
	public <T extends BaseSystem> void initSystem(T system) throws ReflectionException {
		Class<?> superClass = system.getClass();
		while(true) {
			Class<?> tempClass = superClass.getSuperclass();
			if(tempClass != null && tempClass != Object.class) {
				superClass = tempClass;
			} else break;
		}
		Field f = ClassReflection.getDeclaredField(superClass, "world");
		f.setAccessible(true);
		f.set(system, new World());
	}

}
