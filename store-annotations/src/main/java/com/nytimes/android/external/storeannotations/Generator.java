package com.nytimes.android.external.storeannotations;


import com.nytimes.android.external.store.base.BarCode;
import com.nytimes.android.external.store.base.annotation.BuildStore;
import com.nytimes.android.external.store.base.annotation.Persister;
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

import retrofit2.http.GET;

public class Generator {

    private TypeElement classElement;
    private final ProcessingEnvironment env;


    public Generator(TypeElement classElement, ProcessingEnvironment env) throws IllegalArgumentException {
        this.classElement = classElement;
        this.env = env;
    }

    void writeFiles() {
        BuildStore annotation = classElement.getAnnotation(BuildStore.class);
        env.getMessager().printMessage(Diagnostic.Kind.WARNING, "class has annotation");

        List<? extends Element> methods = classElement.getEnclosedElements();
        env.getMessager().printMessage(Diagnostic.Kind.WARNING, "before For loop");

        for (Element method : methods) {
            env.getMessager().printMessage(Diagnostic.Kind.WARNING, "we have methods");

            Annotation getAnnotation = method.getAnnotation(GET.class);
            Annotation persister = method.getAnnotation(Persister.class);
            if (getAnnotation != null) {
                env.getMessager().printMessage(Diagnostic.Kind.WARNING, "method has Get Annotation");

                ExecutableElement realMethod = (ExecutableElement) method;
                List<? extends VariableElement> enclosedElements = realMethod.getParameters();

                String className = capitalize(realMethod.getSimpleName().toString()) + "Module";

                TypeSpec.Builder classBuilder = TypeSpec.classBuilder(className)
                        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                        .superclass(BarCode.class);

               if(persister!=null){
                   generateProvidesMethod(realMethod,
                           capitalize(realMethod.getSimpleName().toString()), classBuilder);

               }


                MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC);


                for (VariableElement parameter : enclosedElements) {
                    parameter.asType();

                    generateProperty(classBuilder, constructorBuilder, parameter);
                }


                classBuilder.addMethod(constructorBuilder.build());
                JavaFile javaFile = JavaFile.builder("com.nytimes.android.store.generated", classBuilder.build())
                        .build();


                try { // write the file
                    JavaFileObject source = env.getFiler().createSourceFile("com.nytimes.android.store.generated." + className);


                    Writer writer = source.openWriter();
                    writer.write(javaFile.toString());
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    // Note: calling e.printStackTrace() will print IO errors
                    // that occur from the file already existing after its first run, this is normal
                }
                for (Element methodParams : enclosedElements) {
                    env.getMessager().printMessage(Diagnostic.Kind.WARNING, methodParams.asType().toString());

                }
            }
        }
    }

    private void generateProvidesMethod(ExecutableElement realMethod, String className, TypeSpec.Builder classBuilder) {
        ClassName persist = ClassName.get("com.nytimes.android.external.store.base", "Persister");
        ClassName store = ClassName.get("com.nytimes.android.external.store.base", "Store");




        TypeMirror returnType = realMethod.getReturnType();

        TypeName genericReturnType = TypeName.get(getGenericType(returnType));


        TypeName persister = ParameterizedTypeName.get(persist, genericReturnType);
        TypeName storeReturn = ParameterizedTypeName.get(store, genericReturnType);

        MethodSpec.Builder providesMethod = MethodSpec.methodBuilder("provide" + className + "Store")
                .addParameter(TypeName.get(classElement.asType()), classElement.getSimpleName().toString().toLowerCase())
                .addParameter(persister,"persister")
                .addModifiers(Modifier.PUBLIC)
                .addStatement("return null")
                .returns(storeReturn);
        classBuilder.addMethod(providesMethod.build());
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
