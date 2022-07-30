package fr.univlyon1.tiw1.framework.annotations.processors;


import com.squareup.javapoet.*;
import fr.univlyon1.tiw1.framework.annotations.COMPONENT_TYPE;
import fr.univlyon1.tiw1.framework.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.util.Set;

@SupportedAnnotationTypes("fr.univlyon1.tiw1.framework.annotations.Business")
public class BusinessProcessor extends CustomProcessor {
    private static final Logger logger = LoggerFactory.getLogger(AbstractProcessor.class);

    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            logger.info("Annotation : " + annotation.getSimpleName());
            for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
                logger.info("Business annoté : " + element.toString());

                // Création d'un sous-composant
                TypeSpec subComponent = TypeSpec
                        .classBuilder(ClassName.bestGuess(element.toString() + "_Business"))
                        .superclass(element.asType())
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(AnnotationSpec.builder(Component.class).addMember("type", "$T.$L", COMPONENT_TYPE.class, COMPONENT_TYPE.BUSINESS.name()).build())
                        .build();


                writeResult(element, subComponent);
            }
        }
        return true;
    }
}