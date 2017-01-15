package com.nytimes.android.external.storeannotations;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import java.lang.annotation.Annotation;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.tools.Diagnostic;

import retrofit2.http.GET;

public class FactoryAnnotatedClass {

    private TypeElement annotatedClassElement;
    private String qualifiedSuperClassName;
    private String simpleTypeName;
    private String id;

    public FactoryAnnotatedClass(TypeElement classElement, ProcessingEnvironment env) throws IllegalArgumentException {
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

                TypeSpec helloWorld = TypeSpec.classBuilder(className)
                        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                        .build();

                JavaFile javaFile = JavaFile.builder("com.nytimes.android.store.generated", helloWorld)
                        .build();


                for (Element methodParams : enclosedElements) {
                    env.getMessager().printMessage(Diagnostic.Kind.WARNING, methodParams.asType().toString());

                }
            }
        }

        // Get the full QualifiedTypeName
        try {
            Class<?> clazz = annotation.apiFactory();
            qualifiedSuperClassName = clazz.getCanonicalName();
            simpleTypeName = clazz.getSimpleName();
        } catch (MirroredTypeException mte) {
            DeclaredType classTypeMirror = (DeclaredType) mte.getTypeMirror();
            TypeElement classTypeElement = (TypeElement) classTypeMirror.asElement();
            qualifiedSuperClassName = classTypeElement.getQualifiedName().toString();
            simpleTypeName = classTypeElement.getSimpleName().toString();
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
