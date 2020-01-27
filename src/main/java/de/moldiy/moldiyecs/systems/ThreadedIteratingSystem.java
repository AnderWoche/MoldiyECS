package de.moldiy.moldiyecs.systems;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import de.moldiy.moldiyecs.subscription.EntitySubscription;
import de.moldiy.moldiyecs.utils.Bag;
import de.moldiy.moldiyecs.utils.IntBag;

public abstract class ThreadedIteratingSystem extends BaseSystem {

	private EntitySubscription subscription;
	
	public static final int CPU_CORES = Runtime.getRuntime().availableProcessors();
	private static final ExecutorService executor = Executors.newFixedThreadPool(CPU_CORES);
	private final Future<?>[] runningTasks = new Future[CPU_CORES];

	@Override
	public void processSystem() {
		IntBag entityIDs = subscription.updateEntityBag();
		int[] entities = entityIDs.getData();
		int size = entityIDs.size();
		int iterationAmout = size / CPU_CORES;
		
//		System.out.println("ITERATE THREADED = " + size);

		for (int k = 0; k < CPU_CORES; k++) {
			int indexFrom = k * iterationAmout;
			int indexTo;
			if ((k + 1) >= CPU_CORES) {
				indexTo = size;
			} else {
				indexTo = (k + 1) * iterationAmout;
			}
			Future<Boolean> process = executor.submit(()-> {
				for(int i = indexFrom; i < indexTo; i++) {
					this.processEntity(entities[i]);
				}
				return true;
			});
			this.runningTasks[k] = process;
		}
		for(int i = 0; i < this.runningTasks.length; i++) {
			try {
				this.runningTasks[i].get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
	}

	public abstract void processEntity(int entity);

	protected EntitySubscription getSubscription() {
		return this.subscription;
	}

}
