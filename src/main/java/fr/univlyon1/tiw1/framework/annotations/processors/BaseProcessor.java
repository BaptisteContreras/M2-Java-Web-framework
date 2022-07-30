package fr.univlyon1.tiw1.framework.annotations.processors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.javapoet.*;
import fr.univlyon1.tiw1.framework.annotations.*;
import fr.univlyon1.tiw1.framework.loader.ApplicationConfig;
import fr.univlyon1.tiw1.framework.loader.ComponentConfig;
import fr.univlyon1.tiw1.framework.loader.PropertiesLoader;
import fr.univlyon1.tiw1.framework.loader.builder.ControllerConfigBuilder;
import org.picocontainer.Disposable;
import org.picocontainer.Startable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.processing.*;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.StandardLocation;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;

import fr.univlyon1.tiw1.framework.context.LocateRegistry;

import java.util.*;
import java.util.stream.Collectors;

@SupportedAnnotationTypes("fr.univlyon1.tiw1.framework.annotations.Component")
public class BaseProcessor extends CustomProcessor {
    private static final Logger logger = LoggerFactory.getLogger(AbstractProcessor.class);
    private static final ApplicationConfig applicationConfig = new ApplicationConfig();
    private final String path = "config-framework.json";
    MethodSpec stopMethod;
    FieldSpec loggerField, annuaireField;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        this.stopMethod = MethodSpec
                .methodBuilder("stop")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addCode("logger.info(\"Composant \" + this.toString() + \" arrêté.\");")
                .build();

