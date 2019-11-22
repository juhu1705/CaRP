package de.juhu.dateimanager;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(FIELD)
public @interface ConfigElement {
	public String defaultValue();

	public Class elementClass();

	public String description();

	public String name();
}
