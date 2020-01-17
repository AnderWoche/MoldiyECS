package de.moldiy.moldiyecs.systems;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadedSystemGroup extends SystemGroup {

	private boolean sleepAfterIteration = false;
	private int sleepTimeAfterIteration;
	
	private Thread thread;
	private boolean isThreadInPause;
	
	private Lock lock = new ReentrantLock();
	private Condition condition = lock.newCondition();
	
	public ThreadedSystemGroup() {
		this.thread = new Thread(new Runnable() {
			@Override
			public void run() {
				while(!thread.isInterrupted()) {
					if(isThreadInPause) {
						lock.lock();
						try {
							condition.await();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						lock.unlock();
					}
					process();
					if(sleepAfterIteration) {
						try {
							Thread.sleep(0, sleepTimeAfterIteration);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		});
		this.thread.setName("SystemThreadGroup-");
	}
	
	@Override
	public void initialize() {
		super.initialize();
		this.thread.start();
	}
	
	public void pause() {
		this.lock.lock();
		this.isThreadInPause = true;
		this.lock.unlock();
	}
	
	public void resume() {
		this.lock.lock();
		this.isThreadInPause = false;
		this.condition.signalAll();
		this.lock.unlock();
	}
	
	/**
	 * Set a iteration Per Second if you don't set it it's iterate so mutch
	 * it can, unlimetet.
	 * If if you want unlimeted iterations so set it 0.
	 * 
	 * @param iterationPerSecond
	 */
	public void setIterationPerSecond(int iterationPerSecond) {
		this.sleepTimeAfterIteration = (1_000_000 / iterationPerSecond);
		if(this.sleepTimeAfterIteration <= 1000) {
			this.sleepAfterIteration = false;
		} else {
			this.sleepAfterIteration = true;
		}
		System.out.println("sleep after iteration: " + this.sleepAfterIteration);
		System.out.println("nano time to sleep: " + this.sleepTimeAfterIteration);
	}
	
	public boolean isGroupPaused() {
		return this.isThreadInPause;
	}
}
