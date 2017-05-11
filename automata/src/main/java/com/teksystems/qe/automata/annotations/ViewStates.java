package com.teksystems.qe.automata.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ViewStates {
    String[] value();               //Used to map string states to this view
    long timeOut() default 0;       //Used to override application level page timeouts
}
