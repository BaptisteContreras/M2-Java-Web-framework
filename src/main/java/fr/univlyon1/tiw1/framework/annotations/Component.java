package fr.univlyon1.tiw1.framework.annotations;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface Component {
    COMPONENT_TYPE type() default COMPONENT_TYPE.BUSINESS;
}
