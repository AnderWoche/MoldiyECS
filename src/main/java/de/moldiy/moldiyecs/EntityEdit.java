package de.moldiy.moldiyecs;

import de.moldiy.moldiyecs.componentmanager.Component;
import de.moldiy.moldiyecs.componentmanager.ComponentMapper;
import de.moldiy.moldiyecs.systems.SystemGroup;

public class EntityEdit {

	private SystemGroup group;
	private World world;

	public EntityEdit(World world, SystemGroup group) {
		this.world = world;
		this.group = group;
	}

	public World getWorld() {
		return this.world;
	}

	public SystemGroup getGroup() {
		return this.group;
	}

	public <T extends Component> T create(int entity, Class<T> component) {
		ComponentMapper<T> componentMapper = this.world.getComponentManager().getMapper(component);
		
		T componentObject;
		if (componentMapper.isSynchronized()) {
			componentObject = componentMapper.create(entity);
		} else {
			componentMapper.setSynchronized(true);
			componentObject = componentMapper.create(entity);
			componentMapper.setSynchronized(false);
		}
		return componentObject;
	}

	public <T extends Component> void delete(int entity, Class<T> component) {
		ComponentMapper<T> componentMapper = this.world.getComponentManager().getMapper(component);
		if (componentMapper.isSynchronized()) {
			componentMapper.remove(entity);
		} else {
			componentMapper.setSynchronized(true);
			componentMapper.remove(entity);
			componentMapper.setSynchronized(false);
		}
	}

	public <T extends Component> T createComponentOnly(Class<T> component) {
		ComponentMapper<T> componentMapper = this.world.getComponentManager().getMapper(component);
		if (componentMapper.isSynchronized()) {
			return componentMapper.createComponentOnly();
		} else {
			componentMapper.setSynchronized(true);
			T returnObject = componentMapper.createComponentOnly();
			componentMapper.setSynchronized(false);
			return returnObject;
		}
	}

	public <T extends Component> void addComponent(int entity, T component) {
		@SuppressWarnings("unchecked")
		ComponentMapper<T> mapper = (ComponentMapper<T>) this.world.getComponentManager()
				.getMapper(component.getClass());
		if (mapper.isSynchronized()) {
			mapper.add(entity, component);
		} else {
			mapper.setSynchronized(true);
			mapper.add(entity, component);
			mapper.setSynchronized(false);
		}
	}

}
