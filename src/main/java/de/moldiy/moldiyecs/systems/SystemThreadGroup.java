package de.moldiy.moldiyecs.systems;


import de.moldiy.moldiyecs.utils.Bag;

public class SystemThreadGroup {

	private Bag<BaseSystem> systemsInGroup;
	
	private int sleepAfterIteration;
	
	private String groupName;
	private Thread thread;
	
	public SystemThreadGroup(int iterationPerSecond, String groupName, Bag<BaseSystem> systems) {
		this.sleepAfterIteration = 60_000 / iterationPerSecond;
		this.groupName = groupName;
		this.thread = new Thread(new Runnable() {
			@Override
			public void run() {
				while(!thread.isInterrupted()) {
					BaseSystem[] baseSystems = systemsInGroup.getData();
					for(int i = 0, s = systemsInGroup.size(); i < s; i++) {
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
	
	public void start() {
		this.thread.start();
	}
	
	public void dispose() {
		this.thread.interrupt();
	}
	
	public String getGroupName() {
		return this.groupName;
	}
}
