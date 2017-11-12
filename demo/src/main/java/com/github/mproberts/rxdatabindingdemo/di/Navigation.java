package com.github.mproberts.rxdatabindingdemo.di;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class Navigation {

    private static class ProxyNavigator implements InvocationHandler {

        private final Context _context;

        private ProxyNavigator(Context context) {
            _context = context;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // If the method is a method from Object then defer to normal invocation.
            if (method.getDeclaringClass() == Object.class) {
                return method.invoke(this, args);
            }

            ActivityNavigation activityNavigation = method.getAnnotation(ActivityNavigation.class);

            if (activityNavigation != null) {
                Class<? extends Activity> activityClass = activityNavigation.value();

                Intent intent = new Intent(_context, activityClass);

                if (args != null) {
                    for (int i = 0; i < args.length; ++i) {
                        String argName = "arg" + i;

                        if (args[i] instanceof Serializable) {
                            intent.putExtra(argName, (Serializable) args[i]);
                        } else {
                            Method putExtra = intent.getClass().getMethod("putExtra", String.class, args[i].getClass());

                            if (putExtra == null) {
                                throw new IllegalArgumentException(
                                        "Unserializable type "
                                                + args[i].getClass().getName()
                                                + " supplied to navigation method");
                            }
                            putExtra.invoke(intent, argName, args[i]);
                        }
                    }
                }

                _context.startActivity(intent);

                return null;
            }

            return method.invoke(proxy, args);
        }
    }

    public static <T> T create(Context context, Class<T> navigatorClass) {
        @SuppressWarnings("unchecked cast")
        T proxy = (T) Proxy.newProxyInstance(
                navigatorClass.getClassLoader(),
                new Class<?>[]{navigatorClass},
                new ProxyNavigator(context));

        return proxy;
    }
}
