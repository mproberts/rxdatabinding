package com.github.mproberts.rxdatabinding.navigation;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import java.util.concurrent.Callable;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;

final class CodegenTools {

    private CodegenTools() {
        // intentionally blank
    }

    public static TypeName typeNameOf(String fullyQualifiedName) {
        return ClassName.get(packageOf(fullyQualifiedName), classNameOf(fullyQualifiedName));
    }

    public static final String packageOf(TypeElement element) {
        return packageOf(element.toString());
    }

    public static final String packageOf(String className) {
        String elementName = className.toString();

        int index = elementName.lastIndexOf('.');

        if (index <= 0) {
            return "";
        }

        return elementName.substring(0, index);
    }

    public static final String classNameOf(TypeElement element) {
        return classNameOf(element.getSimpleName().toString());
    }

    public static final String classNameOf(String className) {
        String elementName = className.toString();

        int index = elementName.lastIndexOf('.');

        if (index <= 0) {
            return elementName;
        }

        return elementName.substring(index + 1);
    }

    public static String getQualifiedClassName(Callable<?> callable) {
        try {
            return callable.call().toString();
        }
        catch (MirroredTypeException exceptionWithRequiredInfo) {
            return exceptionWithRequiredInfo.getTypeMirror().toString();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
