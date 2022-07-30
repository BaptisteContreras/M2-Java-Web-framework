package fr.univlyon1.tiw1.framework.annotations.processors;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import fr.univlyon1.tiw1.framework.annotations.App;
import fr.univlyon1.tiw1.framework.launcher.DefaultWebFrameworkApp;
import fr.univlyon1.tiw1.framework.picojetty.servers.JettyLauncher;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.beans.PropertyChangeEvent;
import java.util.Set;

@SupportedAnnotationTypes("fr.univlyon1.tiw1.framework.annotations.App")
public class AppProcessor extends CustomProcessor {
    private static final Logger logger = LoggerFactory.getLogger(AbstractProcessor.class);

    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
                logger.info("Create App");

                MethodSpec launch = MethodSpec
                        .methodBuilder("launch")
                        .addModifiers(Modifier.PUBLIC)
                        .addCode("$T.run();", JettyLauncher.class)
                        .build();

                MethodSpec defaultConstructor = MethodSpec
                        .constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .build();
                // Création d'un sous-composant
                TypeSpec subComponent = TypeSpec
                        .classBuilder(ClassName.bestGuess("WebFrameworkApp"))
                        .addModifiers(Modifier.PUBLIC)
                        .addSuperinterface(DefaultWebFrameworkApp.class)
                        .addMethod(defaultConstructor)
                        .addMethod(launch)
                        .build();




                writeResult(element, subComponent);

            }
        }

        // Après avoir construit le squelette du se rveur, on register les composants

        return true;
    }
}
