package com.nytimes.android.external.storeannotations;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

@SupportedAnnotationTypes("com.nytimes.android.external.storeannotations.BuildStore")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class StoreProcessor extends AbstractProcessor {
    private Types typeUtils;
    private Elements elementUtils;
    private Filer filer;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        typeUtils = processingEnv.getTypeUtils();
        elementUtils = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {





        for (Element annotatedElement : roundEnvironment.getElementsAnnotatedWith(BuildStore.class)) {

            processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING,"found our annotation");

            // We can cast it, because we know that it of ElementKind.CLASS
            TypeElement typeElement = (TypeElement) annotatedElement;

            try {
                FactoryAnnotatedClass annotatedClass =
                        new FactoryAnnotatedClass(typeElement, processingEnv); // throws IllegalArgumentException

            } catch (IllegalArgumentException e) {



                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,"FOO error:" + e.getMessage());
                processingEnv.getMessager().printMessage(Diagnostic.Kind.MANDATORY_WARNING,"FOO error:" + e.getMessage());
                processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING,"FOO error:" + e.getMessage());
                processingEnv.getMessager().printMessage(Diagnostic.Kind.OTHER,"FOO error:" + e.getMessage());

                // @Factory.id() is empty
//                error(typeElement, e.getMessage());
                return true;
            }


//        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,"START of PROCESS");
            return false;
        }
        return false;
    }
}
