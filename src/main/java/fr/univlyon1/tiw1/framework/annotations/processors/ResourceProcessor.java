package fr.univlyon1.tiw1.framework.annotations.processors;

import com.squareup.javapoet.*;
import fr.univlyon1.tiw1.framework.annotations.COMPONENT_TYPE;
import fr.univlyon1.tiw1.framework.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.*;
import java.util.Set;

@SupportedAnnotationTypes("fr.univlyon1.tiw1.framework.annotations.Resource")
public class ResourceProcessor extends CustomProcessor {
    private static final Logger logger = LoggerFactory.getLogger(AbstractProcessor.class);

    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            logger.info("Annotation : " + annotation.getSimpleName());
            for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
                logger.info("Service annoté : " + element.toString());

                // Création d'un sous-composant
                TypeSpec subComponent = TypeSpec
                        .classBuilder(ClassName.bestGuess(element.toString() + "_Resource"))
                        .superclass(element.asType())
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(AnnotationSpec.builder(Component.class).addMember("type", "$T.$L", COMPONENT_TYPE.class, COMPONENT_TYPE.RESOURCE.name()).build())
                        .build();

                writeResult(element, subComponent);
            }
        }
        return true;
    }
}