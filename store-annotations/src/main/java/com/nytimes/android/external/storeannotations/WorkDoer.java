package com.nytimes.android.external.storeannotations;

import com.nytimes.android.external.store.base.BuildStore;
import com.nytimes.android.external.store.base.BarCode;
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

public class WorkDoer {

    private TypeElement annotatedClassElement;
    private String qualifiedSuperClassName;
    private String simpleTypeName;
    private String id;

    public WorkDoer(TypeElement classElement, ProcessingEnvironment env) throws IllegalArgumentException {
        this.annotatedClassElement = classElement;
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

                String className = realMethod.getSimpleName() + "Barcode";


                MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC);


                for (VariableElement parameter : enclosedElements) {
                    parameter.asType();

                    constructorBuilder.addParameter(TypeName.get(parameter.asType()), parameter.getSimpleName().toString());
                }


                TypeSpec classInstance = TypeSpec.classBuilder(className)
                        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                        .superclass(BarCode.class)
                        .addMethod(constructorBuilder.build())
                        .build();

                JavaFile javaFile = JavaFile.builder("com.nytimes.android.store.generated", classInstance)
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

    /**
     * Get the id as specified in {@link Factory#id()}.
     * return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Get the full qualified name of the type specified in  {@link Factory#type()}.
     *
     * @return qualified name
     */
    public String getQualifiedFactoryGroupName() {
        return qualifiedSuperClassName;
    }


    /**
     * Get the simple name of the type specified in  {@link Factory#type()}.
     *
     * @return qualified name
     */
    public String getSimpleFactoryGroupName() {
        return simpleTypeName;
    }

    /**
     * The original element that was annotated with @Factory
     */
    public TypeElement getTypeElement() {
        return annotatedClassElement;
    }
}