        this.loggerField = FieldSpec
                .builder(Logger.class, "logger")
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                .initializer("org.slf4j.LoggerFactory.getLogger(this.getClass())")
                .build();
        this.annuaireField = FieldSpec
                .builder(LocateRegistry.class, "annuaire")
                .addModifiers(Modifier.PROTECTED)
                .build();
    }

    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            logger.info("Annotation : " + annotation.getSimpleName());
            for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
                logger.info("Element annote : " + element.toString());

                Component objectAnnotation = element.getAnnotation(Component.class);
                // Création d'un sous-composant
                ClassName className = ClassName.bestGuess(element.toString() + "_Component");
                TypeSpec.Builder subComponentBuilder = TypeSpec
                        .classBuilder(className)
                        .superclass(element.asType())
                        .addSuperinterface(Startable.class)
                        .addSuperinterface(Disposable.class)
                        .addSuperinterface(PropertyChangeListener.class)
                        .addField(this.loggerField)
                        .addField(this.annuaireField)
                        .addModifiers(Modifier.PUBLIC)
                        .addMethod(this.stopMethod)
                        .addMethod(MethodSpec.constructorBuilder()
                                .addModifiers(Modifier.PUBLIC)
                                .addParameter(LocateRegistry.class, "annuaire")
                                .addStatement("this.annuaire = annuaire")
                                .addCode("logger.info(\"Composant \" + this.toString() + \" ANNURAIRE SET .\" + annuaire);")
                                .build())
                        .addMethod(writeStartMethod(element, objectAnnotation.type()))
                        .addMethod(writeDisposeMethod(element))
                        .addMethod(writePropertyChangeListenerMethod(element));

                TypeSpec typeSpec = subComponentBuilder.build();

                switch (objectAnnotation.type()) {
                    case CONTROLLER:
                        applicationConfig.addComponent(
                                ControllerConfigBuilder.build(
                                        this,
                                        getPureElement(element).getSimpleName().toString(),
                                        element.toString() + "_Component",
                                        element
                                ),
                                objectAnnotation.type()
                        );
                        break;
                    case PERSISTENCE:
                        Persistence persistence = getPureElement(element).getAnnotation(Persistence.class);
                        applicationConfig.addComponent(
                                new ComponentConfig(
                                        persistence.implementation(),
                                        element.toString() + "_Component"
                                ),
                                objectAnnotation.type());

                        break;
                    default:
                        applicationConfig.addComponent(
                                new ComponentConfig(
                                        getPureElement(element).getSimpleName().toString(),
                                        element.toString() + "_Component"
                                ),
                                objectAnnotation.type()
                        );
                }


                writeResult(element, typeSpec);

            }
        }
        if (!annotations.isEmpty()) {
            ObjectMapper objectMapper = new ObjectMapper();
            String inputStream = "";
            try {
                inputStream = objectMapper.writeValueAsString(applicationConfig);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            try {
                Writer writer = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", path).openWriter();
                writer.write(inputStream);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    private MethodSpec writeStartMethod(Element element, COMPONENT_TYPE component_type) {
        List<Element> listFields = getAnnotatedFieldsWith(element, Autowire.class);
        List<Element> listConfigs = getAnnotatedFieldsWith(element, Config.class);
        List<Element> listInits = getAnnotatedMethodsWith(element, Init.class);

        MethodSpec.Builder builder = MethodSpec
                .methodBuilder("start")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC);

        addConfigFields(listConfigs, builder);
        addAutoInjectedFields(listFields, builder, component_type);
        addInitMethods(listInits, builder);


        return builder.build();
    }



    private void addAutoInjectedFields(List<Element> listFields, MethodSpec.Builder builder, COMPONENT_TYPE component_type){
        if (!listFields.isEmpty()) {
            builder.addCode("try {\n");
        }

        for (Element field : listFields) {
            String[] nameSplit = field.asType().toString().split("\\.");
            String annuaireName = nameSplit[nameSplit.length-1];
            builder.addCode("\tthis." + field.getSimpleName())
                    .addCode(" = (" + getPureElement(field).asType().toString() + ")")
                    .addCode("annuaire.getComponentReference(\"" + annuaireName + "\", this, $T."+component_type.name()+");\n",  COMPONENT_TYPE.class)
            ;
        }
        if (!listFields.isEmpty()) {
            builder.addCode("} catch ( $T ignored) {\n\n}\n", Exception.class);
        }
    }

    private void addConfigFields(List<Element> listFields, MethodSpec.Builder builder){
        for (Element field : listFields) {
            builder.addCode("this." + field.getSimpleName())
                    .addCode(" = \"" + PropertiesLoader.load().getProperty(field.getAnnotation(Config.class).name())+"\";\n" )
            ;

        }
    }

    private void addInitMethods(List<Element> listMethods, MethodSpec.Builder builder){


         listMethods = listMethods.stream().sorted((o1, o2) -> {
            Init persistence1 = o1.getAnnotation(Init.class);
            Init persistence2 = o2.getAnnotation(Init.class);
            return persistence1.order() - persistence2.order();
        }).collect(Collectors.toList());

        if (!listMethods.isEmpty()) {
            builder.addCode("try {\n");
        }

        for (Element method : listMethods) {
            builder.addCode("\tthis." + method.getSimpleName()+"();\n");
        }
        if (!listMethods.isEmpty()) {
            builder.addCode("} catch ( $T ignored) {\n\n}\n", Exception.class);
        }
    }

    private MethodSpec writeDisposeMethod(Element element) {
        List<Element> listFields = getAnnotatedFieldsWith(element, Autowire.class);

        MethodSpec.Builder builder = MethodSpec
                .methodBuilder("dispose")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC);

        for (Element field : listFields) {
            builder.addCode("this." + field.getSimpleName() + " = null;\n");
            return builder.build();
        }
        return builder.build();
    }

    private MethodSpec writePropertyChangeListenerMethod(Element element) {
        List<Element> listFields = getAnnotatedFieldsWith(element, Autowire.class)
                .stream().filter(e -> e.getAnnotation(Autowire.class).autoUpdate())
                .collect(Collectors.toList());

        MethodSpec.Builder builder = MethodSpec
                .methodBuilder("propertyChange")
                .addAnnotation(Override.class)
                .addParameter(PropertyChangeEvent.class, "propertyChangeEvent")
                .addModifiers(Modifier.PUBLIC);

        for (Element field : listFields) {
            String[] split = field.asType().toString().split("\\.");
            builder.addCode("if (propertyChangeEvent.getPropertyName().equals($S)) {\n", split[split.length - 1])
                    .addCode("\tthis." + field.getSimpleName() + " = (")
                    .addCode(getPureElement(field).asType().toString() + ")")
                    .addCode("propertyChangeEvent.getNewValue();\n}\n");
        }

        addInitMethods(getAnnotatedMethodsWith(element, Init.class)
                .stream().filter(el -> el.getAnnotation(Init.class).withUpdate())
                .collect(Collectors.toList()), builder);

        return builder.build();
    }
}
