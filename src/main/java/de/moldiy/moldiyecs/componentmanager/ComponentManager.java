package de.moldiy.moldiyecs.componentmanager;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import de.moldiy.moldiyecs.componentmanager.ComponentMapper.ComponentListener;
import de.moldiy.moldiyecs.utils.Bag;

public class ComponentManager {

	private final Bag<ComponentMapper<? extends Component>> mappers = new Bag<ComponentMapper<? extends Component>>();

	private final ComponentIDFactory componentIDFactory = new ComponentIDFactory();

	private final Lock lock;

	private Bag<EntityChangedComponentIDsListener> listener = new Bag<ComponentManager.EntityChangedComponentIDsListener>(
			EntityChangedComponentIDsListener.class);
	private ComponentListener componentListener;

	public ComponentManager() {
		lock = new ReentrantLock();
		componentListener = new ComponentListener() {
			@Override
			public void componentAdded(Class<? extends Component> component, int entity) {
				notifyEntityChangedComponentIDsListener_ADDED(component, entity);
			}
			@Override
			public void componentDeleteted(Class<? extends Component> component, int entity) {
				notifyEntityChangedComponentIDsListener_REMOVED(component, entity);
			}
		};
	}

	@SuppressWarnings("unchecked")
	public <T extends Component> ComponentMapper<T> getMapper(Class<T> c) {
		int componentID = this.componentIDFactory.getComponentIDFor(c);
		ComponentMapper<?> mapper = this.mappers.get(componentID);
		if (mapper == null) {
			mapper = this.createMapper(c, componentID);
			mapper.addComponentListener(this.componentListener);
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
	
	public void addEntityChangedCompoenntIDsListener(EntityChangedComponentIDsListener changedComponentIDsListener) {
		this.listener.add(changedComponentIDsListener);
	}
	
	protected void notifyEntityChangedComponentIDsListener_ADDED(Class<? extends Component> component, int entity) {
		EntityChangedComponentIDsListener[] changedComponentIDs = this.listener.getData();
		for(int i = 0, s = this.listener.size(); i < s; i++) {
			changedComponentIDs[i].entityComponentIDAdded(component, entity);
		}
	}
	
	protected void notifyEntityChangedComponentIDsListener_REMOVED(Class<? extends Component> component, int entity) {
		EntityChangedComponentIDsListener[] changedComponentIDs = this.listener.getData();
		for(int i = 0, s = this.listener.size(); i < s; i++) {
			changedComponentIDs[i].entityComponentIDRemoved(component, entity);
		}
	}

	public interface EntityChangedComponentIDsListener {
		public void entityComponentIDRemoved(Class<? extends Component> component, int entity);

		public void entityComponentIDAdded(Class<? extends Component> component, int entity);
	}

}
