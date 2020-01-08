package de.moldiy.moldiyecs.systems;


import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import de.moldiy.moldiyecs.utils.Bag;

public class SystemThreadGroup {

	private final Bag<BaseSystem> systems;
	
	private int sleepAfterIteration;
	
	private String groupName;
	private Thread thread;
	private boolean isThreadInPause;
	
	private Lock lock = new ReentrantLock();
	private Condition condition = lock.newCondition();
	
	public SystemThreadGroup(int iterationPerSecond, String groupName) {
		this.systems = new Bag<BaseSystem>(BaseSystem.class);
		this.setIterationPerSecond(iterationPerSecond);
		this.groupName = groupName;
		
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
					
					BaseSystem[] baseSystems = systems.getData();
					for(int i = 0, s = systems.size(); i < s; i++) {
						BaseSystem system = baseSystems[i];
						system.processSystem();
					}
					try {
						Thread.sleep(sleepAfterIteration);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		this.thread.setName(groupName);
	}
	
	public <T extends BaseSystem> void addSystem(T system) {
		this.systems.add(system);
	}
	
	public void start() {
		BaseSystem[] baseSystems = systems.getData();
		for(int i = 0, s = systems.size(); i < s; i++) {
			BaseSystem system = baseSystems[i];
			system.initialize();
		}	
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
	
	public void dispose() {
		this.thread.interrupt();
	}
	
	public String getGroupName() {
		return this.groupName;
	}
	
	public void setIterationPerSecond(int iterationPerSecond) {
		this.sleepAfterIteration = 1000 / iterationPerSecond;
	}
	
	public boolean isGroupPaused() {
		return this.isThreadInPause;
	}
}
