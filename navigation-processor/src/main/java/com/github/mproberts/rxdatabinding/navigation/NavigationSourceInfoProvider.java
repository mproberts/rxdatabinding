package com.github.mproberts.rxdatabinding.navigation;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

class NavigationSourceInfoProvider {

    private final String _targetPackage;
    private final String _targetClassName;
    private final String _baseActivityTypeName;
    private String _brClassName;
    private final Map<String, String> _viewModelTypeElements = new HashMap<>();
    private final Map<String, String> _methodToTypeResolver = new HashMap<>();

    public NavigationSourceInfoProvider(TypeElement element, Set<? extends Element> rootElements, String targetClassName, String targetPackage, String baseActivityTypeName) {
        _targetClassName = targetClassName;
        _targetPackage = targetPackage;
        _baseActivityTypeName = baseActivityTypeName;
        buildResolverMaps(element, rootElements);
    }

    public String getTargetClassName() {
        return _targetClassName;
    }

    public String getTargetPackage() {
        return _targetPackage;
    }

    public String getBaseActivityTypeName() {
        return _baseActivityTypeName;
    }

    public String getBindingClassName() {
        return _brClassName;
    }

    public String findTypeElementForNavigator(String navigator) {
        return _viewModelTypeElements.get(navigator);
    }

    public String findNavigationTypeForMethod(String methodName) {
        return _methodToTypeResolver.get(methodName);
    }

    private void buildResolverMaps(TypeElement element, Set<? extends Element> rootElements) {

        for (Element rootElement : rootElements) {
            if (rootElement.toString().endsWith(".BuildConfig")) {
                String qualifiedName = rootElement.toString();
                String rootPackage = CodegenTools.packageOf(qualifiedName);

                _brClassName = rootPackage + ".BR";
            }
        }


        for (TypeMirror anInterface : element.getInterfaces()) {
            String navigationTypeName = anInterface.toString();

            try {
                Class<?> navigationClass = Class.forName(navigationTypeName);

                throw new IllegalStateException("Only inner classes can be used as navigators");
            } catch (ClassNotFoundException e) {
                String outerClassName = CodegenTools.packageOf(navigationTypeName);
                String innerClassName = outerClassName + "$" + CodegenTools.classNameOf(navigationTypeName);
                try {
                    Class<?> navigationInnerClass = Class.forName(innerClassName);

                    for (Method method : navigationInnerClass.getMethods()) {
                        String methodName = method.getName();

                        _methodToTypeResolver.put(methodName, navigationTypeName);
                    }

                    _viewModelTypeElements.put(navigationTypeName, outerClassName);

                    return;
                } catch (ClassNotFoundException e1) {
                    // ignored
                }
            }

            for (Element rootElement : rootElements) {
                if (!(rootElement instanceof TypeElement)) {
                    continue;
                }

                TypeElement checkTypeElement = (TypeElement) rootElement;
                String classTypeName = checkTypeElement.toString();

                if (navigationTypeName.startsWith(classTypeName)) {
                    String checkElementTypeName = classTypeName;
                    _viewModelTypeElements.put(navigationTypeName, checkElementTypeName);

                    List<? extends Element> enclosedElements = checkTypeElement.getEnclosedElements();

                    for (Element enclosedElement : enclosedElements) {
                        if (!(enclosedElement instanceof TypeElement)) {
                            continue;
                        }

                        if (navigationTypeName.equals(enclosedElement.toString())) {
                            List<? extends Element> navigationMethodElements = enclosedElement.getEnclosedElements();

                            for (Element navigationMethodElement : navigationMethodElements) {
                                String methodName = navigationMethodElement.getSimpleName().toString();

                                _methodToTypeResolver.put(methodName, navigationTypeName);
                            }
                        }
                    }
                }
            }
        }
    }
}
