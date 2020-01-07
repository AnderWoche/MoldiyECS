package de.moldiy.moldiyecs.componentmanager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;

public class ComponentMapperTest {

	public static class TransformComponenet implements Component {
		public int x, y;

		public void reset() {
			this.x = 0;
			this.y = 0;
		}
	}

	@Test
	public void synchronizedTest() {
		ComponentManager manager = new ComponentManager();
		final ComponentMapper<TransformComponenet> mapper = manager.getMapper(TransformComponenet.class);

		mapper.create(0);

		ExecutorService executorService = Executors.newCachedThreadPool();

		executorService.execute(new Runnable() {
			public void run() {
				for (int i = 0; i < 200; i++) {
//					mapper.lock();
					TransformComponenet transformComponenet = mapper.get(0);
					System.out.println("Thread 1 x = " + transformComponenet.x);
					transformComponenet.x += 1;
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
//					mapper.unlock();
				}
			}
		});
		executorService.execute(new Runnable() {
			public void run() {
				for (int i = 0; i < 200; i++) {
//					mapper.lock();
					TransformComponenet transformComponenet = mapper.get(0);
					System.out.println("Thread 2 x = " + transformComponenet.x);
					transformComponenet.x = ++transformComponenet.x;
//					mapper.unlock();
				}
			}
		});

		try {
			Thread.sleep(1_000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		executorService.shutdown();
	}
}
