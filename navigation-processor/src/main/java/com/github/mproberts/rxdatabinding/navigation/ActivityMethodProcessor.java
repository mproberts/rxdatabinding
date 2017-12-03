package com.github.mproberts.rxdatabinding.navigation;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;

class ActivityMethodProcessor {
    private static final String EXTRA_METHOD_NAME = "com.github.mproberts.rxdatabinding.METHOD_NAME";
    private static final TypeName INTENT_TYPE_NAME = ClassName.get("android.content", "Intent");

    private final TypeSpec.Builder _navigationSourceRoot;
    private TypeSpec.Builder _activityProviderBuilder;
    private TypeSpec.Builder _activityBuilder;
    private MethodSpec.Builder _activityIntentBuilder;
    private boolean _initialized;
    private final NavigationSourceInfoProvider _info;
    private final String _codegenPrefix;

    public ActivityMethodProcessor(String codegenPrefix, NavigationSourceInfoProvider info, TypeSpec.Builder navigationSourceRoot) {
        _navigationSourceRoot = navigationSourceRoot;
        _info = info;
        _codegenPrefix = codegenPrefix;
    }

    private void prepare(ExecutableElement method, final ActivityNavigation activityAnnotation) {
        final TypeName baseActivityTypeName = CodegenTools.typeNameOf(_info.getBaseActivityTypeName());

        String methodName = method.getSimpleName().toString();
        String declaringNavigator = _info.findNavigationTypeForMethod(methodName);
        String viewModelType = _info.findTypeElementForNavigator(declaringNavigator);
        TypeName viewModelTypeName = CodegenTools.typeNameOf(viewModelType);
        String viewModelClassName = CodegenTools.classNameOf(viewModelType);
        String providerClassName = _info.getTargetClassName() + viewModelClassName + "Provider";

        TypeName rootTypeName = ClassName.get(_info.getTargetPackage(), _codegenPrefix + _info.getTargetClassName());
        TypeName providerTypeName = ClassName.get(_info.getTargetPackage(), _codegenPrefix + _info.getTargetClassName(), providerClassName);
        String targetClassFullName = _codegenPrefix + CodegenTools.classNameOf(CodegenTools.getQualifiedClassName(new Callable<Class<?>>() {
            @Override
            public Class<?> call() throws Exception {
                return activityAnnotation.value();
            }
        }));

        _activityProviderBuilder = TypeSpec.interfaceBuilder(providerClassName)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC);
        _activityBuilder = TypeSpec.classBuilder(targetClassFullName)
                .superclass(baseActivityTypeName)
                .addSuperinterface(providerTypeName)
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);

        _activityBuilder.addField(ClassName.get("android.databinding", "ViewDataBinding"), "_binding", Modifier.PRIVATE);
        _activityBuilder.addMethod(MethodSpec.methodBuilder("handleIntent")
                .addModifiers(Modifier.PRIVATE)
                .addParameter(INTENT_TYPE_NAME, "intent")
                .addStatement("Object viewModel = $T.intentTo$L(intent, this)", rootTypeName, viewModelClassName)
                .addStatement("_binding.setVariable($L.model, viewModel)", _info.getBindingClassName())
                .build());
        _activityBuilder.addMethod(MethodSpec.methodBuilder("getLayoutResource")
                .addModifiers(Modifier.PROTECTED, Modifier.ABSTRACT)
                .returns(TypeName.INT)
                .addAnnotation(ClassName.get("android.support.annotation", "LayoutRes"))
                .build());
        _activityBuilder.addMethod(MethodSpec.methodBuilder("onCreate")
                .addModifiers(Modifier.PROTECTED)
                .addAnnotation(Override.class)
                .addParameter(ClassName.get("android.os", "Bundle"), "savedInstanceState")
                .addStatement("super.onCreate(savedInstanceState)")
                .addStatement("_binding = android.databinding.DataBindingUtil.setContentView(this, getLayoutResource())")
                .addStatement("handleIntent(getIntent())")
                .build());
        _activityBuilder.addMethod(MethodSpec.methodBuilder("onNewIntent")
                .addModifiers(Modifier.PROTECTED)
                .addAnnotation(Override.class)
                .addParameter(INTENT_TYPE_NAME, "intent")
                .addStatement("super.onNewIntent(intent)")
                .addStatement("handleIntent(intent)")
                .build());

        _activityIntentBuilder = MethodSpec.methodBuilder("intentTo" + viewModelClassName)
                .addParameter(INTENT_TYPE_NAME, "intent")
                .addParameter(providerTypeName, "provider")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addStatement("$T viewModel = null", viewModelTypeName)
                .addStatement("String methodName = intent.getStringExtra($S)", EXTRA_METHOD_NAME)
                .returns(viewModelTypeName);
    }

    public void addMethod(ExecutableElement method, final ActivityNavigation activityAnnotation) {
        String targetClassFullName = CodegenTools.getQualifiedClassName(new Callable<Class<?>>() {
            @Override
            public Class<?> call() throws Exception {
                return activityAnnotation.value();
            }
        });
        String methodName = method.getSimpleName().toString();
        String declaringNavigator = _info.findNavigationTypeForMethod(methodName);
        String viewModelType = _info.findTypeElementForNavigator(declaringNavigator);
        TypeName viewModelTypeName = CodegenTools.typeNameOf(viewModelType);

        if (!_initialized) {
            _initialized = true;
            prepare(method, activityAnnotation);
        }

        List<? extends VariableElement> parameterTypes = method.getParameters();

        CodeBlock.Builder codeBuilder = CodeBlock.builder()
                .addStatement("$T intent = new Intent(_context, $N.class)", INTENT_TYPE_NAME, targetClassFullName);
        CodeBlock.Builder activityCodeBuilder = CodeBlock.builder();

        MethodSpec.Builder navigatorMethodBuilder = MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class);

        MethodSpec.Builder activityMethodBuilder = MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
                .returns(viewModelTypeName);

        codeBuilder.addStatement("intent.putExtra($S, $S)", EXTRA_METHOD_NAME, methodName);

        if (parameterTypes.size() == 0) {
            activityCodeBuilder.beginControlFlow("if (methodName == null || $S.equals(methodName))", methodName);
        }
        else {
            activityCodeBuilder.beginControlFlow("if ($S.equals(methodName))", methodName);
        }

        StringBuilder callParameters = new StringBuilder();

        for (VariableElement parameterType : parameterTypes) {
            String name = parameterType.getSimpleName().toString();
            TypeName parameterTypeName = TypeName.get(parameterType.asType());

            activityMethodBuilder.addParameter(parameterTypeName, name);
            navigatorMethodBuilder.addParameter(parameterTypeName, name);

            codeBuilder.addStatement("intent.putExtra($S, $N)", name, name);

            String parameterClassName = CodegenTools.classNameOf(parameterTypeName.toString());

            if (parameterClassName.equals("String")) {
                activityCodeBuilder.addStatement("String $N = intent.getStringExtra($S)", name, name);
            }
            else if (parameterClassName.equals("boolean") || parameterTypeName.equals("Boolean")) {
                activityCodeBuilder.addStatement("boolean $N = intent.getBooleanExtra($S, false)", name, name);
            }
            else if (parameterClassName.equals("byte") || parameterTypeName.equals("Byte")) {
                activityCodeBuilder.addStatement("byte $N = intent.getByteExtra($S, 0)", name, name);
            }
            else if (parameterClassName.equals("int") || parameterTypeName.equals("Integer")) {
                activityCodeBuilder.addStatement("int $N = intent.getIntExtra($S, 0)", name, name);
            }
            else if (parameterClassName.equals("long") || parameterTypeName.equals("Long")) {
                activityCodeBuilder.addStatement("long $N = intent.getLongExtra($S, 0)", name, name);
            }
            else if (parameterClassName.equals("float") || parameterTypeName.equals("Float")) {
                activityCodeBuilder.addStatement("float $N = intent.getFloatExtra($S, 0)", name, name);
            }
            else if (parameterClassName.equals("double") || parameterTypeName.equals("Double")) {
                activityCodeBuilder.addStatement("double $N = intent.getDoubleExtra($S, 0)", name, name);
            }
            else  {
                activityCodeBuilder.addStatement("$T $N = ($T) intent.getSerializableExtra($S)", parameterTypeName, name, parameterTypeName, name);
            }

            if (callParameters.length() != 0) {
                callParameters.append(", ");
            }

            callParameters.append(name);
        }

        activityCodeBuilder.addStatement("viewModel = provider.$N($L)", methodName, callParameters);

        activityCodeBuilder.endControlFlow();

        _activityIntentBuilder.addCode(activityCodeBuilder.build());

        codeBuilder.addStatement("_context.startActivity(intent)");
        navigatorMethodBuilder.addCode(codeBuilder.build());

        _navigationSourceRoot.addMethod(navigatorMethodBuilder.build());
        _activityProviderBuilder.addMethod(activityMethodBuilder.build());
    }

    public void finish(RoundEnvironment roundEnvironment, ProcessingEnvironment processingEnv) {
        _activityIntentBuilder.beginControlFlow("if (viewModel != null)")
                .addStatement("return viewModel")
                .endControlFlow()
                .addStatement("throw new $T(\"Unsupported Intent\")", IllegalArgumentException.class);

        _navigationSourceRoot.addType(_activityProviderBuilder.build());
        _navigationSourceRoot.addMethod(_activityIntentBuilder.build());

        JavaFile activityJavaFile = JavaFile.builder(_info.getTargetPackage(), _activityBuilder.build()).build();

        try {
            activityJavaFile.writeTo(processingEnv.getFiler());
        }
        catch (IOException e) {
            roundEnvironment.errorRaised();
        }
    }
}
