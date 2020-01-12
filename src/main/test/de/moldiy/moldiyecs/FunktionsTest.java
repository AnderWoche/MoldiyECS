package de.moldiy.moldiyecs;

import org.junit.Test;

import de.moldiy.moldiyecs.componentmanager.Component;
import de.moldiy.moldiyecs.componentmanager.ComponentMapper;
import de.moldiy.moldiyecs.componentmanager.Mapper;
import de.moldiy.moldiyecs.systems.All;
import de.moldiy.moldiyecs.systems.ManagedSystem;
import de.moldiy.moldiyecs.systems.ThreadedSystemGroup;

public class FunktionsTest {
	
	public static class TransformCompoent implements Component {
		public float x, y;
		@Override
		public void reset() {
		}
	}
	public static class LiveComponent implements Component {
		public float max, current;
		@Override
		public void reset() {
		}
	}
	
	@All({TransformCompoent.class})
	private class MoveSystem extends ManagedSystem {

		@Mapper(TransformCompoent.class)
		private ComponentMapper<TransformCompoent> transformM;
		
		@Override
		public void processEntity(int entity) {
//			System.out.println(transformM.get(entity).y);
			transformM.remove(entity);
			transformM.create(entity);
		}
		
	}
	
	@All({TransformCompoent.class})
	private class MoveSystem2 extends ManagedSystem {
		@Mapper(TransformCompoent.class)
		private ComponentMapper<TransformCompoent> transformM;
		
		@Override
		public void processEntity(int entity) {
//			System.out.println(transformM.get(entity).x);
			transformM.remove(entity);
			transformM.create(entity);
		}
	}
	
	@Test
	public void test() {
		World w = new World();
		
		w.getSystemManager().addSystem(new MoveSystem(), w.getSystemManager().getMainThreadSystemGroup());
		
		ThreadedSystemGroup threadedSystemGroup = w.getSystemManager().createSystemThreadGroup();
		w.getSystemManager().addSystem(new MoveSystem2(), threadedSystemGroup);
		
		w.start();
		
		for(int i = 0; i < 2000; i++) {
			int entity = w.getEntityManager().create();
			ComponentMapper<TransformCompoent> component = w.getComponentManager().getMapper(TransformCompoent.class, w.getSystemManager().getMainThreadSystemGroup());
			component.create(entity);
		}
		
		for(int i = 0; i < 1_000_000; i++) {
			w.process();
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}

}
