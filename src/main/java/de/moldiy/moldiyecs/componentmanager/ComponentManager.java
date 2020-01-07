package de.moldiy.moldiyecs.componentmanager;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import de.moldiy.moldiyecs.utils.Bag;

public class ComponentManager {

	private final Bag<ComponentMapper<? extends Component>> mappers = new Bag<ComponentMapper<? extends Component>>();

	private final ComponentIDFactory componentIDFactory = new ComponentIDFactory();

	private Lock lock;

	public ComponentManager() {
		lock = new ReentrantLock();
	}

	@SuppressWarnings("unchecked")
	public <T extends Component> ComponentMapper<T> getMapper(Class<T> c) {
		int componentID = this.componentIDFactory.getComponentIDFor(c);
		ComponentMapper<?> mapper = this.mappers.get(componentID);
		if (mapper == null) {
			mapper = this.createMapper(c, componentID);
		}
		return (ComponentMapper<T>) mapper;
	}

	private <T extends Component> ComponentMapper<T> createMapper(Class<T> c, int componentID) {
		lock.lock();
		ComponentMapper<T> mapper = new ComponentMapper<T>(c);
		this.mappers.set(componentID, mapper);
		lock.unlock();
		return mapper;
	}

	public ComponentIDFactory getComponentIDFactory() {
		return this.componentIDFactory;
	}

}
