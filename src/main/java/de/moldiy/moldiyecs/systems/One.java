package de.moldiy.moldiyecs.systems;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.moldiy.moldiyecs.componentmanager.Component;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface One {
	/**
	 * @return required types
	 */
	Class<? extends Component>[] value();
}
