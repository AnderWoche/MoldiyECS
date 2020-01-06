package de.moldiy.moldiyecs.componentManager;

import de.moldiy.moldiyecs.utils.Bag;

public class ComponentManager {

	private final Bag<ComponentMapper<? extends Component>> mappers = new Bag<ComponentMapper<? extends Component>>();

	private final ComponentIDFactory componentIDFactory = new ComponentIDFactory();
	

	public ComponentManager() {
		
	}

	@SuppressWarnings("unchecked")
	public <T extends Component> ComponentMapper<T> getMapper(Class<T> c) {
		int componentID = this.componentIDFactory.getComponentIDFor(c);
		ComponentMapper<?> mapper = this.mappers.get(componentID);
		if(mapper == null) {
			mapper = new ComponentMapper<T>(c);
			this.mappers.set(componentID, mapper);
		}
		return (ComponentMapper<T>) mapper;
	}
	
	public ComponentIDFactory getComponentIDFactory() {
		return this.componentIDFactory;
	}
	

}
