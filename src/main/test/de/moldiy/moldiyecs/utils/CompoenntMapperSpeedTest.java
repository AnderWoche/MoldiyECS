package de.moldiy.moldiyecs.utils;

import org.junit.Test;

import de.moldiy.moldiyecs.componentmanager.Component;
import de.moldiy.moldiyecs.componentmanager.ComponentManager;
import de.moldiy.moldiyecs.componentmanager.ComponentMapper;
import de.moldiy.moldiyecs.systems.SystemGroup;

public class CompoenntMapperSpeedTest {
	
	public static class posComponent implements Component {
		@Override
		public void reset() {
		}
		
	}
	
	@Test
	public void speedTest() {
		SystemGroup group = new SystemGroup();
		ComponentManager manager = new ComponentManager();
		
		ComponentMapper<posComponent> mapper = manager.getMapper(posComponent.class, group);
		mapper.create(1);
		
		long startTimeWithGroup = System.currentTimeMillis();
		for(int i = 0; i < 100_000; i++) {
			manager.getMapper(posComponent.class, group).get(1);
		}
		System.out.println("with Group mapper = " + (System.currentTimeMillis() - startTimeWithGroup) + " milis");
		
		long startTimeWithOutGroup = System.currentTimeMillis();
		for(int i = 0; i < 100_000; i++) {
			mapper.get(1);
		}
		System.out.println("normnal time = " + (System.currentTimeMillis() - startTimeWithOutGroup) + " milis");
		
	}

}
