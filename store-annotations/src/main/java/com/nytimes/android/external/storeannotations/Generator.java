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

    private TypeElement classElement;
    private final ProcessingEnvironment env;


    public Generator(TypeElement classElement, ProcessingEnvironment env) throws IllegalArgumentException {
        this.classElement = classElement;
        this.env = env;
    }

    void writeFiles() {
        env.getMessager().printMessage(Diagnostic.Kind.WARNING, "class has annotation");

        List<? extends Element> methods = classElement.getEnclosedElements();

        String moduleClassName = classElement.getSimpleName() + "Module";
        TypeSpec.Builder moduleClassBuilder = TypeSpec.classBuilder(moduleClassName)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addAnnotation(Module.class);
        for (Element method : methods) {
            env.getMessager().printMessage(Diagnostic.Kind.WARNING, "we have methods");

            Annotation getAnnotation = method.getAnnotation(GET.class);
            Annotation persister = method.getAnnotation(Persister.class);
            Annotation persisterFile = method.getAnnotation(PersisterFile.class);
            if (getAnnotation != null) {

                ExecutableElement realMethod = (ExecutableElement) method;
                List<? extends VariableElement> enclosedElements = realMethod.getParameters();

                String className = capitalize(realMethod.getSimpleName().toString());

                String barcodeClassName = className + "BarCode";
                TypeSpec.Builder barcodeClassBuilder = TypeSpec.classBuilder(barcodeClassName)
                        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                        .superclass(BarCode.class);




               if(persister!=null){
                  moduleClassBuilder= generateProvidesMethod(realMethod,
                           capitalize(realMethod.getSimpleName().toString()), moduleClassBuilder);
               }

                if(persisterFile!=null){
                    moduleClassBuilder= generateProvidesMethodWithFile(realMethod,
                            capitalize(realMethod.getSimpleName().toString()), moduleClassBuilder);
                }


                MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC);


                for (VariableElement parameter : enclosedElements) {
                    parameter.asType();

                    generateProperty(barcodeClassBuilder, constructorBuilder, parameter);
                }


                barcodeClassBuilder.addMethod(constructorBuilder.build());
                JavaFile barcodeFile = JavaFile.builder("com.nytimes.android.store.generated", barcodeClassBuilder.build())
                        .build();



                writeFile( barcodeClassName, barcodeFile);
            }
        }

        JavaFile moduleFile = JavaFile.builder("com.nytimes.android.store.generated", moduleClassBuilder.build())
                .build();
        writeFile( moduleClassName, moduleFile);

    }

    private void writeFile(String className, JavaFile file) {
        try { // write the file
            JavaFileObject source = env.getFiler().createSourceFile("com.nytimes.android.store.generated." + className);
            Writer writer = source.openWriter();
            writer.write(file.toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            // Note: calling e.printStackTrace() will print IO errors
            // that occur from the file already existing after its first run, this is normal
        }
    }

    private TypeSpec.Builder generateProvidesMethod(ExecutableElement realMethod, String className, TypeSpec.Builder moduleClassBuilder) {
        ClassName persist = ClassName.get("com.nytimes.android.external.store.base", "Persister");
        ClassName store = ClassName.get("com.nytimes.android.external.store.base", "Store");

        TypeMirror returnType = realMethod.getReturnType();

        TypeName genericReturnType = TypeName.get(getGenericType(returnType));


        TypeName persister = ParameterizedTypeName.get(persist, genericReturnType);
        TypeName storeReturn = ParameterizedTypeName.get(store, genericReturnType);
        env.getMessager().printMessage(Diagnostic.Kind.WARNING, "FOO" +className);
        MethodSpec.Builder providesMethod = MethodSpec.methodBuilder("provide" + className + "Store")
                .addParameter(TypeName.get(classElement.asType()), classElement.getSimpleName().toString().toLowerCase())
                .addParameter(persister,"persister")
                .addModifiers(Modifier.PUBLIC)
                .addStatement("return null")
                .addAnnotation(Provides.class)
                .addAnnotation(Singleton.class)
                .returns(storeReturn);
        return moduleClassBuilder.addMethod(providesMethod.build());
    }

    private TypeSpec.Builder generateProvidesMethodWithFile(ExecutableElement realMethod, String className, TypeSpec.Builder moduleClassBuilder) {
        ClassName persist = ClassName.get("com.nytimes.android.external.store.base", "Persister");
        ClassName store = ClassName.get("com.nytimes.android.external.store.base", "Store");

        TypeMirror returnType = realMethod.getReturnType();

        TypeName genericReturnType = TypeName.get(getGenericType(returnType));


        TypeName persister = ParameterizedTypeName.get(persist, genericReturnType);
        TypeName storeReturn = ParameterizedTypeName.get(store, genericReturnType);
        env.getMessager().printMessage(Diagnostic.Kind.WARNING, "FOO" +className);
        MethodSpec.Builder providesMethod = MethodSpec.methodBuilder("provide" + className + "Store")
                .addParameter(TypeName.get(classElement.asType()), classElement.getSimpleName().toString().toLowerCase())
                .addParameter(String.class, "fileName")
                .addModifiers(Modifier.PUBLIC)
                .addStatement("return null")
                .addAnnotation(Provides.class)
                .addAnnotation(Singleton.class)
                .returns(storeReturn);
        return moduleClassBuilder.addMethod(providesMethod.build());
    }

    private void generateProperty(TypeSpec.Builder classBuilder, MethodSpec.Builder constructorBuilder, VariableElement parameter) {
        TypeName type = TypeName.get(parameter.asType());
        String name = parameter.getSimpleName().toString();
        classBuilder.addField(type, name, Modifier.PRIVATE, Modifier.FINAL);
        constructorBuilder.addParameter(type, name);
        constructorBuilder.addStatement("this." + name + " = " + name);
        MethodSpec.Builder getter = MethodSpec.methodBuilder("get" + capitalize(name))
                .addModifiers(Modifier.PUBLIC)
                .returns(type)
                .addStatement("return " +name);
        classBuilder.addMethod(getter.build());
    }

    public static String capitalize(String s) {
        if (s.length() == 0) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }


    public static TypeMirror getGenericType(final TypeMirror type)
    {
        final TypeMirror[] result = { null };

        type.accept(new SimpleTypeVisitor6<Void, Void>()
        {
            @Override
            public Void visitDeclared(DeclaredType declaredType, Void v)
            {
                List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
                if (!typeArguments.isEmpty())
                {
                    result[0] = typeArguments.get(0);
                }
                return null;
            }
            @Override
            public Void visitPrimitive(PrimitiveType primitiveType, Void v)
            {
                return null;
            }
            @Override
            public Void visitArray(ArrayType arrayType, Void v)
            {
                return null;
            }
            @Override
            public Void visitTypeVariable(TypeVariable typeVariable, Void v)
            {
                return null;
            }
            @Override
            public Void visitError(ErrorType errorType, Void v)
            {
                return null;
            }
            @Override
            protected Void defaultAction(TypeMirror typeMirror, Void v)
            {
                throw new UnsupportedOperationException();
            }
        }, null);

        return result[0];
    }

}
