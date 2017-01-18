package com.nytimes.android.external.storeannotations;


import com.nytimes.android.external.store.base.BarCode;
import com.nytimes.android.external.store.base.annotation.Persister;
import com.nytimes.android.external.store.base.annotation.PersisterFile;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Locale;

import javax.annotation.processing.ProcessingEnvironment;
import javax.inject.Singleton;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ErrorType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.SimpleTypeVisitor6;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import dagger.Module;
import dagger.Provides;
import retrofit2.http.GET;

public class Generator {

    private static final String TARGET_PACKAGE = "com.nytimes.android.store.generated";
    private static final String PACKAGE_NAME = "com.nytimes.android.external.store.base";
    private static final String PERSISTER_NAME = "Persister";
    private static final String STORE_NAME = "Store";
    private final ProcessingEnvironment env;
    private final TypeElement classElement;


    public Generator(TypeElement classElement, ProcessingEnvironment env) throws IllegalArgumentException {
        this.classElement = classElement;
        this.env = env;
    }

    public static String capitalize(String s) {
        if (s.length() == 0) {
            return s;
        }
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    private static TypeMirror getGenericType(final TypeMirror type) {
        final TypeMirror[] result = {null};

        type.accept(new SimpleTypeVisitor6<Void, Void>() {
            @Override
            public Void visitDeclared(DeclaredType declaredType, Void v) {
                List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
                if (!typeArguments.isEmpty()) {
                    result[0] = typeArguments.get(0);
                }
                return null;
            }

            @Override
            public Void visitPrimitive(PrimitiveType primitiveType, Void v) {
                return null;
            }

            @Override
            public Void visitArray(ArrayType arrayType, Void v) {
                return null;
            }

            @Override
            public Void visitTypeVariable(TypeVariable typeVariable, Void v) {
                return null;
            }

            @Override
            public Void visitError(ErrorType errorType, Void v) {
                return null;
            }

            @Override
            protected Void defaultAction(TypeMirror typeMirror, Void v) {
                throw new UnsupportedOperationException();
            }
        }, null);

        return result[0];
    }

    void generateFiles() {
        env.getMessager().printMessage(Diagnostic.Kind.WARNING, "class has annotation");

        List<? extends Element> methods = getMethods();

        String moduleClassName = getModuleName();
        TypeSpec.Builder moduleClassBuilder = createModuleClassBuilder(moduleClassName);
        for (Element method : methods) {

            Annotations annotations = new Annotations(method).invoke();
            Annotation persister = annotations.persisterAnnototation();
            Annotation persisterFile = annotations.persisterFileAnnotation();
            if (annotations.getAnnotation != null) {
                moduleClassBuilder = generateForEachGetMethod(
                        moduleClassBuilder, (ExecutableElement) method, persister, persisterFile);
            }
        }

        writeFile(moduleClassName, moduleClassBuilder);

    }

    private void writeFile(String moduleClassName, TypeSpec.Builder moduleClassBuilder) {
        JavaFile moduleFile = JavaFile.builder(TARGET_PACKAGE, moduleClassBuilder.build())
                .build();
        writeOut(moduleClassName, moduleFile);
    }

    private TypeSpec.Builder generateForEachGetMethod(TypeSpec.Builder moduleClassBuilder,
                                                      ExecutableElement realMethod, Annotation persister,
                                                      Annotation persisterFile) {
        List<? extends VariableElement> methodParams = realMethod.getParameters();

        String className = methodName(realMethod);

        String barcodeClassName = className + "BarCode";
        TypeSpec.Builder barcodeClassBuilder = TypeSpec.classBuilder(barcodeClassName)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .superclass(BarCode.class);


        if (persister != null) {
            moduleClassBuilder = generateProvidesMethod(realMethod, methodName(realMethod), moduleClassBuilder);
        }

        if (persisterFile != null) {
            moduleClassBuilder = generateProvidesMethodWithFile(realMethod,
                    methodName(realMethod), moduleClassBuilder);
        }


        MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC);


        for (VariableElement parameter : methodParams) {
            parameter.asType();

            generateProperty(barcodeClassBuilder, constructorBuilder, parameter);
        }


        barcodeClassBuilder.addMethod(constructorBuilder.build());
        writeFile(barcodeClassName, barcodeClassBuilder);
        return moduleClassBuilder;
    }

