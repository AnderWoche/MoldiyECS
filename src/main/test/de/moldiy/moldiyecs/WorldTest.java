package de.moldiy.moldiyecs;

import org.junit.Test;

import de.moldiy.moldiyecs.componentmanager.Component;
import de.moldiy.moldiyecs.componentmanager.ComponentMapper;
import de.moldiy.moldiyecs.subscription.Aspect;
import de.moldiy.moldiyecs.subscription.EntitySubscription;
import de.moldiy.moldiyecs.systems.BaseSystem;
import de.moldiy.moldiyecs.utils.IntBag;

public class WorldTest {

	private static class TransformCompoent implements Component {
		@Override
		public void reset() {
		}
	};

	@Test
	public void worldCreationTest() {
		World w = new World();

		w.getSystemManager().addSystem(new BaseSystem() {
			@Override
			public void processSystem() {
			}
		});

		w.getSystemManager().createSystemThreadGroup("lol", 20000);
		
//		w.getSystemManager().getSystemThreadGroup("lol").addSystem(system); // system wird nciht inited!

		int entity = w.getEntityManager().create();
		
		w.getSystemManager().addSystem("lol", new BaseSystem() {

			private EntitySubscription sub;
			private ComponentMapper<TransformCompoent> transformM;

			@Override
			public void processSystem() {
				transformM.create(entity);
				
				IntBag entityIDs = sub.updateEntityBag();
				int[] entities = entityIDs.getData();
				for(int i = 0, s = entityIDs.size(); i < s; i++) {
					int entity = entities[i];
					
					this.transformM.remove(entity);
					System.out.println("ein entity!");
				}
			}

			@Override
			protected void initialize() {
				this.sub = super.getWorld().getSubscriptionManager().getSubscription(Aspect.all(TransformCompoent.class));
				this.transformM = super.getWorld().getComponentManager().getMapper(TransformCompoent.class);
				this.transformM.create(entity);
				super.initialize();
			}
		});

		w.start();

		long endTime = System.currentTimeMillis() + 10_000;
		while (System.currentTimeMillis() < endTime) {
//			w.getSystemManager().getSystemThreadGroup("lol").pause();
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
//			w.getSystemManager().getSystemThreadGroup("lol").resume();
		}
	}

}
