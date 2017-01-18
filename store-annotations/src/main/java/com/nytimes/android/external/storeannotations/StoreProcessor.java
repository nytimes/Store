package com.nytimes.android.external.storeannotations;

import com.nytimes.android.external.store.base.annotation.BuildStore;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

@SupportedAnnotationTypes("com.nytimes.android.external.store.base.annotation.BuildStore")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class StoreProcessor extends AbstractProcessor {

    //private Types typeUtils;
    //private Elements elementUtils;
    //private Filer filer;
    //private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        //typeUtils = processingEnv.getTypeUtils();
        //elementUtils = processingEnv.getElementUtils();
        //filer = processingEnv.getFiler();
        //messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {


        for (Element annotatedElement : roundEnvironment.getElementsAnnotatedWith(BuildStore.class)) {
            // We can cast it, because we know that it of ElementKind.CLASS
            TypeElement typeElement = (TypeElement) annotatedElement;

            try {
                new Generator(typeElement, processingEnv).generateFiles();
            } catch (IllegalArgumentException exception) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "error:" + exception.getMessage());
                return true;
            }
            return false;
        }
        return false;
    }

}
