package com.github.mproberts.rxdatabinding.navigation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates a root navigation object on which navigators can be build
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface NavigationSource {
    /**
     * The base activity type which will be used for all activities generated
     * for this navigation source
     * @return
     */
    Class<?> baseActivity();
}