    private String methodName(ExecutableElement realMethod) {
        return capitalize(realMethod.getSimpleName().toString());
    }

    private TypeSpec.Builder createModuleClassBuilder(String moduleClassName) {
        return TypeSpec.classBuilder(moduleClassName)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addAnnotation(Module.class);
    }

    private String getModuleName() {
        return classElement.getSimpleName() + "Module";
    }

    private List<? extends Element> getMethods() {
        return classElement.getEnclosedElements();
    }

    private void writeOut(String className, JavaFile file) {
        try { // write the file
            JavaFileObject source = env.getFiler().createSourceFile(TARGET_PACKAGE + "." + className);
            Writer writer = source.openWriter();
            writer.write(file.toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            // Note: calling e.printStackTrace() will print IO errors
            // that occur from the file already existing after its first run, this is normal
        }
    }

    private TypeSpec.Builder generateProvidesMethod(ExecutableElement method, String className,
                                                    TypeSpec.Builder classBuilder) {

        ClassName persist = ClassName.get(PACKAGE_NAME, PERSISTER_NAME);
        ClassName store = ClassName.get(PACKAGE_NAME, STORE_NAME);

        TypeName genericReturnType = TypeName.get(getGenericType(method.getReturnType()));
        TypeName persister = ParameterizedTypeName.get(persist, genericReturnType);
        TypeName storeReturn = ParameterizedTypeName.get(store, genericReturnType);

//        env.getMessager().printMessage(Diagnostic.Kind.WARNING, "FOO" + className);
        MethodSpec.Builder providesMethod = providesMethodBuilder(className, storeReturn)
                .addParameter(persister, PERSISTER_NAME.toLowerCase(Locale.US));
        return classBuilder.addMethod(providesMethod.build());
    }

    private TypeSpec.Builder generateProvidesMethodWithFile(ExecutableElement method, String className,
                                                            TypeSpec.Builder moduleClassBuilder) {
        ClassName store = ClassName.get(PACKAGE_NAME, STORE_NAME);
        TypeName genericReturnType = TypeName.get(getGenericType(method.getReturnType()));
        TypeName storeReturn = ParameterizedTypeName.get(store, genericReturnType);
//        env.getMessager().printMessage(Diagnostic.Kind.WARNING, "FOO" + genericReturnType.toString());
        MethodSpec.Builder providesMethod = providesMethodBuilder(className, storeReturn)
                .addParameter(String.class, "fileName");
        return moduleClassBuilder.addMethod(providesMethod.build());
    }

    private MethodSpec.Builder providesMethodBuilder(String className, TypeName storeReturn) {
        return MethodSpec.methodBuilder("provide" + className + "Store")
                .addParameter(TypeName.get(classElement.asType()), classElement.getSimpleName()
                        .toString().toLowerCase(Locale.US))
                .addModifiers(Modifier.PUBLIC)
                .addStatement("return null")
                .addAnnotation(Provides.class)
                .addAnnotation(Singleton.class)
                .returns(storeReturn);
    }

    private void generateProperty(TypeSpec.Builder classBuilder, MethodSpec.Builder constructorBuilder,
                                  VariableElement parameter) {
        TypeName type = TypeName.get(parameter.asType());
        String name = parameter.getSimpleName().toString();
        classBuilder.addField(type, name, Modifier.PRIVATE, Modifier.FINAL);
        constructorBuilder.addParameter(type, name);
        constructorBuilder.addStatement("this." + name + " = " + name);
        MethodSpec.Builder getter = MethodSpec.methodBuilder("get" + capitalize(name))
                .addModifiers(Modifier.PUBLIC)
                .returns(type)
                .addStatement("return " + name);
        classBuilder.addMethod(getter.build());
    }

    private static class Annotations {
        private final Element method;
        private Annotation getAnnotation;
        private Annotation persister;
        private Annotation persisterFile;

        public Annotations(Element method) {
            this.method = method;
        }

        public Annotation getAnnotation() {
            return getAnnotation;
        }

        public Annotation persisterAnnototation() {
            return persister;
        }

        public Annotation persisterFileAnnotation() {
            return persisterFile;
        }

        public Annotations invoke() {
            getAnnotation = method.getAnnotation(GET.class);
            persister = method.getAnnotation(Persister.class);
            persisterFile = method.getAnnotation(PersisterFile.class);
            return this;
        }
    }
}
