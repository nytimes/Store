package com.nytimes.android.external.storeannotations;

import com.nytimes.android.external.store.base.BarCode;
import com.nytimes.android.external.store.base.BuildStore;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
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
            if (getAnnotation != null) {
                env.getMessager().printMessage(Diagnostic.Kind.WARNING, "method has Get Annotation");

                ExecutableElement realMethod = (ExecutableElement) method;
                List<? extends VariableElement> enclosedElements = realMethod.getParameters();

                String className = capitalize(realMethod.getSimpleName().toString()) + "Barcode";

                TypeSpec.Builder classBuilder = TypeSpec.classBuilder(className)
                        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                        .superclass(BarCode.class);
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
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }


}
