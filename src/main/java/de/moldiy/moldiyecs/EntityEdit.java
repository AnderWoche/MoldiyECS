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
		if (componentMapper.locked) {
			return componentMapper.create(entity);
		} else {
			componentMapper.locked = true;
			T componentObject = componentMapper.create(entity);
			componentMapper.locked = false;
			return componentObject;
		}
	}

	public <T extends Component> void delete(int entity, Class<T> component) {
		ComponentMapper<T> componentMapper = this.world.getComponentManager().getMapper(component);
		if (componentMapper.locked) {
			componentMapper.remove(entity);
		} else {
			componentMapper.locked = true;
			componentMapper.remove(entity);
			componentMapper.locked = false;
		}
	}

	public <T extends Component> T createComponentOnly(Class<T> component) {
		ComponentMapper<T> componentMapper = this.world.getComponentManager().getMapper(component);
		if (componentMapper.locked) {
			return componentMapper.createComponentOnly();
		} else {
			componentMapper.locked = true;
			T returnObject = componentMapper.createComponentOnly();
			componentMapper.locked = false;
			return returnObject;
		}
	}

	public <T extends Component> void addComponent(int entity, T component) {
		@SuppressWarnings("unchecked")
		ComponentMapper<T> mapper = (ComponentMapper<T>) this.world.getComponentManager()
				.getMapper(component.getClass());
		if (mapper.locked) {
			mapper.add(entity, component);
		} else {
			mapper.locked = true;
			mapper.add(entity, component);
			mapper.locked = false;
		}
	}

}
