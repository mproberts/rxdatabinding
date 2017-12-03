package com.github.mproberts.rxdatabinding.navigation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated navigator method will be handled by bundling and
 * launching a particular Activity via an Intent
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface ActivityNavigation {
    /**
     * The target activity class which will be receive the view model
     * @return
     */
    Class<?> value();
}
