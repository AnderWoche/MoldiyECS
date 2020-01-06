package de.moldiy.moldiyecs.componentManager;

import java.lang.reflect.Modifier;
import java.util.IdentityHashMap;
import java.util.Map.Entry;

import de.moldiy.moldiyecs.utils.reflect.ClassReflection;
import de.moldiy.moldiyecs.utils.reflect.Constructor;
import de.moldiy.moldiyecs.utils.reflect.ReflectionException;

public class ComponentIDFactory {

	private final IdentityHashMap<Class<? extends Component>, Integer> componentids = new IdentityHashMap<Class<? extends Component>, Integer>();

	@SuppressWarnings("unchecked")
	public Entry<Class<? extends Component>, Integer> getAllComponentIDs() {
		return (Entry<Class<? extends Component>, Integer>) componentids.entrySet();
	}
	
	public int getComponentIDFor(Class<? extends Component> c) {
		Integer componentID = componentids.get(c);

		if (componentID == null) {
			componentID = this.createComponentID(c);
		}

		return componentID;
	}

	private int createComponentID(Class<? extends Component> c) {
		try {
			Constructor ctor = ClassReflection.getConstructor(c);
			if ((ctor.getModifiers() & Modifier.PUBLIC) == 0)
				throw new IllegalArgumentException(c.getClass() + "missing public constructor");
		} catch (ReflectionException e) {
			throw new IllegalArgumentException(c.getClass() + "missing public constructor", e);
		}

		int componentID = componentids.size();

		this.componentids.put(c, componentID);

		return componentID;
	}
	
}