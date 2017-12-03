package com.github.mproberts.rxdatabinding.navigation;

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
    private final Map<String, TypeElement> _viewModelTypeElements = new HashMap<>();
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

    public TypeElement findTypeElementForNavigator(String navigator) {
        return _viewModelTypeElements.get(navigator);
    }

    public String findNavigationTypeForMethod(String methodName) {
        return _methodToTypeResolver.get(methodName);
    }

    private void buildResolverMaps(TypeElement element, Set<? extends Element> rootElements) {

        for (TypeMirror anInterface : element.getInterfaces()) {
            String navigationTypeName = anInterface.toString();

            for (Element rootElement : rootElements) {
                if (!(rootElement instanceof TypeElement)) {
                    continue;
                }

                TypeElement checkTypeElement = (TypeElement) rootElement;
                String classTypeName = checkTypeElement.toString();

                if (navigationTypeName.startsWith(classTypeName)) {
                    _viewModelTypeElements.put(navigationTypeName, checkTypeElement);

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
