package de.moldiy.moldiyecs.componentmanager;

import java.util.ArrayList;

import org.junit.Test;

import de.moldiy.moldiyecs.World;
import de.moldiy.moldiyecs.subscription.EntitySubscription.SubscriptionListener;
import de.moldiy.moldiyecs.systems.All;
import de.moldiy.moldiyecs.systems.IteratingSystem;
import de.moldiy.moldiyecs.systems.SystemInitalizer;
import de.moldiy.moldiyecs.utils.reflect.ReflectionException;

public class ComponentMapperTest {

	public static class TransformComponent implements Component {
		public float x, y;

		@Override
		public void reset() {

		}
	}

	@All({ TransformComponent.class })
	private static class TestSystem extends IteratingSystem {

		@Mapper(TransformComponent.class)
		private ComponentMapper<TransformComponent> transformM;

		private ArrayList<Integer> test = new ArrayList<Integer>();

		@Override
		protected void initialize() {
			transformM.setSynchronized(true);
			super.getSubscription().addSubscriptionListener(new SubscriptionListener() {
				@Override
				public void removed(int entity) {
					transformM.remove(entity);
					for (int i = 0; i < 100; i++) {
						if (test.contains(i))
							test.remove(i);
					}
				}

				@Override
				public void inserted(int entity) {
					for (int i = 0; i < 100; i++) {
						if(test.contains(i))
							test.add(1);
					}
				}
			});
			super.initialize();
		}

		@Override
		public void processSystem() {
//			System.out.println("Thread = " + Thread.currentThread());
//			transformM.callListener();

			for (int i = 0; i < 100; i++) {
				transformM.remove(i);
			}
			for (int i = 0; i < 100; i++) {
				transformM.create(i);
			}

			for (int lol : test) {
				int test2 = lol + 1;
			}

			super.processSystem();
//			System.out.println("Thread FERTIG: " + Thread.currentThread());
//			transformM.publicAccess();
		}

		@Override
		public void processEntity(int entity) {
		}
	}

	@Test
	public void test() {
		World w = new World();

		for (int i = 0; i < 100; i++) {
			w.createEntity();
		}

		{
			TestSystem system = new TestSystem();
			try {
				SystemInitalizer.initSystem(system, w.getSystemManager().getMainSystemGroup(), w);
				SystemInitalizer.initMapper(system, w.getSystemManager().getMainSystemGroup(), w.getComponentManager());
				system.initialize();
			} catch (ReflectionException e) {
				e.printStackTrace();
			}
			Thread t1 = new Thread(new Runnable() {
				@Override
				public void run() {
					while (true) {
						system.processSystem();
					}
				}
			});
			t1.start();
		}
		{
			TestSystem system = new TestSystem();
			try {
				SystemInitalizer.initSystem(system, w.getSystemManager().getMainSystemGroup(), w);
				SystemInitalizer.initMapper(system, w.getSystemManager().getMainSystemGroup(), w.getComponentManager());
				system.initialize();
			} catch (ReflectionException e) {
				e.printStackTrace();
			}
			Thread t1 = new Thread(new Runnable() {
				@Override
				public void run() {
					while (true) {
						system.processSystem();
					}
				}
			});
			t1.start();
		}
		{
			TestSystem system = new TestSystem();
			try {
				SystemInitalizer.initSystem(system, w.getSystemManager().getMainSystemGroup(), w);
				SystemInitalizer.initMapper(system, w.getSystemManager().getMainSystemGroup(), w.getComponentManager());
				system.initialize();
			} catch (ReflectionException e) {
				e.printStackTrace();
			}
			Thread t1 = new Thread(new Runnable() {
				@Override
				public void run() {
					while (true) {
						system.processSystem();
					}
				}
			});
			t1.start();
		}
		{
			TestSystem system = new TestSystem();
			try {
				SystemInitalizer.initSystem(system, w.getSystemManager().getMainSystemGroup(), w);
				SystemInitalizer.initMapper(system, w.getSystemManager().getMainSystemGroup(), w.getComponentManager());
				system.initialize();
			} catch (ReflectionException e) {
				e.printStackTrace();
			}
			Thread t1 = new Thread(new Runnable() {
				@Override
				public void run() {
					while (true) {
						system.processSystem();
					}
				}
			});
			t1.start();
		}
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

}
