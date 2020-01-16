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
		return this.world.getComponentManager().getMapper(component, this.group).create(entity);
	}

	public <T extends Component> void delete(int entity, Class<T> component) {
		this.world.getComponentManager().getMapper(component, this.group).remove(entity);
	}

	public <T extends Component> T createComponentOnly(int entity, Class<T> component) {
		return this.world.getComponentManager().getMapper(component, this.group).createComponentOnly();
	}

	public <T extends Component> void addComponent(int entity, T component) {
		@SuppressWarnings("unchecked")
		ComponentMapper<T> mapper = (ComponentMapper<T>) this.world.getComponentManager().getMapper(component.getClass(), this.group);
		mapper.add(entity, component);
	}

}
