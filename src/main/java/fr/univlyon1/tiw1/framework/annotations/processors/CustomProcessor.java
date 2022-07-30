package fr.univlyon1.tiw1.framework.annotations.processors;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Element;
import javax.annotation.processing.AbstractProcessor;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.stream.Collectors;

abstract public class CustomProcessor extends AbstractProcessor {

    /**
    protected <A> A getAnnotationInstance(Class<A> annotationType, Element element){
        return element.getAnnotation(annotationType);
    }
     **/
    protected void writeResult(Element element, TypeSpec subComponent){
        String packageName = element.toString();
        int separator = packageName.lastIndexOf(".");
        packageName = packageName.substring(0, separator);
        // Création du fichier source Java
        JavaFile javaFile = JavaFile
                .builder(packageName, subComponent)
                .build();

        try {
            // Utilisation de l'interface Filer pour récupérer un PrintWriter
            // vers le répertoire GeneratedSources indiqué dans le pom
            JavaFileObject builderFile = processingEnv
                    .getFiler()
                    .createSourceFile(subComponent.name);
            try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
                // Ecriture du fichier
                javaFile.writeTo(out);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Element getPureElement(Element element){
        String[] nameChain = element.getSimpleName().toString().split("_");
        for (int i = 0; i < nameChain.length - 1; i++) {
            element = getSuperElement(element);
        }
        return element;
    }

    public Element getSuperElement(Element element){
        return  ((DeclaredType)((TypeElement) element).getSuperclass()).asElement();
    }

    protected List<Element> getAnnotatedFieldsWith(Element element, Class<? extends Annotation> annotation){
        return getPureElement(element).getEnclosedElements().stream()
                .filter(element1 -> element1.getKind().isField())
                .filter(element1 -> element1.getAnnotation(annotation) != null)
                .collect(Collectors.toList());
    }

    public List<Element> getAnnotatedMethodsWith(Element element, Class<? extends Annotation> annotation){
        return getPureElement(element).getEnclosedElements().stream()
                .filter(element1 -> element1 instanceof ExecutableElement)
                .filter(element1 -> element1.getAnnotation(annotation) != null)
                .collect(Collectors.toList());
    }
}
