package fr.univlyon1.tiw1.framework.loader.builder;

import fr.univlyon1.tiw1.framework.annotations.Controller;
import fr.univlyon1.tiw1.framework.annotations.processors.CustomProcessor;
import fr.univlyon1.tiw1.framework.annotations.request.DELETE;
import fr.univlyon1.tiw1.framework.annotations.request.GET;
import fr.univlyon1.tiw1.framework.annotations.request.POST;
import fr.univlyon1.tiw1.framework.annotations.request.PUT;
import fr.univlyon1.tiw1.framework.loader.ControllerConfig;
import fr.univlyon1.tiw1.framework.loader.ParamConfig;
import fr.univlyon1.tiw1.framework.loader.RequestConfig;

import javax.lang.model.element.Element;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ControllerConfigBuilder {

    private ControllerConfigBuilder() {

    }

    private static Optional<RequestConfig> buildRequestConfig(CustomProcessor customProcessor,
                                                              Element element, Class<? extends Annotation> annotation) {

        String[] split = annotation.toString().split("\\.");
        return customProcessor.getAnnotatedMethodsWith(element, annotation).stream().map(
                e -> new RequestConfig(
                       split[split.length - 1].toLowerCase(),
                        e.toString().split("\\(")[0],
                        customProcessor.getPureElement(element).getSimpleName().toString()
                )
        ).findFirst();
    }

    private static List<RequestConfig> buildListRequest(CustomProcessor customProcessor,
                                                        Element element) {
        List<RequestConfig> list = new ArrayList<>();
        List<Class<? extends Annotation>> listAnnotation = List.of(
                GET.class,
                POST.class,
                PUT.class,
                DELETE.class
        );
        for (Class<? extends Annotation> annotation : listAnnotation) {
            Optional<RequestConfig> optional = buildRequestConfig(customProcessor, element, annotation);
            optional.ifPresent(list::add);
        }
        return list;
    }

    public static ControllerConfig build(
            CustomProcessor customProcessor, String name,
            String className, Element element) {

        return new ControllerConfig(
                name,
                className,
                customProcessor.getPureElement(element).getAnnotation(Controller.class).path(),
                buildListRequest(customProcessor, element)
        );
    }
}
